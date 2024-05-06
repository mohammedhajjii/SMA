package com.example.sma.core;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MessageTemplates {

    public static MessageTemplate initiatorMessageTemplate(){
        return MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.REFUSE),
                                MessageTemplate.or(
                                        MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                                        MessageTemplate.MatchPerformative(ACLMessage.FAILURE)
                                )
                        )
                )
        );
    }

    public static MessageTemplate responderMessageTemplate(){
        return MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.CFP),
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                        MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL)
                )
        );
    }

    public static MessageTemplate consumerMessageTemplate(){
        return MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchPerformative(ACLMessage.FAILURE)
        );
    }
}
