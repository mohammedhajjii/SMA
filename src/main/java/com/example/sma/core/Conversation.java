package com.example.sma.core;

import com.example.sma.core.enums.ConversationStatus;
import com.example.sma.core.enums.FailureReason;
import com.example.sma.core.exchange.*;
import jade.core.AID;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;



@Getter @Setter
public class Conversation {
    private final String CID;
    private final String bookName;
    private final int quantity;
    private final Instant creationInstant;
    private double bestUnitPrice;
    private AID bestSeller;
    private long sellersCount;
    private long receivedProposalCount;
    private ConversationStatus status;

    public Conversation(RequestForPurchase request) {
        this.CID = UUID.randomUUID().toString();
        this.bookName = request.bookName();
        this.quantity = request.quantity();
        this.creationInstant = Instant.now();
        this.bestUnitPrice = Double.MAX_VALUE;
        this.bestSeller = null;
        this.sellersCount = 0;
        this.receivedProposalCount = 0;
        this.status = ConversationStatus.ASK_FOR_PRICE;
    }

    public void newReceivedProposal(){
        this.receivedProposalCount += 1;
    }

    public void sellerRefused(){
        this.sellersCount -= 1;
    }

    public RequestForQuantity createRequestForQuantity(){
        status = ConversationStatus.ASK_FOR_QUANTITY;
        return new RequestForQuantity(CID, bookName, quantity);
    }

    public ResponseForPurchase finish(){
        this.status = ConversationStatus.SUCCEEDED;
        return new ResponseForPurchase(
                CID,
                bookName,
                quantity,
                bestSeller.getLocalName(),
                bestUnitPrice,
                bestUnitPrice * quantity
        );
    }

    public FailureForPurchase failed(FailureReason reason){
        status = ConversationStatus.FAILED;
        return new FailureForPurchase(CID, bookName,reason);
    }

    public boolean isExpired(){
        return Instant.now().isAfter(creationInstant.plusSeconds(5));
    }

    public boolean hasAskedForPrice(){
        return status == ConversationStatus.ASK_FOR_PRICE;
    }


    public boolean hasBestSeller(){
        return bestSeller != null;
    }

    public boolean allSellersProposed(){
        return sellersCount == receivedProposalCount;
    }

}
