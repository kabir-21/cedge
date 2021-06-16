package sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

abstract class Module extends Network{
    protected HashMap<String, Integer> modMap = new HashMap<>();
    protected ObservableList<String> modList;
    @FXML
    protected ComboBox<String> module;
    @FXML
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
            ObservableList<String> withAll = FXCollections.observableArrayList();
            withAll.addAll(modList); withAll.add("All Modules");
            module.setItems(withAll);
        }
    }
    @Override @FXML
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
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            network.setItems(netList);
        }
    }
}
