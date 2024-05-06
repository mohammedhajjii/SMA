package com.example.sma.core.agents;

import com.example.sma.core.AgentImage;
import com.example.sma.core.BookSellingService;
import com.example.sma.core.behaviours.SellersDiscoveryBehaviour;
import com.example.sma.core.behaviours.BuyerBehaviour;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@AgentImage
@AllArgsConstructor
public class BuyerAgent extends Agent {

    private final ObservableList<String> observableLogList;
    private final ObjectMapper objectMapper;


    @Override
    protected void setup() {


        var initiatorBehaviour = new BuyerBehaviour(this, observableLogList, objectMapper);

        var discoveryBehaviour = new SellersDiscoveryBehaviour(this, 5000);

        var parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(initiatorBehaviour);
        parallelBehaviour.addSubBehaviour(discoveryBehaviour);
        addBehaviour(parallelBehaviour);
    }

    @Override
    protected void takeDown() {
        BookSellingService.deregister(this);
    }
}
