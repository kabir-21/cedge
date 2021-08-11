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
import java.util.HashMap;
import java.util.ResourceBundle;

abstract class Circle extends Bank{//implements Initializable {
    protected ObservableList<String> circlesList = FXCollections.observableArrayList();
    protected HashMap<String,Integer> map = new HashMap<>();
    @FXML
    protected ComboBox<String> circle;
    @FXML
    protected void getSummary(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {}
    @FXML
    protected void getReport(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {}
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> withAll = FXCollections.observableArrayList();
        for(int i=1;i<=Main.circlesList.size();i++){
            map.put(Main.circlesList.get(i-1),i);
            withAll.add(Main.circlesList.get(i-1));
        }
        withAll.add("All Circles");
        circle.setItems(withAll);
    }
}
