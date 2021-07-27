package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Branch_Graph extends Branch{
    @FXML
    private ComboBox<String> type,period,chartType;
    @Override
    //for pie chart
    protected void getSummary(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if(circle.getValue()==null || fromDate == null || toDate == null || fromDate.isAfter(toDate) || network.getValue()==null
                || module.getValue()==null || ro.getValue()==null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else if (type.getValue() == null || (!period.isDisable() && period.getValue() == null)){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Graph Type not selected");
            errorAlert.showAndWait();
        }else{
            if(type.getValue().equals("Date-Wise")){
                Period pd = Period.between(fromDate,toDate);
                System.out.println(pd.getDays()+" "+ pd.getMonths()+" "+ pd.getYears());
                final String s = "Number of Accounts in date range: " + fromDate.toString().substring(0, 10) + " and " + toDate.toString().substring(0, 10);
                switch (period.getValue()) {
                    case "Monthly" -> { //for months
                        HashMap<String, Integer> dataSeries = new HashMap<>();
                        SqlQuery query = new SqlQuery();
                        if (branch.getValue().equals("All Branches")) {
                            StringBuilder tempQ = new StringBuilder("SELECT extract(month from Opening_date) as MONTH, count(*) as COUNT\n" +
                                    "FROM accounts where branch_id = ");
                            for (int i = 0; i < branchList.size(); i++) {
                                tempQ.append(branchMap.get(branchList.get(i)));
                                if (i != branchList.size() - 1)
                                    tempQ.append(" or branch_id = ");
                            }
                            tempQ.append("\nand opening_date between '").append(myDateFormat.format(fromDate)).append("' and '")
                                    .append(myDateFormat.format(toDate)).append("'\n")
                                    .append("GROUP BY extract(month from Opening_date)\n").append("order by MONTH asc");
                            query.setQuery(tempQ.toString());
                            System.out.println(query.getQuery());
                        } else {
                            query.setQuery("SELECT extract(month from Opening_date) as MONTH, count(*) as COUNT\n" +
                                    "FROM accounts where branch_id = " +
                                    (branchMap.get(branch.getValue())) + " and opening_date between '" + myDateFormat.format(fromDate) + "' and '"
                                    + myDateFormat.format(toDate) + "'\n" +
                                    "GROUP BY extract(month from Opening_date)\n" +
                                    "order by MONTH asc");
                            System.out.println(query.getQuery());
                        }
                        ResultSet rs = query.sql();
                        while (rs.next()) {
                            dataSeries.put(rs.getString("MONTH"),rs.getInt("COUNT"));
                        }
                        PieChartController pieChartController = new PieChartController(dataSeries);
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/PieChart.fxml"));
                        fxmlLoader.setController(pieChartController);
                        Parent root1 = fxmlLoader.load();
                        Stage stage = new Stage();
                        stage.setTitle(branch.getValue() + " Pie Chart");
                        stage.setScene(new Scene(root1, 425, 712));
                        stage.show();
                    }
                    case "Weekly" -> {// for weekwise
                        HashMap<String, Integer> dataSeries = new HashMap<>();
                        SqlQuery query = new SqlQuery();
                        if (branch.getValue().equals("All Branches")) {
                            StringBuilder tempQ = new StringBuilder("select to_char(opening_date, 'IW') as WEEK, count(Account_Id) as COUNT\n" +
                                    "from accounts where branch_id = ");
                            for (int i = 0; i < branchList.size(); i++) {
                                tempQ.append(branchMap.get(branchList.get(i)));
                                if (i != branchList.size() - 1)
                                    tempQ.append(" or branch_id = ");
                            }
                            tempQ.append("\nand opening_date between '").append(myDateFormat.format(fromDate)).append("' and '").append(myDateFormat.format(toDate)).append("'\n").append("group by to_char(opening_date, 'IW')");
                            query.setQuery(tempQ.toString());
                        } else {
                            query.setQuery("select to_char(opening_date, 'IW') as WEEK, count(Account_Id) as COUNT\n" +
                                    "from accounts where branch_id = " +
                                    (branchMap.get(branch.getValue())) + "\nand opening_date between '" + myDateFormat.format(fromDate) + "' and '"
                                    + myDateFormat.format(toDate) + "'\n" +
                                    "group by to_char(opening_date, 'IW')");
                            System.out.println(query.getQuery());
                        }
                        ResultSet rs = query.sql();
                        int cnt = 1;
                        while (rs.next())
                            dataSeries.put("" + cnt++, rs.getInt("COUNT"));
                        PieChartController pieChartController = new PieChartController(dataSeries);
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/PieChart.fxml"));
                        fxmlLoader.setController(pieChartController);
                        Parent root1 = fxmlLoader.load();
                        Stage stage = new Stage();
                        stage.setTitle(branch.getValue() + " Pie Chart");
                        stage.setScene(new Scene(root1, 425, 712));
                        stage.show();
                    }
                    case "Daily" -> {//for daywise
                        HashMap<String, Integer> dataSeries = new HashMap<>();
                        SqlQuery query = new SqlQuery();
                        if (branch.getValue().equals("All Branches")) {
                            StringBuilder tempQ = new StringBuilder("SELECT extract(year from opening_date) as YY, extract(month from opening_date) as MM, \n" +
                                    "extract(day from opening_date) as DD, count(*) as COUNT\n" +
                                    "FROM accounts where branch_id = ");
                            for (int i = 0; i < branchList.size(); i++) {
                                tempQ.append(branchMap.get(branchList.get(i)));
                                if (i != branchList.size() - 1)
                                    tempQ.append(" or branch_id = ");
                            }
                            tempQ.append("\nand opening_date between '").append(myDateFormat.format(fromDate))
                                    .append("' and '").append(myDateFormat.format(toDate)).append("'\n")
                                    .append("GROUP BY extract(year from opening_date), extract(month from opening_date), \n")
                                    .append("extract(day from opening_date)\n").append("order by yy asc\n");
                            System.out.println(tempQ);
                            query.setQuery(tempQ.toString());
                        } else {
                            query.setQuery("SELECT extract(year from opening_date) as YY, extract(month from opening_date) as MM, \n" +
                                    "extract(day from opening_date) as DD, count(*) as COUNT\n" +
                                    "FROM accounts where branch_id in = " +
                                    (branchMap.get(branch.getValue())) +
                                    "\nand opening_date between '" + myDateFormat.format(fromDate) + "' and '"
                                    + myDateFormat.format(toDate) + "'" +
                                    "\nGROUP BY extract(year from opening_date), extract(month from opening_date), \n" +
                                    "extract(day from opening_date)\n" +
                                    "order by yy asc");
                            System.out.println(query.getQuery());
                        }
                        ResultSet rs = query.sql();
                        while (rs.next()) {
                            String d = rs.getString("DD") + "-" + rs.getString("MM") + "-" + rs.getString("YY");
                            dataSeries.put(d, rs.getInt("COUNT"));
                        }
                        PieChartController pieChartController = new PieChartController(dataSeries);
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/PieChart.fxml"));
                        fxmlLoader.setController(pieChartController);
                        Parent root1 = fxmlLoader.load();
                        Stage stage = new Stage();
                        stage.setTitle(branch.getValue() + " Pie Chart");
                        stage.setScene(new Scene(root1, 425, 712));
                        stage.show();
                    }
                }
            }
            else{
                HashMap<String, Integer> dataSeries = new HashMap<>();
                for (String acc : Main.accountType) {
                    SqlQuery query = new SqlQuery();
                    if(branch.getValue().equals("All Branches")){
                        StringBuilder tempQ = new StringBuilder("SELECT count(*) as COUNT\n" +
                                "FROM accounts\n" +
                                "where account_type = '"+ acc +"' and branch_id = ");
                        for (int i = 0; i < branchList.size(); i++) {
                            tempQ.append(branchMap.get(branchList.get(i)));
                            if (i != branchList.size() - 1)
                                tempQ.append(" or branch_id = ");
                        }
                        tempQ.append("\nand opening_date between '").append(myDateFormat.format(fromDate))
                                .append("' and '").append(myDateFormat.format(toDate)).append("'\n");
                        query.setQuery(tempQ.toString());
                    }else{
                        StringBuilder tempQ = new StringBuilder("SELECT count(*) as COUNT\n" +
                                "FROM accounts\n" +
                                "where account_type = '"+ acc +"' and branch_id = "+branchMap.get(branch.getValue()));
                        tempQ.append("\nand opening_date between '").append(myDateFormat.format(fromDate))
                                .append("' and '").append(myDateFormat.format(toDate)).append("'\n");
                        query.setQuery(tempQ.toString());
                    }
                    System.out.println(query.getQuery());
                    ResultSet rs = query.sql();
                    while (rs.next())
                        dataSeries.put(acc, rs.getInt("COUNT"));
                }
                PieChartController pieChartController = new PieChartController(dataSeries);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/PieChart.fxml"));
                fxmlLoader.setController(pieChartController);
                Parent root1 = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle(branch.getValue() + " Detailed Report Graph");
                stage.setScene(new Scene(root1, 425, 712));
                stage.show();
            }
        }
    }
    @Override
    //for barchart
    protected void getReport(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if(circle.getValue()==null || fromDate == null || toDate == null || fromDate.isAfter(toDate) || network.getValue()==null
                || module.getValue()==null || ro.getValue()==null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else if(type.getValue()==null || period.getValue()==null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Graph Type not selected");
            errorAlert.showAndWait();
        }else{
            if(type.getValue().equals("Date-Wise")){
                Period pd = Period.between(fromDate,toDate);
                System.out.println(pd.getDays()+" "+ pd.getMonths()+" "+ pd.getYears());
                final String s = "Number of Accounts in date range: " + fromDate.toString().substring(0, 10) + " and " + toDate.toString().substring(0, 10);
                switch (period.getValue()) {
                    case "Monthly" -> { //for months
                        CategoryAxis x = new CategoryAxis();
                        x.setLabel("Month Number");
                        NumberAxis y = new NumberAxis();
                        y.setLabel("No. of Accounts");
                        XYChart.Series dataSeries = new XYChart.Series();
                        dataSeries.setName(s);
                        ArrayList<XYChart.Series> seriesArr = new ArrayList<>();
                        seriesArr.add(dataSeries);
                        SqlQuery query = new SqlQuery();
                        int max = Integer.MIN_VALUE;
                        if (branch.getValue().equals("All Branches")) {
                            StringBuilder tempQ = new StringBuilder("SELECT extract(month from Opening_date) as MONTH, count(*) as COUNT\n" +
                                    "FROM accounts where branch_id = ");
                            for (int i = 0; i < branchList.size(); i++) {
                                tempQ.append(branchMap.get(branchList.get(i)));
                                if (i != branchList.size() - 1)
                                    tempQ.append(" or branch_id = ");
                            }
                            tempQ.append("\nand opening_date between '").append(myDateFormat.format(fromDate)).append("' and '")
                                    .append(myDateFormat.format(toDate)).append("'\n")
                                    .append("GROUP BY extract(month from Opening_date)\n").append("order by MONTH asc");
                            query.setQuery(tempQ.toString());
                            System.out.println(query.getQuery());
                            ResultSet rs = query.sql();
                            while (rs.next()) {
                                max = Math.max(max, rs.getInt("COUNT"));
                                final XYChart.Data<String, Number> data = new XYChart.Data<>(rs.getString("MONTH"), rs.getInt("COUNT"));
                                data.nodeProperty().addListener((ov, oldNode, node) -> {
                                    if (node != null)
                                        displayLabelForData(data);
                                });
                                dataSeries.getData().add(data);
                            }
                        } else {
                            query.setQuery("SELECT extract(month from Opening_date) as MONTH, count(*) as COUNT\n" +
                                    "FROM accounts where branch_id = " +
                                    (branchMap.get(branch.getValue())) + " and opening_date between '" + myDateFormat.format(fromDate) + "' and '"
                                    + myDateFormat.format(toDate) + "'\n" +
                                    "GROUP BY extract(month from Opening_date)\n" +
                                    "order by MONTH asc");
                            System.out.println(query.getQuery());
                            ResultSet rs = query.sql();
                            while (rs.next()) {
                                max = Math.max(max, rs.getInt("COUNT"));
                                final XYChart.Data<String, Number> data = new XYChart.Data<>(rs.getString("MONTH"), rs.getInt("COUNT"));
                                data.nodeProperty().addListener((ov, oldNode, node) -> {
                                    if (node != null) {
                                        displayLabelForData(data);
                                    }
                                });
                                dataSeries.getData().add(data);
                            }
                        }
                        BarGraphController graphController = new BarGraphController(x, y, seriesArr, max, "Month Number", "Number of Accounts");
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/BarGraph.fxml"));
                        fxmlLoader.setController(graphController);
                        Parent root1 = fxmlLoader.load();
                        Stage stage = new Stage();
                        stage.setTitle(branch.getValue() + " Detailed Report Graph");
                        stage.setScene(new Scene(root1, 425, 712));
                        stage.show();
                    }
                    case "Weekly" -> {// for weekwise
                        CategoryAxis x = new CategoryAxis();
                        x.setLabel("Week Number");
                        NumberAxis y = new NumberAxis();
                        y.setLabel("No. of Accounts");
                        XYChart.Series dataSeries = new XYChart.Series();
                        dataSeries.setName(s);
                        ArrayList<XYChart.Series> seriesArr = new ArrayList<>();
                        seriesArr.add(dataSeries);
                        int max = Integer.MIN_VALUE;
                        SqlQuery query = new SqlQuery();
                        if (branch.getValue().equals("All Branches")) {
                            StringBuilder tempQ = new StringBuilder("select to_char(opening_date, 'IW') as WEEK, count(Account_Id) as COUNT\n" +
                                    "from accounts where branch_id = ");
                            for (int i = 0; i < branchList.size(); i++) {
                                tempQ.append(branchMap.get(branchList.get(i)));
                                if (i != branchList.size() - 1)
                                    tempQ.append(" or branch_id = ");
                            }
                            tempQ.append("\nand opening_date between '").append(myDateFormat.format(fromDate)).append("' and '").append(myDateFormat.format(toDate)).append("'\n").append("group by to_char(opening_date, 'IW')");
                            query.setQuery(tempQ.toString());
                            ResultSet rs = query.sql();
                            int cnt = 1;
                            while (rs.next()) {
                                max = Math.max(max, rs.getInt("COUNT"));
                                final XYChart.Data<String, Number> data = new XYChart.Data<>("" + cnt++, rs.getInt("COUNT"));
                                data.nodeProperty().addListener((ov, oldNode, node) -> {
                                    if (node != null) {
                                        displayLabelForData(data);
                                    }
                                });
                                dataSeries.getData().add(data);
                            }
                            BarGraphController graphController = new BarGraphController(x, y, seriesArr, max, "Week Number", "Number of Accounts");
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/BarGraph.fxml"));
                            fxmlLoader.setController(graphController);
                            Parent root1 = fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.setTitle(branch.getValue() + " Detailed Report Graph");
                            stage.setScene(new Scene(root1, 425, 712));
                            stage.show();
                        } else {
                            query.setQuery("select to_char(opening_date, 'IW') as WEEK, count(Account_Id) as COUNT\n" +
                                    "from accounts where branch_id = " +
                                    (branchMap.get(branch.getValue())) + "\nand opening_date between '" + myDateFormat.format(fromDate) + "' and '"
                                    + myDateFormat.format(toDate) + "'\n" +
                                    "group by to_char(opening_date, 'IW')");
                            System.out.println(query.getQuery());
                            ResultSet rs = query.sql();
                            int cnt = 1;
                            while (rs.next()) {
                                max = Math.max(max, rs.getInt("COUNT"));
                                final XYChart.Data<String, Number> data = new XYChart.Data<>("" + cnt++, rs.getInt("COUNT"));
                                data.nodeProperty().addListener((ov, oldNode, node) -> {
                                    if (node != null) {
                                        displayLabelForData(data);
                                    }
                                });
                                dataSeries.getData().add(data);
                            }
                            BarGraphController graphController = new BarGraphController(x, y, seriesArr, max, "Week Number", "Number of Accounts");
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/BarGraph.fxml"));
                            fxmlLoader.setController(graphController);
                            Parent root1 = fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.setTitle(branch.getValue() + " Detailed Report Graph");
                            stage.setScene(new Scene(root1, 425, 712));
                            stage.show();
                        }
                    }
                    case "Daily" -> {//for daywise
                        CategoryAxis x = new CategoryAxis();
                        x.setLabel("Date");
                        NumberAxis y = new NumberAxis();
                        y.setLabel("No. of Accounts");
                        XYChart.Series dataSeries = new XYChart.Series();
                        dataSeries.setName(s);
                        ArrayList<XYChart.Series> seriesArr = new ArrayList<>();
                        seriesArr.add(dataSeries);
                        SqlQuery query = new SqlQuery();
                        if (branch.getValue().equals("All Branches")) {
                            StringBuilder tempQ = new StringBuilder("SELECT extract(year from opening_date) as YY, extract(month from opening_date) as MM, \n" +
                                    "extract(day from opening_date) as DD, count(*) as COUNT\n" +
                                    "FROM accounts where branch_id = ");
                            for (int i = 0; i < branchList.size(); i++) {
                                tempQ.append(branchMap.get(branchList.get(i)));
                                if (i != branchList.size() - 1)
                                    tempQ.append(" or branch_id = ");
                            }
                            tempQ.append("\nand opening_date between '").append(myDateFormat.format(fromDate))
                                    .append("' and '").append(myDateFormat.format(toDate)).append("'\n")
                                    .append("GROUP BY extract(year from opening_date), extract(month from opening_date), \n")
                                    .append("extract(day from opening_date)\n").append("order by yy asc\n");
                            System.out.println(tempQ);
                            query.setQuery(tempQ.toString());
                            ResultSet rs = query.sql();
                            int max = Integer.MIN_VALUE;
                            while (rs.next()) {
                                max = Math.max(max, rs.getInt("COUNT"));
                                String d = rs.getString("DD") + "-" + rs.getString("MM") + "-" + rs.getString("YY");
                                final XYChart.Data<String, Number> data = new XYChart.Data<>(d, rs.getInt("COUNT"));
                                data.nodeProperty().addListener((ov, oldNode, node) -> {
                                    if (node != null) {
                                        displayLabelForData(data);
                                    }
                                });
                                dataSeries.getData().add(data);
                            }
                            BarGraphController graphController = new BarGraphController(x, y, seriesArr, max, "Date", "Number of Accounts");
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/BarGraph.fxml"));
                            fxmlLoader.setController(graphController);
                            Parent root1 = fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.setTitle(branch.getValue() + " Detailed Report Graph");
                            stage.setScene(new Scene(root1, 425, 712));
                            stage.show();
                        } else {
                            query.setQuery("SELECT extract(year from opening_date) as YY, extract(month from opening_date) as MM, \n" +
                                    "extract(day from opening_date) as DD, count(*) as COUNT\n" +
                                    "FROM accounts where branch_id in = " +
                                    (branchMap.get(branch.getValue())) +
                                    "\nand opening_date between '" + myDateFormat.format(fromDate) + "' and '"
                                    + myDateFormat.format(toDate) + "'" +
                                    "\nGROUP BY extract(year from opening_date), extract(month from opening_date), \n" +
                                    "extract(day from opening_date)\n" +
                                    "order by yy asc");
                            System.out.println(query.getQuery());
                            ResultSet rs = query.sql();
                            int max = Integer.MIN_VALUE;
                            while (rs.next()) {
                                max = Math.max(max, rs.getInt("COUNT"));
                                String d = rs.getString("DD") + "-" + rs.getString("MM") + "-" + rs.getString("YY");
                                final XYChart.Data<String, Number> data = new XYChart.Data<>(d, rs.getInt("COUNT"));
                                data.nodeProperty().addListener((ov, oldNode, node) -> {
                                    if (node != null) {
                                        displayLabelForData(data);
                                    }
                                });
                                dataSeries.getData().add(data);
                            }
                            BarGraphController graphController = new BarGraphController(x, y, seriesArr, max, "Date", "Number of Accounts");
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/BarGraph.fxml"));
                            fxmlLoader.setController(graphController);
                            Parent root1 = fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.setTitle(branch.getValue() + " Detailed Report Graph");
                            stage.setScene(new Scene(root1, 425, 712));
                            stage.show();
                        }
                    }
                }
            }else if(type.getSelectionModel().getSelectedIndex()==1){
                Period pd = Period.between(fromDate,toDate);
                System.out.println(pd.getDays()+" "+ pd.getMonths()+" "+ pd.getYears());
                switch (period.getValue()) {
                    case "Monthly" -> { //for months
                        CategoryAxis x = new CategoryAxis();
                        x.setLabel("Month Number");
                        NumberAxis y = new NumberAxis();
                        y.setLabel("No. of Accounts");
                        ArrayList<XYChart.Series> seriesArr = new ArrayList<>();
                        int max = Integer.MIN_VALUE;

                        for(String acc:Main.accountType){
                            XYChart.Series dataSeries = new XYChart.Series();
                            dataSeries.setName(acc);
                            seriesArr.add(dataSeries);
                            SqlQuery query = new SqlQuery();
                            if (branch.getValue().equals("All Branches")) {
                                StringBuilder tempQ = new StringBuilder("SELECT extract(month from Opening_date) as MONTH, count(*) as COUNT\n" +
                                        "FROM accounts where branch_id = ");
                                for (int i = 0; i < branchList.size(); i++) {
                                    tempQ.append(branchMap.get(branchList.get(i)));
                                    if (i != branchList.size() - 1)
                                        tempQ.append(" or branch_id = ");
                                }
                                tempQ.append("\nand account_type = '"+acc+"' and opening_date between '").append(myDateFormat.format(fromDate)).append("' and '")
                                        .append(myDateFormat.format(toDate)).append("'\n")
                                        .append("GROUP BY extract(month from Opening_date)\n").append("order by MONTH asc");
                                query.setQuery(tempQ.toString());
                                System.out.println(query.getQuery());
                                ResultSet rs = query.sql();
                                while (rs.next()) {
                                    max = Math.max(max, rs.getInt("COUNT"));
                                    final XYChart.Data<String, Number> data = new XYChart.Data<>(rs.getString("MONTH"), rs.getInt("COUNT"));
                                    data.nodeProperty().addListener((ov, oldNode, node) -> {
                                        if (node != null)
                                            displayLabelForData(data);
                                    });
                                    dataSeries.getData().add(data);
                                }
                            } else {
                                query.setQuery("SELECT extract(month from Opening_date) as MONTH, count(*) as COUNT\n" +
                                        "FROM accounts where branch_id = " +
                                        (branchMap.get(branch.getValue())) + " and account_type = '"+acc+"' and opening_date between '" + myDateFormat.format(fromDate) + "' and '"
                                        + myDateFormat.format(toDate) + "'\n" +
                                        "GROUP BY extract(month from Opening_date)\n" +
                                        "order by MONTH asc");
                                System.out.println(query.getQuery());
                                ResultSet rs = query.sql();
                                while (rs.next()) {
                                    max = Math.max(max, rs.getInt("COUNT"));
                                    final XYChart.Data<String, Number> data = new XYChart.Data<>(rs.getString("MONTH"), rs.getInt("COUNT"));
                                    data.nodeProperty().addListener((ov, oldNode, node) -> {
                                        if (node != null) {
                                            displayLabelForData(data);
                                        }
                                    });
                                    dataSeries.getData().add(data);
                                }
                            }
                        }
                        BarGraphController graphController = new BarGraphController(x, y, seriesArr, max, "Month Number", "Number of Accounts");
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/BarGraph.fxml"));
                        fxmlLoader.setController(graphController);
                        Parent root1 = fxmlLoader.load();
                        Stage stage = new Stage();
                        stage.setTitle(branch.getValue() + " Detailed Report Graph");
                        stage.setScene(new Scene(root1, 425, 712));
                        stage.show();
                    }
                    case "Weekly" -> {// for weekwise
                        CategoryAxis x = new CategoryAxis();
                        x.setLabel("Week Number");
                        NumberAxis y = new NumberAxis();
                        y.setLabel("No. of Accounts");
                        ArrayList<XYChart.Series> seriesArr = new ArrayList<>();
                        int max = Integer.MIN_VALUE;
                        for(String acc:Main.accountType){
                            XYChart.Series dataSeries = new XYChart.Series();
                            dataSeries.setName(acc);
                            seriesArr.add(dataSeries);
                            SqlQuery query = new SqlQuery();
                            if (branch.getValue().equals("All Branches")) {
                                StringBuilder tempQ = new StringBuilder("select to_char(opening_date, 'IW') as WEEK, count(Account_Id) as COUNT\n" +
                                        "from accounts where branch_id = ");
                                for (int i = 0; i < branchList.size(); i++) {
                                    tempQ.append(branchMap.get(branchList.get(i)));
                                    if (i != branchList.size() - 1)
                                        tempQ.append(" or branch_id = ");
                                }
                                tempQ.append("\n and account_type = '").append(acc).append("' and opening_date between '").append(myDateFormat.format(fromDate)).append("' and '").append(myDateFormat.format(toDate)).append("'\n").append("group by to_char(opening_date, 'IW')");
                                query.setQuery(tempQ.toString());
                                ResultSet rs = query.sql();
                                int cnt = 1;
                                while (rs.next()) {
                                    max = Math.max(max, rs.getInt("COUNT"));
                                    final XYChart.Data<String, Number> data = new XYChart.Data<>("" + cnt++, rs.getInt("COUNT"));
                                    data.nodeProperty().addListener((ov, oldNode, node) -> {
                                        if (node != null) {
                                            displayLabelForData(data);
                                        }
                                    });
                                    dataSeries.getData().add(data);
                                }
                            } else {
                                query.setQuery("select to_char(opening_date, 'IW') as WEEK, count(Account_Id) as COUNT\n" +
                                        "from accounts where branch_id = " +
                                        (branchMap.get(branch.getValue())) + "\n and account_type = '"+acc+"' and opening_date between '" + myDateFormat.format(fromDate) + "' and '"
                                        + myDateFormat.format(toDate) + "'\n" +
                                        "group by to_char(opening_date, 'IW')");
                                System.out.println(query.getQuery());
                                ResultSet rs = query.sql();
                                int cnt = 1;
                                while (rs.next()) {
                                    max = Math.max(max, rs.getInt("COUNT"));
                                    final XYChart.Data<String, Number> data = new XYChart.Data<>("" + cnt++, rs.getInt("COUNT"));
                                    data.nodeProperty().addListener((ov, oldNode, node) -> {
                                        if (node != null) {
                                            displayLabelForData(data);
                                        }
                                    });
                                    dataSeries.getData().add(data);
                                }
                            }
                        }
                        BarGraphController graphController = new BarGraphController(x, y, seriesArr, max, "Week Number", "Number of Accounts");
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/BarGraph.fxml"));
                        fxmlLoader.setController(graphController);
                        Parent root1 = fxmlLoader.load();
                        Stage stage = new Stage();
                        stage.setTitle(branch.getValue() + " Detailed Report Graph");
                        stage.setScene(new Scene(root1, 425, 712));
                        stage.show();
                    }
                    case "Daily" -> {//for daywise
                        CategoryAxis x = new CategoryAxis();
                        x.setLabel("Date");
                        NumberAxis y = new NumberAxis();
                        y.setLabel("No. of Accounts");
                        ArrayList<XYChart.Series> seriesArr = new ArrayList<>();
                        int max = Integer.MIN_VALUE;
                        for(String acc:Main.accountType){
                            XYChart.Series dataSeries = new XYChart.Series();
                            dataSeries.setName(acc);
                            seriesArr.add(dataSeries);
                            SqlQuery query = new SqlQuery();
                            if (branch.getValue().equals("All Branches")) {
                                StringBuilder tempQ = new StringBuilder("SELECT extract(year from opening_date) as YY, extract(month from opening_date) as MM, \n" +
                                        "extract(day from opening_date) as DD, count(*) as COUNT\n" +
                                        "FROM accounts where branch_id = ");
                                for (int i = 0; i < branchList.size(); i++) {
                                    tempQ.append(branchMap.get(branchList.get(i)));
                                    if (i != branchList.size() - 1)
                                        tempQ.append(" or branch_id = ");
                                }
                                tempQ.append("\nand account_type = '").append(acc).append("' and opening_date between '").append(myDateFormat.format(fromDate))
                                        .append("' and '").append(myDateFormat.format(toDate)).append("'\n")
                                        .append("GROUP BY extract(year from opening_date), extract(month from opening_date), \n")
                                        .append("extract(day from opening_date)\n").append("order by yy asc\n");
                                System.out.println(tempQ);
                                query.setQuery(tempQ.toString());
                                ResultSet rs = query.sql();
                                while (rs.next()) {
                                    max = Math.max(max, rs.getInt("COUNT"));
                                    String d = rs.getString("DD") + "-" + rs.getString("MM") + "-" + rs.getString("YY");
                                    final XYChart.Data<String, Number> data = new XYChart.Data<>(d, rs.getInt("COUNT"));
                                    data.nodeProperty().addListener((ov, oldNode, node) -> {
                                        if (node != null) {
                                            displayLabelForData(data);
                                        }
                                    });
                                    dataSeries.getData().add(data);
                                }
                            } else {
                                query.setQuery("SELECT extract(year from opening_date) as YY, extract(month from opening_date) as MM, \n" +
                                        "extract(day from opening_date) as DD, count(*) as COUNT\n" +
                                        "FROM accounts where branch_id in = " +
                                        (branchMap.get(branch.getValue())) +
                                        "\nand account_type = '"+acc+"' and opening_date between '" + myDateFormat.format(fromDate) + "' and '"
                                        + myDateFormat.format(toDate) + "'" +
                                        "\nGROUP BY extract(year from opening_date), extract(month from opening_date), \n" +
                                        "extract(day from opening_date)\n" +
                                        "order by yy asc");
                                System.out.println(query.getQuery());
                                ResultSet rs = query.sql();
                                while (rs.next()) {
                                    max = Math.max(max, rs.getInt("COUNT"));
                                    String d = rs.getString("DD") + "-" + rs.getString("MM") + "-" + rs.getString("YY");
                                    final XYChart.Data<String, Number> data = new XYChart.Data<>(d, rs.getInt("COUNT"));
                                    data.nodeProperty().addListener((ov, oldNode, node) -> {
                                        if (node != null) {
                                            displayLabelForData(data);
                                        }
                                    });
                                    dataSeries.getData().add(data);
                                }
                            }
                        }
                        BarGraphController graphController = new BarGraphController(x, y, seriesArr, max, "Date", "Number of Accounts");
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmls/graphs/BarGraph.fxml"));
                        fxmlLoader.setController(graphController);
                        Parent root1 = fxmlLoader.load();
                        Stage stage = new Stage();
                        stage.setTitle(branch.getValue() + " Detailed Report Graph");
                        stage.setScene(new Scene(root1, 425, 712));
                        stage.show();
                    }
                }
            }
        }
    }
    private void displayLabelForData(XYChart.Data<String, Number> data) {
        final Node node = data.getNode();
        final Text dataText = new Text(data.getYValue() + "");
        node.parentProperty().addListener((ov, oldParent, parent) -> {
            Group parentGroup = (Group) parent;
            parentGroup.getChildren().add(dataText);
        });

        node.boundsInParentProperty().addListener((ov, oldBounds, bounds) -> {
            dataText.setLayoutX(
                    Math.round(
                            bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2
                    )
            );
            dataText.setLayoutY(
                    Math.round(
                            bounds.getMinY() - dataText.prefHeight(-1) * 0.5
                    )
            );
        });
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        circle.setItems(Main.circlesList);
        ObservableList<String> types = FXCollections.observableArrayList("Date-Wise","Acc Type + Date-Wise");
        type.setItems(types);
        ObservableList<String> periods = FXCollections.observableArrayList("Monthly","Weekly","Daily");
        period.setItems(periods);
        chartType.setItems(FXCollections.observableArrayList("Pie Chart","Bar Chart"));
    }

    @FXML
    protected void getPlot(ActionEvent event) throws SQLException, IOException, ClassNotFoundException{
        if(chartType.getSelectionModel().getSelectedIndex()==0){ //barchart
            getSummary(new ActionEvent());
        }else{
            getReport(new ActionEvent());
        }
    }

    @FXML
    protected void setParameters(ActionEvent event) {
        if(chartType.getValue().equals("Pie Chart")){
            type.setItems(FXCollections.observableArrayList("Date-Wise","Acc-Type Wise"));
        }else{
            type.setItems(FXCollections.observableArrayList("Date-Wise","Date-Wise+Acc-Type Wise"));
        }
    }

    @FXML
    protected void setPeriod(ActionEvent event) {
        if(type.getValue()!=null)
            period.setDisable(type.getValue().equals("Acc-Type Wise"));
    }
}
