package app;

import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.Arrays;

public class BarChartPainter {

    public  BarChart<String, Number> paintHistogram() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<>();
        dataSeries1.setName("czerwony");

        dataSeries1.getData().add(new XYChart.Data<>("0", 10));
        dataSeries1.getData().add(new XYChart.Data<>("1", 30));
        dataSeries1.getData().add(new XYChart.Data<>("2", 20));

        XYChart.Series<String, Number> dataSeries2 = new XYChart.Series<>();
        dataSeries2.setName("zielony");

        dataSeries2.getData().add(new XYChart.Data<>("1", 10));
        dataSeries2.getData().add(new XYChart.Data<>("2", 30));
        dataSeries2.getData().add(new XYChart.Data<>("3", 20));

        XYChart.Series<String, Number> dataSeries3 = new XYChart.Series<>();
        dataSeries3.setName("niebieski");

        dataSeries3.getData().add(new XYChart.Data<>("1", 10));
        dataSeries3.getData().add(new XYChart.Data<>("2", 30));
        dataSeries3.getData().add(new XYChart.Data<>("3", 20));

        barChart.getData().add(dataSeries1);
        barChart.getData().add(dataSeries2);
        barChart.getData().add(dataSeries3);

        barChart.setTitle("Histogram");

        barChart.getStylesheets().add("app.css");

        return barChart;
    }

}
