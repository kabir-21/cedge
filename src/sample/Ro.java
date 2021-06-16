package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

abstract class Ro extends Module{
    protected HashMap<String,Integer> roMap = new HashMap<>();
    protected ObservableList<String> roList;
    @FXML
    protected ComboBox<String> ro;
    @Override @FXML
    protected void setModule(ActionEvent event) {
        modMap = new HashMap<>();
        if(network.getValue()!=null){
            modList = FXCollections.observableArrayList();
            SqlQuery q2 = new SqlQuery();
            try {
                q2.setQuery("select * from modules where network_id = "+netMap.get(network.getValue()));
                ResultSet set = q2.sql();
                while (set.next()){
                    modMap.put(set.getString(2),set.getInt(1));
                    modList.add(set.getString(2));
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            module.setItems(modList);
        }
    }
    @FXML
    protected void setRO(ActionEvent event) {
        roMap = new HashMap<>();
        if(module.getValue()!=null){
            roList = FXCollections.observableArrayList();
            SqlQuery q2 = new SqlQuery();
            try {
                q2.setQuery("select * from ro where module_id = "+modMap.get(module.getValue()));
                ResultSet set = q2.sql();
                while (set.next()){
                    roMap.put(set.getString(2),set.getInt(1));
                    roList.add(set.getString(2));
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            ObservableList<String> withAll = FXCollections.observableArrayList();
            withAll.addAll(roList); withAll.add("All ROs");
            ro.setItems(withAll);
        }
    }
}
