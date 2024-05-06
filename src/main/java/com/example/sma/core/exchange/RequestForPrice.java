package com.example.sma.core.exchange;


import lombok.AllArgsConstructor;
import lombok.Getter;


public record RequestForPrice(String CID, String bookName) {
}
