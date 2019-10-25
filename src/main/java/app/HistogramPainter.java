package app;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;

public class HistogramPainter {

    private ImageMap imageMap;

    private XYChart.Series seriesRed;
    private XYChart.Series seriesGreen;
    private XYChart.Series seriesBlue;

    private boolean success;

    HistogramPainter(Image image) {
        success = false;

        imageMap = new ImageConverter().toImageMap(image);

        seriesRed = new XYChart.Series();
        seriesGreen = new XYChart.Series();
        seriesBlue = new XYChart.Series();
        seriesRed.setName("czerwony");
        seriesGreen.setName("zielony");
        seriesBlue.setName("niebieski");

        Histogram histogram = imageMap.histogram();

        for (int i = 0; i < 256; i++) {
            seriesRed.getData().add(new XYChart.Data(String.valueOf(i), histogram.red()[i]));
            seriesGreen.getData().add(new XYChart.Data(String.valueOf(i), histogram.green()[i]));
            seriesBlue.getData().add(new XYChart.Data(String.valueOf(i), histogram.blue()[i]));
        }
    }

    public XYChart.Series getSeriesRed() {
        return seriesRed;
    }

    public XYChart.Series getSeriesGreen() {
        return seriesGreen;
    }

    public XYChart.Series getSeriesBlue() {
        return seriesBlue;
    }

    public BarChart<String, Number> paintChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        barChart.getStylesheets().add("app.css");

        barChart.setBarGap(0);

        barChart.horizontalGridLinesVisibleProperty().setValue(false);
        barChart.verticalGridLinesVisibleProperty().setValue(false);

        return barChart;
    }
}
