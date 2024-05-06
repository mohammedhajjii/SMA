package com.example.sma.core.enums;

public enum FailureReason {
    BOOK_NOT_FOUND {
        @Override
        public String getReasonAsString() {
            return "Book not found";
        }
    }, NO_SELLER_FOUNDED {
        @Override
        public String getReasonAsString() {
            return "No seller is present";
        }
    }, INSUFFISANT_QUANTITY {
        @Override
        public String getReasonAsString() {
            return "book quantity is not suffisant for request";
        }
    };


    public abstract String getReasonAsString();
}
