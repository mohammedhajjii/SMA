package com.example.sma.core.behaviours;

import com.example.sma.core.BookSellingService;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class SellerServiceRegistrationBehaviour extends OneShotBehaviour {

    public SellerServiceRegistrationBehaviour(Agent agent) {
        super(agent);

    }

    @Override
    public void action() {
        BookSellingService.register(myAgent);
    }

}
