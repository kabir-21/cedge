package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

abstract class Bank implements Initializable {
    protected ObservableList<String> bankList = FXCollections.observableArrayList();
    protected DateTimeFormatter myDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @FXML
    protected Button summary,report;
    @FXML
    protected ComboBox<String> bank;
    @FXML
    protected DatePicker from,to;
    @FXML
    protected void getSummary(ActionEvent event) throws SQLException, ClassNotFoundException, IOException { }
    @FXML
    protected void getReport(ActionEvent event) throws IOException, SQLException, ClassNotFoundException { }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bankList.add("Bank1");
        bank.setItems(bankList);
    }
}
