package com.example.sma.core.agents;

import com.example.sma.core.BookSellingService;
import com.example.sma.core.behaviours.SellerServiceRegistrationBehaviour;
import com.example.sma.core.behaviours.SellerBehaviour;
import com.example.sma.modelsandrepositories.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SellerAgent extends Agent {

    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void setup() {

        var responderBehaviour = new SellerBehaviour(this, bookRepository, objectMapper);
        var publisherBehaviour = new SellerServiceRegistrationBehaviour(this);


        var parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(responderBehaviour);
        parallelBehaviour.addSubBehaviour(publisherBehaviour);

        addBehaviour(parallelBehaviour);



    }

    @Override
    protected void takeDown() {
        BookSellingService.deregister(this);
    }


}
