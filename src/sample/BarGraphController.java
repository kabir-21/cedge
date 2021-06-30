package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BarGraphController implements Initializable {
    @FXML
    CategoryAxis xAxis;
    @FXML
    NumberAxis yAxis;
    @FXML
    BarChart barChart;
    ArrayList<XYChart.Series> dataSeries;
    int max;
    String xLabel;
    String yLabel;

    BarGraphController(CategoryAxis x, NumberAxis y, ArrayList<XYChart.Series> dataSeries, int max, String xLabel, String yLabel){
        this.xAxis = x;
        this.xLabel = xLabel;
        this.yAxis = y;
        this.yLabel = yLabel;
        this.dataSeries = dataSeries;
        this.max = max;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        this.yAxis.setAutoRanging(false);
        this.yAxis.setUpperBound(max+100);
        for(XYChart.Series s: this.dataSeries){
            this.barChart.getData().add(s);
        }
    }
}
