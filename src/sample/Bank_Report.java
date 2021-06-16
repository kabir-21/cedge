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

public class Bank_Report extends Bank{
    @Override
    protected void getSummary(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if(bank.getValue()==null || fromDate == null || toDate == null || fromDate.isAfter(toDate)){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else{
            ObservableList<AccountSummary> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            String query = "select opening_date, count(*) from accounts"+
                    " where opening_date between '"+myDateFormat.format(fromDate)+"' and '"
                    +myDateFormat.format(toDate)+"' group by opening_date";
            System.out.println(query);
            q.setQuery(query);
            ResultSet rs = q.sql();
            while (rs.next()){
                AccountSummary a = new AccountSummary(rs.getString("OPENING_DATE").substring(0,10),rs.getInt("COUNT(*)"));
                accounts.add(a);
            }
            SummaryReportController summaryController = new SummaryReportController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/reports/summaryReport.fxml"));
            fxmlLoader.setController(summaryController);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(bank.getValue()+" Summary");
            stage.setScene(new Scene(root1,425,712));
            stage.show();
        }
    }

    @Override
    protected void getReport(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if(bank.getValue()==null || fromDate == null || toDate == null || fromDate.isAfter(toDate)){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else{
            ObservableList<Account> accounts = FXCollections.observableArrayList();
            SqlQuery q = new SqlQuery();
            String query = "select * from accounts "
                    +"where opening_date between '"+myDateFormat.format(fromDate)+"' and '"
                    +myDateFormat.format(toDate)+"'";
            q.setQuery(query);
            ResultSet rs = q.sql();
            while (rs.next()){
                Account a = new Account(rs.getString("ACCOUNT_ID"),rs.getString("OPENING_DATE").substring(0,10),rs.getString("ACCOUNT_TYPE"),
                        rs.getString("BRANCH_ID"),rs.getString("MERCHANT_NAME"));
                accounts.add(a);
            }
            DetailedReportController rep = new DetailedReportController(accounts);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/reports/detailedReport.fxml"));
            fxmlLoader.setController(rep);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(bank.getValue()+" Report");
            stage.setScene(new Scene(root1,1526,807));
            stage.show();
        }
    }
}
