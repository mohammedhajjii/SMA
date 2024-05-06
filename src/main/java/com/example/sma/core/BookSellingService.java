package com.example.sma.core;

import com.example.sma.core.enums.AgentName;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.leap.Iterator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class BookSellingService {

    public static Set<AID> sellerSet = new ConcurrentSkipListSet<>();
    public static final String SERVICE_TYPE = "JADE-book-trading";
    public static final String SERVICE_NAME = "book-selling";
    public static final ServiceDescription SERVICE_DESCRIPTION;
    public static final AID CONSUMER_AID;
    public static final AID BUYER_AID;

    static {
        SERVICE_DESCRIPTION = new ServiceDescription();
        SERVICE_DESCRIPTION.setType(SERVICE_TYPE);
        SERVICE_DESCRIPTION.setName(SERVICE_NAME);
        CONSUMER_AID = new AID(AgentName.CONSUMER_AGENT.getName(), AID.ISLOCALNAME);
        BUYER_AID = new AID(AgentName.BUYER_AGENT.getName(), AID.ISLOCALNAME);
    }

    @SneakyThrows({FIPAException.class})
    public static void register(Agent agent) {
        DFAgentDescription description = new DFAgentDescription();
        description.setName(agent.getAID());
        description.addServices(SERVICE_DESCRIPTION);
        DFService.register(agent, description);
    }


    @SneakyThrows({FIPAException.class})
    public static void deregister(Agent agent) {
        DFAgentDescription description = new DFAgentDescription();
        description.setName(agent.getAID());
        description.addServices(SERVICE_DESCRIPTION);
        DFService.deregister(agent, description);
    }

    @SneakyThrows({FIPAException.class})
    public static void discoverNewSellers(Agent agent){

        DFAgentDescription description = new DFAgentDescription();
        description.addServices(SERVICE_DESCRIPTION);

        SearchConstraints constraints = new SearchConstraints();
        constraints.setMaxResults(-1L);

        DFAgentDescription[] search = DFService.search(agent, description, constraints);
        sellerSet = Arrays.stream(search)
                .<AID>mapMulti((df, stream) -> {
                    Iterator allServices = df.getAllServices();
                    Stream.Builder<Object> builder = Stream.builder();

                    while (allServices.hasNext())
                        builder.accept(allServices.next());

                    boolean bookServiceIsPresentAsService = builder.build()
                            .anyMatch(serDes -> serDes instanceof ServiceDescription service &&
                                    service.getType().equals(SERVICE_TYPE));

                    if(bookServiceIsPresentAsService)
                        stream.accept(df.getName());

                }).collect(Collectors.toSet());
    }
}
