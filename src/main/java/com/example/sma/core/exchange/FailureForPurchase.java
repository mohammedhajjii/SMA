package com.example.sma.core.exchange;

import com.example.sma.core.enums.FailureReason;

public record FailureForPurchase(String CID, String bookName, FailureReason reason) {
}
