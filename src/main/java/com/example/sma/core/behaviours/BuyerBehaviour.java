package com.example.sma.core.behaviours;

import com.example.sma.core.*;
import com.example.sma.core.enums.FailureReason;
import com.example.sma.core.exchange.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Set;


@Slf4j
public class BuyerBehaviour extends CyclicBehaviour {

    private final HashMap<String, Conversation> currentConversations;
    private final MessageTemplate messageTemplate;
    private final ObjectMapper objectMapper;

    private final ObservableList<String> observableLogList;
    public BuyerBehaviour(Agent agent, ObservableList<String> observableLogList, ObjectMapper objectMapper) {
        super(agent);
        this.observableLogList = observableLogList;
        this.currentConversations = new HashMap<>();
        this.messageTemplate = MessageTemplates.initiatorMessageTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public void action() {

        ACLMessage received = myAgent.receive(messageTemplate);
        if (received != null){
            switch (received.getPerformative()){
                case ACLMessage.REQUEST -> handleRequest(received);
                case ACLMessage.PROPOSE -> handlePropose(received);
                case ACLMessage.REFUSE -> handleRefuse(received);
                case ACLMessage.AGREE -> handleAgree(received);
                case ACLMessage.FAILURE -> handleFailure(received);
            }
        }
        else block();
    }


    @SneakyThrows({JsonProcessingException.class})
    public void handlePropose(ACLMessage propose) {

        var responseForPrice = objectMapper.readValue(propose.getContent(), ResponseForPrice.class);
        var conversation = currentConversations.get(responseForPrice.CID());
        conversation.newReceivedProposal();

        Platform.runLater(() ->
                observableLogList.add("new propose received from"
                        + propose.getSender().getLocalName()
                        +" for CID: "
                        + responseForPrice.CID()
                        +" with price: " + responseForPrice.price()));

        if (responseForPrice.price() < conversation.getBestUnitPrice()){
            conversation.setBestUnitPrice(responseForPrice.price());
            sendRejectProposal(conversation.getBestSeller());
            conversation.setBestSeller(propose.getSender());
        }
        else sendRejectProposal(propose.getSender());


        if (conversation.allSellersProposed()){

            if (conversation.hasBestSeller()){
                var request = conversation.createRequestForQuantity();
                sendAcceptProposal(conversation.getBestSeller(), request);
                Platform.runLater(() -> observableLogList.add("sending accept proposal to: " + conversation.getBestSeller().getLocalName()));
                return;
            }
            Platform.runLater(() -> observableLogList.add("book: " + conversation.getBookName() + " not found"));
            sendFailureToConsumer(conversation.getCID(), FailureReason.BOOK_NOT_FOUND);
        }

    }
    public void handleRefuse(ACLMessage refuse){
        String CID = refuse.getContent();
        currentConversations.get(CID).sellerRefused();
    }

    @SneakyThrows({JsonProcessingException.class})
    public void handleAgree(ACLMessage agree){

        var conversation = currentConversations.get(agree.getContent());
        ResponseForPurchase response = conversation.finish();

        Platform.runLater(() ->  observableLogList.add("receiving agree from seller: "
                + agree.getSender().getLocalName()
                + " for CID: " + agree.getContent()));

        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
        inform.addReceiver(BookSellingService.CONSUMER_AID);
        inform.setContent(objectMapper.writeValueAsString(response));
        myAgent.send(inform);
    }

    @SneakyThrows({JsonProcessingException.class})
    public void handleFailure(ACLMessage failure) {
        if (failure.getSender().getLocalName().equals("ams"))
            return;

        var failureDetails = objectMapper.readValue(failure.getContent(), FailureForPurchase.class);

        Platform.runLater(() ->  observableLogList.add("receiving failure from: " + failure.getSender().getLocalName()
                + " for CID: " + failureDetails.CID() + " Reason: "
                + failureDetails.reason().getReasonAsString()));

        failure.clearAllReceiver();
        failure.addReceiver(BookSellingService.CONSUMER_AID);
        myAgent.send(failure);
    }

    @SneakyThrows({JsonProcessingException.class})
    public void handleRequest(ACLMessage request) {


        RequestForPurchase purchase = objectMapper.readValue(request.getContent(), RequestForPurchase.class);
        Conversation conversation = new Conversation(purchase);
        currentConversations.put(conversation.getCID(), conversation);


        Platform.runLater(() -> {
            observableLogList.add("new purchase request has created:");
            observableLogList.add("CID: " + conversation.getCID() + " for book: " + purchase.bookName());
        });
        int sellerCount = BookSellingService.sellerSet.size();
        if (sellerCount > 0){
            Platform.runLater(() -> observableLogList.add("sending CFP to all sellers"));
            var requestForPrice = new RequestForPrice(conversation.getCID(), purchase.bookName());
            broadCast(requestForPrice);
            conversation.setSellersCount(sellerCount);
            return;
        }

        Platform.runLater(() -> observableLogList.add("no seller has been founded"));
        sendFailureToConsumer(conversation.getCID(), FailureReason.NO_SELLER_FOUNDED);
    }


    private void sendRejectProposal(AID to){
        ACLMessage reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
        reject.addReceiver(to);
        myAgent.send(reject);
    }

    @SneakyThrows({JsonProcessingException.class})
    private void sendAcceptProposal(AID to, RequestForQuantity request){
        ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        accept.addReceiver(to);
        accept.setContent(objectMapper.writeValueAsString(request));
        myAgent.send(accept);
    }

    @SneakyThrows({JsonProcessingException.class})
    private void sendFailureToConsumer(String CID, FailureReason reason){
        FailureForPurchase failureForPurchase = currentConversations.get(CID).failed(reason);

        ACLMessage failure = new ACLMessage(ACLMessage.FAILURE);
        failure.addReceiver(BookSellingService.CONSUMER_AID);
        failure.setContent(objectMapper.writeValueAsString(failureForPurchase));
        myAgent.send(failure);
    }

    @SneakyThrows({JsonProcessingException.class})
    private void broadCast(RequestForPrice request){
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        BookSellingService.sellerSet.forEach(cfp::addReceiver);
        cfp.setContent(objectMapper.writeValueAsString(request));
        myAgent.send(cfp);
    }

    @Override
    protected void handleBlockEvent() {
        currentConversations.values()
                .stream()
                .filter(conv -> conv.hasAskedForPrice() && conv.isExpired())
                .forEach(conv -> {

                    if (conv.hasBestSeller()){
                        sendAcceptProposal(conv.getBestSeller(), conv.createRequestForQuantity());
                        Platform.runLater(() -> {
                            observableLogList.add("timeout expired for conversation: " + conv.getCID());
                            observableLogList.add("sending accept propose to: "
                                    + conv.getBestSeller().getLocalName()
                                    + " for CID: " + conv.getCID());
                        });
                    }
                    else {
                        Platform.runLater(() -> {
                            observableLogList.add("timeout expired for conversation: " + conv.getCID());
                            observableLogList.add("book: " + conv.getBookName() + " not found");
                        });
                       sendFailureToConsumer(conv.getCID(), FailureReason.BOOK_NOT_FOUND);
                    }

                });
    }



}
