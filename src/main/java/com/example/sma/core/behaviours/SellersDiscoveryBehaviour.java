package com.example.sma.core.behaviours;

import com.example.sma.core.BookSellingService;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class SellersDiscoveryBehaviour extends TickerBehaviour {

    public SellersDiscoveryBehaviour(Agent agent, long period) {
        super(agent, period);
    }

    @Override
    protected void onTick() {
        BookSellingService.discoverNewSellers(myAgent);
    }
}
