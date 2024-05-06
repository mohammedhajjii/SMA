package com.example.sma.core.behaviours;

import com.example.sma.core.exchange.FailureForPurchase;
import com.example.sma.core.MessageTemplates;
import com.example.sma.core.exchange.ResponseForPurchase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import lombok.SneakyThrows;

public class ConsumerBehaviour extends CyclicBehaviour {


    private final MessageTemplate messageTemplate;
    private final ObservableList<String> observableResultList;
    private final ObjectMapper objectMapper;

    public ConsumerBehaviour(Agent a,ObservableList<String> observableResultList, ObjectMapper objectMapper) {
        super(a);
        this.messageTemplate = MessageTemplates.consumerMessageTemplate();
        this.observableResultList = observableResultList;
        this.objectMapper = objectMapper;
    }

    @Override
    public void action() {
        ACLMessage receivedMsg = myAgent.receive(messageTemplate);
        if (receivedMsg != null){
            if (receivedMsg.getPerformative() == ACLMessage.INFORM)
                handleInform(receivedMsg);
            else
                handleFailure(receivedMsg);
        }
        else block();
    }

    @SneakyThrows({JsonProcessingException.class})
    public void handleInform(ACLMessage inform)  {
        ResponseForPurchase response = objectMapper.readValue(inform.getContent(), ResponseForPurchase.class);
        Platform.runLater(() -> {
            observableResultList.add("Book purchase completed: ");
            observableResultList.add("CID: " + response.CID());
            observableResultList.add("Book: " + response.bookName());
            observableResultList.add("Quantity: " + response.quantity());
            observableResultList.add("Seller: " + response.seller());
            observableResultList.add("Unit price: " + response.unitPrice());
            observableResultList.add("Total price: " + response.totalPrice());
            observableResultList.add("----------------------------END----------------------------------");
        });
    }

    @SneakyThrows({JsonProcessingException.class})
    public void handleFailure(ACLMessage failure){
        FailureForPurchase failureForPurchase = objectMapper.readValue(failure.getContent(), FailureForPurchase.class);
        Platform.runLater(() -> {
            observableResultList.add("Book purchase failed: ");
            observableResultList.add("CID: " + failureForPurchase.CID());
            observableResultList.add("Book: " + failureForPurchase.bookName());
            observableResultList.add("Reason: " + failureForPurchase.reason().getReasonAsString());
            observableResultList.add("----------------------------END----------------------------------");
        });
    }
}
