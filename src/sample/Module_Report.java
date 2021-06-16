package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Module_Report extends Module {
    @Override
    protected void getSummary(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if (circle.getValue() == null || fromDate == null || toDate == null || fromDate.isAfter(toDate) || network.getValue() == null
                || module.getValue() == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else if(module.getValue().equals("All Modules")){
            ObservableList<AccountSummaryReport> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            for(String s:modList){
                String query = "select opening_date, count(*) from accounts where branch_id in\n" +
                        "    (select branch_id from branches where ro_id in\n" +
                        "        (select ro_id from ro where module_id = " + modMap.get(s) + "))\n" +
                        "and opening_date between '" + myDateFormat.format(fromDate) + "' and '" + myDateFormat.format(toDate)+"' group by opening_date";
                System.out.println(query);
                q.setQuery(query);
                ResultSet rs = q.sql();
                while (rs.next()){
                    AccountSummaryReport a = new AccountSummaryReport(rs.getString("OPENING_DATE").substring(0,10),rs.getInt("COUNT(*)"));
                    accounts.add(a);
                }
            }
            SummaryReportController summaryController = new SummaryReportController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/reports/summaryReport.fxml"));
            fxmlLoader.setController(summaryController);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(network.getValue()+" "+module.getValue()+" Summary");
            stage.setScene(new Scene(root1,425,712));
            stage.show();
        }
        else{
            ObservableList<AccountSummaryReport> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            String query = "select opening_date, count(*) from accounts where branch_id in\n" +
                    "    (select branch_id from branches where ro_id in\n" +
                    "        (select ro_id from ro where module_id = " + modMap.get(module.getValue()) + "))\n" +
                    "and opening_date between '" + myDateFormat.format(fromDate) + "' and '" + myDateFormat.format(toDate)+"' group by opening_date";
            System.out.println(query);
            q.setQuery(query);
            ResultSet rs = q.sql();
            while (rs.next()){
                AccountSummaryReport a = new AccountSummaryReport(rs.getString("OPENING_DATE").substring(0,10),rs.getInt("COUNT(*)"));
                accounts.add(a);
            }
            SummaryReportController summaryController = new SummaryReportController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/reports/summaryReport.fxml"));
            fxmlLoader.setController(summaryController);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(module.getValue()+" Summary");
            stage.setScene(new Scene(root1,425,712));
            stage.show();
        }
    }

    @Override
    protected void getReport(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if (circle.getValue() == null || fromDate == null || toDate == null || fromDate.isAfter(toDate) || network.getValue() == null
                || module.getValue() == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else if(module.getValue().equals("All Modules")){
            ObservableList<AccountDetailedReport> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            for (String s : modList) {
                String query = "select * from accounts where branch_id in\n" +
                        "    (select branch_id from branches where ro_id in\n" +
                        "        (select ro_id from ro where module_id = " + modMap.get(s) + "))\n" +
                        "and opening_date between '" + myDateFormat.format(fromDate) + "' and '" + myDateFormat.format(toDate) + "'";
                q.setQuery(query);
                System.out.println(query);
                ResultSet rs = q.sql();
                while (rs.next()) {
                    AccountDetailedReport a = new AccountDetailedReport(rs.getString("ACCOUNT_ID"), rs.getString("OPENING_DATE").substring(0, 10), rs.getString("ACCOUNT_TYPE"),
                            rs.getString("BRANCH_ID"), rs.getString("MERCHANT_NAME"));
                    accounts.add(a);
                }
            }
            DetailedReportController rep = new DetailedReportController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/reports/detailedReport.fxml"));
            fxmlLoader.setController(rep);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(network.getValue()+" "+module.getValue() + " Report");
            stage.setScene(new Scene(root1,1526,807));
            stage.show();
        }
        else {
            ObservableList<AccountDetailedReport> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            String query = "select * from accounts where branch_id in\n" +
                    "    (select branch_id from branches where ro_id in\n" +
                    "        (select ro_id from ro where module_id = " + modMap.get(module.getValue()) + "))\n" +
                    "and opening_date between '" + myDateFormat.format(fromDate) + "' and '" + myDateFormat.format(toDate) + "'";
            q.setQuery(query);
            ResultSet rs = q.sql();
            while (rs.next()) {
                AccountDetailedReport a = new AccountDetailedReport(rs.getString("ACCOUNT_ID"),rs.getString("OPENING_DATE").substring(0,10),rs.getString("ACCOUNT_TYPE"),
                        rs.getString("BRANCH_ID"),rs.getString("MERCHANT_NAME"));
                accounts.add(a);
            }
            DetailedReportController rep = new DetailedReportController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/reports/detailedReport.fxml"));
            fxmlLoader.setController(rep);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(module.getValue() + " Report");
            stage.setScene(new Scene(root1,1526,807));
            stage.show();
        }
    }
}
