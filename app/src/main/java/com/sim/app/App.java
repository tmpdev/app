package com.sim.app;

import static com.google.common.base.Preconditions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import static com.sim.app.AppUtils.*;

/**
 * Main app that presents the user with a slider for model input, a button for requesting a simulation
 * and charts for model output.
 * 
 * The scene is created in <code>mainScene()</code>. Charts are created using @see AppUtils
 * 
 * @author Max Hoefl
 *
 */
public class App extends Application 
{
	private static final int NUM_YEARS = 15;
	private Simulator simulation;
	private List<SimulationRecord> records = new ArrayList<SimulationRecord>();
	
	private Button button;
	private Slider slider;
	
	
	public static void main(String[] args)
	{
		launch(args);
	}	
	
	/**
	 * Most important method that creates the scene for the app.
	 * Contains a header and subheader (for author), slider, button and charts.
	 */
	public VBox mainScene()
	{
		VBox rootVertical = new VBox();
		rootVertical.setId("root-vertical");
		
		Label title = new Label("Simulation");
		title.setId("label-title");
		rootVertical.getChildren().add(title);
		
		Label author = new Label("by Max Hoefl");
		author.setId("label-author");
		rootVertical.getChildren().add(author);
		
		HBox rootHorizontalControlSlider = new HBox();
		rootHorizontalControlSlider.setId("root-horizontal-control-slider");
		
		HBox rootHorizontalControlButton = new HBox();
		rootHorizontalControlButton.setId("root-horizontal-control-button");
		
		HBox rootHorizontalCharts = new HBox();
		rootHorizontalCharts.setId("root-horizontal-charts");
		
		XYChart.Series<Number, Number> seriesBreedC = createXYChartSeries("Breed C");
		XYChart.Series<Number, Number> seriesBreedNC = createXYChartSeries("Breed NC");
		XYChart.Series<Number, Number> seriesLostBreedC = createXYChartSeries("Lost Breed C");
		XYChart.Series<Number, Number> seriesLostBreedNC = createXYChartSeries("Lost Breed NC");
		XYChart.Series<Number, Number> seriesRegainedBreedC = createXYChartSeries("Regained Breed C");

		LineChart<Number, Number> countChart = createLineChart("Breed Count", "Time", "Count", 0, 15, 1, 0, 10000, 1000, seriesBreedC, seriesBreedNC);
		LineChart<Number, Number> lostChart = createLineChart("Lost Count (cumulative)", "Time", "Count", 0, 15, 1, 0, 50000, 5000, seriesLostBreedC, seriesLostBreedNC);
		LineChart<Number, Number> regainedChart = createLineChart("Regained Count (cumulative)", "Time", "Count", 0, 15, 1, 0, 50000, 5000, seriesRegainedBreedC);
		
		
		slider = createSlider(0.1, 2.9, 0.1);
		slider.setId("input-slider");
		HBox.setHgrow(slider, Priority.ALWAYS);
		Label sliderText = new Label("Brand factor:   ");
		sliderText.setTextFill(Color.WHITE);
		rootHorizontalControlSlider.getChildren().add(sliderText);
		rootHorizontalControlSlider.getChildren().add(slider);
		
		
		button = new Button("Start");
		rootHorizontalControlButton.getChildren().add(button);
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
		
		rootHorizontalCharts.getChildren().add(countChart);
		rootHorizontalCharts.getChildren().add(lostChart);
		rootHorizontalCharts.getChildren().add(regainedChart);
		
		rootVertical.getChildren().add(rootHorizontalControlSlider);
		rootVertical.getChildren().add(rootHorizontalControlButton);
		rootVertical.getChildren().add(rootHorizontalCharts);
		return rootVertical;
	}
	
	/**
	 * Implicitly called by the from Application inherited method <code>launch()</code>
	 */
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		File f = new File("css/line-charts.css");
		checkArgument(f.exists() && f.canRead(), "Css file does not exist or cannot read from it.");
		
		Scene scene = new Scene(mainScene(), 1200, 600);
		scene.getStylesheets().add("file:///" + f.getAbsolutePath());
		
		primaryStage.setTitle("ChartsMain");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
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
	
	/**
	 * Creates a hover node that appears when the user enters a datum from the <code>XYChart.Series</code>
	 */
	class HoverNode extends StackPane {
		/**
		 * @param value	Shows this value when <code>setOnMouseEntered</code> is called.
		 * @param paint The interface for the background color of the box that appears on the datum when <code>setOnMouseEntered</code> is called. 
		 * 				For example <code>javafx.scene.paint.Color.WHITE</code>.	
		 * @see javafx.scene.paint.Paint
		 * @see javafx.scene.paint.Color
		 */
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
