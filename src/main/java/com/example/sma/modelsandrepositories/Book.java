package com.example.sma.modelsandrepositories;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor @NoArgsConstructor @Builder
@Entity
public class Book{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bookName;
    private double price;
    private int quantity;
    private String sellerName;
}
