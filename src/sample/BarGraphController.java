package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

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
//        System.out.println(max);

//        for(XYChart.Series s:this.dataSeries){
//            ObservableList obs = s.getData();
//            for(int i=0;i<obs.size();i++)
//                System.out.println(obs.get(i));
//        }
////        barChart = new BarChart(this.xAxis,this.yAxis);
//        for(XYChart.Series s: dataSeries)
//            barChart.getData().add(s);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        this.yAxis.setAutoRanging(false);
        this.yAxis.setUpperBound(max+50);
//        this.barChart = new BarChart(xAxis, yAxis);
//        xAxis.setLabel();
        for(XYChart.Series s: this.dataSeries){
            this.barChart.getData().add(s);
        }
    }
}
