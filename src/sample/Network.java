package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

abstract class Network extends Circle {
    protected HashMap<String,Integer> netMap = new HashMap<>();
    protected ObservableList<String> netList;
    @FXML
    protected ComboBox<String> network;
    @FXML
    protected void setNetwork(ActionEvent event) {
        netMap = new HashMap<>();
        if(circle.getValue()!=null){
            netList = FXCollections.observableArrayList();
            SqlQuery q2 = new SqlQuery();
            try {
                q2.setQuery("select * from networks where circle_id = "+Main.circleMap.get(circle.getValue()));
                ResultSet set = q2.sql();
                while (set.next()){
                    netMap.put(set.getString(2),set.getInt(1));
                    netList.add(set.getString(2));
                }
                System.out.println(netMap);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            ObservableList<String> withAll = FXCollections.observableArrayList();
            withAll.addAll(netList); withAll.add("All Networks");
            network.setItems(withAll);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        circle.setItems(Main.circlesList);
    }
}
