package com.sim.app;

import static com.google.common.base.Preconditions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaRDD;
import org.mortbay.log.Log;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class App extends Application 
{
	//private static ExecutorService executor;
	//private static SparkOperationsSimudyne simulation = new SparkOperationsSimudyne();
	private static final int NUM_YEARS = 15;
	private Simulator simulation;
	private List<SimulationRecord> records = new ArrayList<SimulationRecord>();
	
	private Button button;
	private Slider slider;
	
	
	public static void main(String[] args)
	{
		launch(args);
	}	

	
	
	public VBox mainScene()
	{
		VBox rootVertical = new VBox();
		HBox rootHorizontalControl = new HBox();
		HBox rootHorizontalCharts = new HBox();

		NumberAxis xAxis = new NumberAxis("Year", 0, 15, 1);
		NumberAxis yAxis = new NumberAxis("Count", 0, 10000, 1000);
		XYChart.Series<Number, Number> seriesBreedC = new XYChart.Series<>();
		seriesBreedC.setName("Breed C");
		XYChart.Series<Number, Number> seriesBreedNC = new XYChart.Series<>();
		seriesBreedNC.setName("Breed NC");
		
		NumberAxis lostXAxis = new NumberAxis("Year", 0, 15, 1);
		NumberAxis lostYAxis = new NumberAxis("Count", 0, 50000, 5000);
		XYChart.Series<Number, Number> seriesLostBreedC = new XYChart.Series<>();
		seriesBreedC.setName("Lost Breed C");
		XYChart.Series<Number, Number> seriesLostBreedNC = new XYChart.Series<>();
		seriesBreedNC.setName("Lost Breed NC");
		
		NumberAxis regainedXAxis = new NumberAxis("Year", 0, 15, 1);
		NumberAxis regainedYAxis = new NumberAxis("Count", 0, 50000, 5000);
		XYChart.Series<Number, Number> seriesRegainedBreedC = new XYChart.Series<>();
		seriesBreedC.setName("Lost Breed C");

		
		slider = createSlider(0.1, 2.9, 0.1);
		rootHorizontalControl.getChildren().add(slider);
		
		button = new Button("Start");
		rootHorizontalControl.getChildren().add(button);
		
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				seriesBreedC.dataProperty().get().clear();
				seriesBreedNC.dataProperty().get().clear();
				seriesLostBreedC.dataProperty().get().clear();
				seriesLostBreedNC.dataProperty().get().clear();
				seriesRegainedBreedC.dataProperty().get().clear();
				
				simulation = new Simulator();
				records = simulation.simulate(NUM_YEARS, slider.getValue());
				
				setSeriesData(seriesBreedC, records.stream()
													 .map(r -> r.getCountC())
													 .collect(Collectors.toList()), true, Color.WHITESMOKE);
				setSeriesData(seriesBreedNC, records.stream()
													  .map(r -> r.getCountNC())
													  .collect(Collectors.toList()), true, Color.WHITESMOKE);
				
				setSeriesData(seriesLostBreedC, records.stream()
													   .map(r -> r.getCountLostC())
													   .collect(Collectors.toList()), true, Color.WHITESMOKE);
				
				setSeriesData(seriesLostBreedNC, records.stream()
														.map(r -> r.getCountLostNC())
														.collect(Collectors.toList()), true, Color.WHITESMOKE);
				
				setSeriesData(seriesRegainedBreedC, records.stream()
														   .map(r -> r.getCountRegainedC())
														   .collect(Collectors.toList()), true, Color.WHITESMOKE);
				
				
			}
		});
		
		
		LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
		chart.setTitle("Agent Count");
		chart.getData().add(seriesBreedC);
		chart.getData().add(seriesBreedNC);
		
		chart.setLegendVisible(true);
		rootHorizontalCharts.getChildren().add(chart);
		
		LineChart<Number, Number> lostChart = new LineChart<>(lostXAxis, lostYAxis);
		lostChart.setTitle("Lost Agent Count");
		lostChart.getData().add(seriesLostBreedC);
		lostChart.getData().add(seriesLostBreedNC);
		
		lostChart.setLegendVisible(true);
		rootHorizontalCharts.getChildren().add(lostChart);
		
		LineChart<Number, Number> regainedChart = new LineChart<>(regainedXAxis, regainedYAxis);
		regainedChart.setTitle("Regained Agent C Count");
		regainedChart.getData().add(seriesRegainedBreedC);
		
		regainedChart.setLegendVisible(true);
		rootHorizontalCharts.getChildren().add(regainedChart);
		
		rootVertical.getChildren().add(rootHorizontalControl);
		rootVertical.getChildren().add(rootHorizontalCharts);
		return rootVertical;
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		//rootHorizontal.getChildren().add(lostAgentCountBox());
		
		File f = new File("css/line-charts.css");
		Scene scene = new Scene(mainScene(), 1200, 600);
		scene.getStylesheets().add("file:///" + f.getAbsolutePath());
		/*scene.getStylesheets().add(
			      getClass().getResource("/spark-big-data-analytics/css/line-charts.css").toExternalForm()
			    );*/
		primaryStage.setTitle("ChartsMain");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private Slider createSlider(final double min, final double max, final double majorTickSize)
	{
		Slider slider = new Slider();
		slider.setMin(min);
		slider.setMax(max);
		slider.setShowTickLabels(true);
		slider.setMajorTickUnit(majorTickSize);
		return slider;
	}
	
	private void setSeriesData(final XYChart.Series<Number, Number> series, final List<Long> values, 
								final boolean visibleHoverText, final Paint bgPaint)
	{
		int count = 0;
		for(long val : values)
		{
			XYChart.Data<Number, Number> xy = new XYChart.Data<Number, Number>(count, val);
			if(visibleHoverText)
			{
				xy.setNode(new HoverNode(val, bgPaint));	
			}
			series.getData().add(xy);
			count++;
		}
		return;
	}
	
	class HoverNode extends StackPane {
		public HoverNode(long value, final Paint paint) {

			final Label label = new Label(String.valueOf(value));
			label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
			label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			label.setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
			
			setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent mouseEvent) {
					getChildren().setAll(label);
					setCursor(Cursor.NONE);
					toFront();
				}
			});
			setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent mouseEvent) {
					getChildren().clear();
					setCursor(Cursor.CROSSHAIR);
				}
			});
		}
	}
	
}
