package com.example.sma.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum AgentName {
    CONSUMER_AGENT("CONSUMER"){},
    BUYER_AGENT("BUYER"){},
    SELLER_AGENT_1("SELLER_1"){},
    SELLER_AGENT_2("SELLER_2"){};

    private final String name;
}
