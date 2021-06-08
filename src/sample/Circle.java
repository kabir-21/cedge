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

public class Circle implements Initializable {
    ObservableList<String> circlesList = FXCollections.observableArrayList();
    HashMap<String,Integer> map = new HashMap<>();
    DateTimeFormatter myDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @FXML
    private ComboBox<String> circle;
    @FXML
    private DatePicker from,to;
    @FXML
    private Button summary,report;
    @FXML
    void getSummary(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if(circle.getValue()==null || fromDate == null || toDate == null || fromDate.isAfter(toDate)){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else{
            ObservableList<AccountSummary> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            String query = "select opening_date, count(*) from accounts where branch_id in\n" +
                    "    (select branch_id from branches where ro_id in\n" +
                    "        (select ro_id from ro where module_id in \n" +
                    "            (select module_id from modules where network_id in \n" +
                    "                (select network_id from networks where circle_id = "+
                    (map.get(circle.getValue()))+")))) and opening_date between '"+myDateFormat.format(fromDate)+"' and '"
                    +myDateFormat.format(toDate)+"' group by opening_date";
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
            stage.setTitle(circle.getValue()+" Summary");
            stage.setScene(new Scene(root1,425,712));
            stage.show();
        }
    }

    @FXML
    void getReport(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if(circle.getValue()==null || fromDate == null || toDate == null || fromDate.isAfter(toDate)){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else{
            ObservableList<Account> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            String query = "select * from accounts where branch_id in\n" +
                    "    (select branch_id from branches where ro_id in\n" +
                    "        (select ro_id from ro where module_id in \n" +
                    "            (select module_id from modules where network_id in \n" +
                    "                (select network_id from networks where circle_id = "+
                    (map.get(circle.getValue()))+")))) and opening_date between '"+myDateFormat.format(fromDate)+"' and '"
                    +myDateFormat.format(toDate)+"'";
            q.setQuery(query);
            ResultSet rs = q.sql();
            while (rs.next()){
                Account a = new Account(rs.getString("ACCOUNT_ID"),rs.getString("OPENING_DATE").substring(0,10),rs.getString("ACCOUNT_TYPE"),
                        rs.getString("BRANCH_ID"),rs.getString("MERCHANT_NAME"));
                accounts.add(a);
            }
            ReportController rep = new ReportController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("report.fxml"));
            fxmlLoader.setController(rep);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(circle.getValue()+" Report");
            stage.setScene(new Scene(root1,1526,807));
            stage.getScene().getStylesheets().add("C:\\Users\\kabni\\IdeaProjects\\untitled2\\CSS\\style.css");
            stage.show();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SqlQuery q = new SqlQuery();
        try {
            q.setQuery("select * from circles");
            ResultSet set = q.sql();
            while (set.next()){
                map.put(set.getString(2),set.getInt(1));
                circlesList.add(set.getString(2));
            }
            System.out.println(map);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        ObservableList<String> withAll = FXCollections.observableArrayList();
        withAll.addAll(circlesList); withAll.add("All Circles");
        circle.setItems(withAll);
    }
}
