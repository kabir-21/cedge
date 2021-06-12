package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Chart implements Initializable {
    DateTimeFormatter myDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    ObservableList<String> subList = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> reportType, plotType, field, sub;
    @FXML
    private Button getChart;
    @FXML
    private DatePicker from, to;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> reportList = FXCollections.observableArrayList("Summary Report","Detailed Report");
//        reportList.add("Summary Report");
//        reportList.add("Detailed Report");
        reportType.setItems(reportList);
        ObservableList<String> plotList = FXCollections.observableArrayList("Date-Wise","Acc Type Wise");
//        plotList.add("Date-Wise");
//        plotList.add("Account Type Wise");
        plotType.setItems(plotList);
        //set from main table make changes later
        ObservableList<String> fieldList = FXCollections.observableArrayList("Bank","Circle","Network","Module","RO","Branch");
        field.setItems(fieldList);
    }
    @FXML
    void generateGraph(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if (reportType.getValue() == null || plotType.getValue() == null || field.getValue() == null
                || fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Query");
            errorAlert.setContentText("Possible Errors:\n1. Invalid Date Selection\n2. Invalid Field Selection");
            errorAlert.showAndWait();
        }else{
            Period pd = Period.between(fromDate,toDate);
            if(pd.getYears()>1.5){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Date should lie between 18 months");
                errorAlert.showAndWait();
            }else{
                if (reportType.getSelectionModel().getSelectedIndex() == 0) {
                    getSummaryReport();
                } else if (reportType.getSelectionModel().getSelectedIndex() == 1) {
                    getDetailedReport();
                }
            }
        }
    }

    private void getDetailedReport() throws IOException, SQLException, ClassNotFoundException {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if(plotType.getSelectionModel().getSelectedIndex()==0){
            //datewise
            CategoryAxis x = new CategoryAxis();
            x.setLabel("Month Number");
            NumberAxis y = new NumberAxis();
            y.setLabel("No. of Accounts");
            XYChart.Series dataSeries = new XYChart.Series();
            dataSeries.setName("Number of Accounts in date range: "+fromDate.toString().substring(0,10)+" and "+toDate.toString().substring(0,10));
            ArrayList<XYChart.Series> seriesArr = new ArrayList<>();
            seriesArr.add(dataSeries);
            SqlQuery query = new SqlQuery();
            switch (field.getValue()){
                case "Bank":
                    query.setQuery("select to_char(Opening_Date, 'MM') as month, count(Account_ID) as count\n" +
                            "from accounts\n" +
                            "where opening_date between '"+myDateFormat.format(fromDate)+"' and '"+myDateFormat.format(toDate)+"'\n" +
                            "group by to_char(Opening_Date, 'MM')\n" +
                            "order by 1");
                case "Circle":
                    query.setQuery("select to_char(Opening_Date, 'MM') as month, count(Account_ID) as count\n" +
                            "from accounts where \n" +
                            "where opening_date between '"+myDateFormat.format(fromDate)+"' and '"+myDateFormat.format(toDate)+"'\n" +
                            "group by to_char(Opening_Date, 'MM')\n" +
                            "order by 1");
            }
            query.setQuery("select to_char(Opening_Date, 'MM') as month, count(Account_ID) as count\n" +
                    "from accounts\n" +
                    "where opening_date between '"+myDateFormat.format(fromDate)+"' and '"+myDateFormat.format(toDate)+"'\n" +
                    "group by to_char(Opening_Date, 'MM')\n" +
                    "order by 1");
            System.out.println(query.getQuery());
            ResultSet rs = query.sql();
            int max = Integer.MIN_VALUE;
            while (rs.next()){
                max = Math.max(max,rs.getInt("COUNT"));
                System.out.println(rs.getString("MONTH")+" "+rs.getInt("COUNT"));
                final XYChart.Data<String,Number> data = new XYChart.Data<>(rs.getString("MONTH"),rs.getInt("COUNT"));
                data.nodeProperty().addListener(new ChangeListener<Node>() {
                    @Override public void changed(ObservableValue<? extends Node> ov, Node oldNode, final Node node) {
                        if (node != null) {
                            displayLabelForData(data);
                        }
                    }
                });
//                new XYChart.Data(rs.getString("MONTH"),rs.getInt("COUNT"))
                dataSeries.getData().add(data);
            }
            BarGraphController graphController = new BarGraphController(x,y,seriesArr,max,"Month Number","Number of Accounts");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BarGraph.fxml"));
            fxmlLoader.setController(graphController);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(field.getValue()+" Detailed Report Graph");
            stage.setScene(new Scene(root1,425,712));
            stage.show();
        }else{
            //datewise acctype wise
        }
    }

    private void getSummaryReport() {
        LocalDate fromDate = from.getValue();
        LocalDate toDate = to.getValue();
        if(plotType.getSelectionModel().getSelectedIndex()==0){
            //datewise
        }else{
            //datewise acctype wise
        }
    }

    /** places a text label with a bar's value above a bar node for a given XYChart.Data */
    private void displayLabelForData(XYChart.Data<String, Number> data) {
        final Node node = data.getNode();
        final Text dataText = new Text(data.getYValue() + "");
        node.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
                Group parentGroup = (Group) parent;
                parentGroup.getChildren().add(dataText);
            }
        });

        node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
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
            }
        });
    }
    @FXML
    void setSub(ActionEvent event) {
        if(field.getValue()!=null){
            switch (field.getValue()){
                case "Bank":

                case "Circle":
                    ObservableList<String> circles = Main.circlesList;
                    circles.add("All Circles");
                    sub.setItems(circles);
                case "Network":

                case "Module":
                case "RO":
                case "Branch":
            }
        }
    }
}
