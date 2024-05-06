package com.example.sma.core.agents;

import com.example.sma.core.AgentImage;
import com.example.sma.core.BookSellingService;
import com.example.sma.core.exchange.RequestForPurchase;
import com.example.sma.core.behaviours.ConsumerBehaviour;
import com.example.sma.core.enums.AgentName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import javafx.collections.ObservableList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AgentImage
public class ConsumerAgent extends GuiAgent {

    private final ObjectMapper objectMapper;
    private final ObservableList<String> observableResultList;

    public ConsumerAgent(ObservableList<String> observableResultList, ObjectMapper objectMapper) {
        this.objectMapper  = objectMapper;
        this.observableResultList = observableResultList;
    }

    @Override
    protected void setup() {
        addBehaviour(new ConsumerBehaviour(this, observableResultList, objectMapper));
    }

    @Override
    @SneakyThrows({JsonProcessingException.class})
    protected void onGuiEvent(GuiEvent guiEvent) {
        if (guiEvent.getType() == 1){
            if (guiEvent.getParameter(0) instanceof RequestForPurchase purchaseRequest){
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(BookSellingService.BUYER_AID);
                request.setContent(objectMapper.writeValueAsString(purchaseRequest));
                send(request);
            }
        }
    }

}
