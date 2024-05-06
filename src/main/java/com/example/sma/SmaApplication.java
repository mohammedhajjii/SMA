package com.example.sma;

import com.example.sma.core.agents.BuyerAgent;
import com.example.sma.core.agents.ConsumerAgent;
import com.example.sma.core.agents.SellerAgent;
import com.example.sma.core.containers.Containers;
import com.example.sma.core.enums.AgentName;
import com.example.sma.modelsandrepositories.Book;
import com.example.sma.modelsandrepositories.BookRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class SmaApplication extends Application {


    private ConfigurableApplicationContext configuration;
    private Parent parent;

    public static void main(String[] args) {
       Application.launch(SmaApplication.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(parent, 800, 600));
        stage.setTitle("BOOK-SELLING-PLATFORM");
        stage.show();
    }

    @Override
    public void init() throws Exception {
        configuration = new SpringApplicationBuilder(SmaApplication.class)
                .headless(false)
                .run();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/book-selling-service.fxml"));
        loader.setControllerFactory(configuration::getBean);
        parent = loader.load();


    }

    @Override
    public void stop() throws Exception {
        this.configuration.close();
    }



    @Bean
    CommandLineRunner start(BuyerAgent buyerAgent,
                            SellerAgent sellerOneAgent,
                            SellerAgent sellerTwoAgent,
                            ConsumerAgent consumerAgent,
                            BookRepository bookRepository){
        return args -> {



            bookRepository.save(
                    Book.builder()
                            .bookName("Java")
                            .price(120.0)
                            .quantity(8)
                            .sellerName(AgentName.SELLER_AGENT_1.getName())
                            .build()
            );

            bookRepository.save(
                    Book.builder()
                            .bookName("Java")
                            .price(110.0)
                            .quantity(2)
                            .sellerName(AgentName.SELLER_AGENT_2.getName())
                            .build()
            );

            bookRepository.save(
                    Book.builder()
                            .bookName("C++")
                            .price(150.0)
                            .quantity(5)
                            .sellerName(AgentName.SELLER_AGENT_1.getName())
                            .build()
            );


            Containers.startMainContainer();
            Containers.containerize(buyerAgent, AgentName.BUYER_AGENT);
            Containers.containerize(sellerOneAgent, AgentName.SELLER_AGENT_1);
            Containers.containerize(sellerTwoAgent, AgentName.SELLER_AGENT_2);
            Containers.containerize(consumerAgent, AgentName.CONSUMER_AGENT);

        };
    }

}





