package com.example.sma.core.exchange;

public record ResponseForPurchase(
        String CID,
        String bookName,
        int quantity,
        String seller,
        double unitPrice,
        double totalPrice
){}
