package com.example.sma.fxcontrollers;

import com.example.sma.core.agents.ConsumerAgent;
import com.example.sma.core.exchange.RequestForPurchase;
import jade.gui.GuiEvent;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Slf4j
public class ViewController implements Initializable {

    @FXML  private Spinner<Integer> quantity;
    @FXML private ListView<String> resultList;
    @FXML private ListView<String> logList;
    @FXML private TextField bookName;


    private final ObservableList<String> observableResultList;
    private final ObservableList<String> observableLogList;
    private final ConsumerAgent consumerAgent;

    public ViewController(ObservableList<String> observableResultList, ObservableList<String> observableLogList, ConsumerAgent consumerAgent) {
        this.observableResultList = observableResultList;
        this.observableLogList = observableLogList;
        this.consumerAgent = consumerAgent;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resultList.setItems(observableResultList);
        logList.setItems(observableLogList);
        quantity.getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }


    /**
     * this method will be called when BUY button clicked:
     */
    public void searchForBook(){
        String bookNameValue = bookName.getText();
        if (!bookNameValue.isBlank()){
            GuiEvent clickEvent = new GuiEvent(this, 1);

            RequestForPurchase request = new RequestForPurchase(bookNameValue, quantity.getValue());

            clickEvent.addParameter(request);
            consumerAgent.postGuiEvent(clickEvent);
        }
    }
}
