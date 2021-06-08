package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Ro implements Initializable {
    HashMap<String,Integer> netMap = new HashMap<>();
    ObservableList<String> netList;
    HashMap<String, Integer> modMap = new HashMap<>();
    ObservableList<String> modList;
    HashMap<String,Integer> roMap = new HashMap<>();
    ObservableList<String> roList;
    DateTimeFormatter myDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    @FXML
    private ComboBox<String> circle,network,module,ro;
    @FXML
    private DatePicker from,to;
    @FXML
    private Button summary,report;

    @FXML
    void setNetwork(ActionEvent event) {
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
//                System.out.println(netMap);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            network.setItems(netList);
        }
    }

    @FXML
    void setModule(ActionEvent event) {
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
    void setRO(ActionEvent event) {
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

    @FXML
    void getSummary(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if (circle.getValue() == null || fromDate == null || toDate == null || fromDate.isAfter(toDate) || network.getValue() == null
                || module.getValue() == null || ro.getValue() == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else if(ro.getValue().equals("All ROs")){
            ObservableList<AccountSummary> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            for(String s:roList){
                String query = "select opening_date, count(*) from accounts where branch_id in\n" +
                        "    (select branch_id from branches where ro_id in\n" +
                        "        (select ro_id from ro where module_id = " + modMap.get(s) + "))\n" +
                        "and opening_date between '" + myDateFormat.format(fromDate) + "' and '" + myDateFormat.format(toDate)+"' group by opening_date";
                System.out.println(query);
                q.setQuery(query);
                ResultSet rs = q.sql();
                while (rs.next()){
                    AccountSummary a = new AccountSummary(rs.getString("OPENING_DATE").substring(0,10),rs.getInt("COUNT(*)"));
                    accounts.add(a);
                }
            }
            SummaryController summaryController = new SummaryController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("summary.fxml"));
            fxmlLoader.setController(summaryController);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(module.getValue()+" "+ro.getValue()+" Summary");
            stage.setScene(new Scene(root1,425,712));
            stage.show();
        }
        else{
            ObservableList<AccountSummary> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            String query = "select opening_date, count(*) from accounts where branch_id in\n" +
                    "    (select branch_id from branches where ro_id in\n" +
                    "        (select ro_id from ro where module_id = " + modMap.get(module.getValue()) + "))\n" +
                    "and opening_date between '" + myDateFormat.format(fromDate) + "' and '" + myDateFormat.format(toDate)+"' group by opening_date";
            System.out.println(query);
            q.setQuery(query);
            ResultSet rs = q.sql();
            while (rs.next()){
                AccountSummary a = new AccountSummary(rs.getString("OPENING_DATE").substring(0,10),rs.getInt("COUNT(*)"));
                accounts.add(a);
            }
            SummaryController summaryController = new SummaryController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("summary.fxml"));
            fxmlLoader.setController(summaryController);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(ro.getValue()+" Summary");
            stage.setScene(new Scene(root1,425,712));
            stage.show();
        }
    }
    @FXML
    void getReport(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if (circle.getValue() == null || fromDate == null || toDate == null || fromDate.isAfter(toDate) || network.getValue() == null
                || module.getValue() == null || ro.getValue() == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else if(ro.getValue().equals("All Ros")) {
            ObservableList<Account> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            for(String s:roList){
                String query = "select * from accounts where branch_id in\n" +
                        "    (select branch_id from branches where ro_id = " + roMap.get(s) + ")" +
                        "and opening_date between '" + myDateFormat.format(fromDate) + "' and '" + myDateFormat.format(toDate) + "'";
                q.setQuery(query);
                ResultSet rs = q.sql();
                while (rs.next()) {
                    Account a = new Account(rs.getString("ACCOUNT_ID"),rs.getString("OPENING_DATE").substring(0,10),rs.getString("ACCOUNT_TYPE"),
                            rs.getString("BRANCH_ID"),rs.getString("MERCHANT_NAME"));
                    accounts.add(a);
                }
            }
            ReportController rep = new ReportController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("report.fxml"));
            fxmlLoader.setController(rep);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(module.getValue()+" "+ro.getValue() + " Report");
            stage.setScene(new Scene(root1,1526,807));
            stage.show();
        }
        else {
            ObservableList<Account> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            String query = "select * from accounts where branch_id in\n" +
                    "    (select branch_id from branches where ro_id = " + roMap.get(ro.getValue()) + ")" +
                    "and opening_date between '" + myDateFormat.format(fromDate) + "' and '" + myDateFormat.format(toDate) + "'";
            q.setQuery(query);
            ResultSet rs = q.sql();
            while (rs.next()) {
                Account a = new Account(rs.getString("ACCOUNT_ID"),rs.getString("OPENING_DATE").substring(0,10),rs.getString("ACCOUNT_TYPE"),
                        rs.getString("BRANCH_ID"),rs.getString("MERCHANT_NAME"));
                accounts.add(a);
            }
            ReportController rep = new ReportController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("report.fxml"));
            fxmlLoader.setController(rep);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(ro.getValue() + " Report");
            stage.setScene(new Scene(root1,1526,807));
            stage.show();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        circle.setItems(Main.circlesList);
    }
}
