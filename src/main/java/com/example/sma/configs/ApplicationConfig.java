package com.example.sma.configs;

import com.example.sma.core.AgentImage;
import com.example.sma.core.agents.SellerAgent;
import com.example.sma.modelsandrepositories.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {


    @AgentImage
    public SellerAgent sellerOneAgent(BookRepository bookRepository, ObjectMapper objectMapper){
        return new SellerAgent(bookRepository, objectMapper);
    }


    @AgentImage
    public SellerAgent sellerTwoAgent(BookRepository bookRepository, ObjectMapper objectMapper){
        return new SellerAgent(bookRepository, objectMapper);
    }


    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public ObservableList<String> observableResultList(){
        return FXCollections.observableArrayList();
    }

    @Bean
    public ObservableList<String> observableLogList(){
        return FXCollections.observableArrayList();
    }


}
