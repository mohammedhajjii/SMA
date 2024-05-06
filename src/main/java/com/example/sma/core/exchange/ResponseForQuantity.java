package com.example.sma.core.exchange;


import com.example.sma.core.enums.FailureReason;

public record ResponseForQuantity(
        String CID,
        FailureReason quantity) {
}
