package com.example.sma.core.behaviours;

import com.example.sma.core.*;
import com.example.sma.core.enums.FailureReason;
import com.example.sma.core.exchange.*;
import com.example.sma.modelsandrepositories.Book;
import com.example.sma.modelsandrepositories.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SellerBehaviour extends CyclicBehaviour {

    private final BookRepository bookRepository;
    private final MessageTemplate messageTemplate;
    private final ObjectMapper objectMapper;

    public SellerBehaviour(Agent agent, BookRepository bookRepository, ObjectMapper objectMapper) {
        super(agent);
        this.bookRepository = bookRepository;
        this.messageTemplate = MessageTemplates.responderMessageTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public void action() {

        ACLMessage received = myAgent.receive(messageTemplate);
        if (received != null){
            switch (received.getPerformative()){
                case ACLMessage.CFP -> handleCfp(received);
                case ACLMessage.ACCEPT_PROPOSAL ->  handleAcceptProposal(received);
                case ACLMessage.REJECT_PROPOSAL -> handleRejectProposal(received);
            }
        }
        else block();

    }

    @SneakyThrows({JsonProcessingException.class})
    public void handleCfp(ACLMessage cfp) {
        RequestForPrice request = objectMapper.readValue(cfp.getContent(), RequestForPrice.class);
        Book book = bookRepository.findByBookNameAndSellerName(request.bookName(), myAgent.getLocalName());

        ACLMessage propose = cfp.createReply(ACLMessage.PROPOSE);


        ResponseForPrice response = (book == null) ?
                new ResponseForPrice(request.CID(), Double.MAX_VALUE):
                new ResponseForPrice(request.CID(), book.getPrice());


        propose.setContent(objectMapper.writeValueAsString(response));
        myAgent.send(propose);
    }

    @SneakyThrows({JsonProcessingException.class})
    public void handleAcceptProposal(ACLMessage accept) {

        RequestForQuantity request = objectMapper.readValue(accept.getContent(), RequestForQuantity.class);
        Book book = bookRepository.findByBookNameAndSellerName(request.bookName(), myAgent.getLocalName());

        ACLMessage reply = accept.createReply();

        if (book.getQuantity() >= request.quantity()){
            reply.setPerformative(ACLMessage.AGREE);
            reply.setContent(request.CID());
            book.setQuantity(book.getQuantity() - request.quantity());
            bookRepository.save(book);
        }
        else {
            reply.setPerformative(ACLMessage.FAILURE);
            FailureForPurchase failure = new FailureForPurchase(
                    request.CID(),
                    request.bookName(),
                    FailureReason.INSUFFISANT_QUANTITY
            );
            reply.setContent(objectMapper.writeValueAsString(failure));
        }

        myAgent.send(reply);
    }

    public void handleRejectProposal(ACLMessage reject){
        log.info(String.format("%s receive reject for proposal", myAgent.getLocalName()));
    }
}
