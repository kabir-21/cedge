package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PieChartController implements Initializable {
    @FXML
    PieChart pie;
    HashMap<String,Integer> data;
    PieChartController(HashMap<String,Integer> data){
        this.data = data;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(Map.Entry<String,Integer> e : this.data.entrySet())
            this.pie.getData().add(new PieChart.Data(e.getKey(),e.getValue()));
    }
}
