package sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

abstract class Branch extends Ro {
    protected HashMap<String,Integer> branchMap = new HashMap<>();
    protected ObservableList<String> branchList;
    @FXML
    protected ComboBox<String> branch;
    @Override
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
            ro.setItems(roList);
        }
    }
    @FXML
    protected void setBranch(ActionEvent event) {
        branchMap = new HashMap<>();
        if(ro.getValue()!=null){
            branchList = FXCollections.observableArrayList();
            SqlQuery q2 = new SqlQuery();
            try {
                q2.setQuery("select * from branches where ro_id = "+roMap.get(ro.getValue()));
                ResultSet set = q2.sql();
                while (set.next()){
                    branchMap.put(set.getString(2),set.getInt(1));
                    branchList.add(set.getString(2));
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            ObservableList<String> withAll = FXCollections.observableArrayList();
            withAll.addAll(branchList); withAll.add("All Branches");
            branch.setItems(withAll);
        }
    }
}
