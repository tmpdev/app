package com.sim.app;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * Utilities for charting.
 * 
 * @See App.java
 * @author Max Hoefl
 *
 */
public class AppUtils {
	
	/**
	 * Creates a line chart containing the array of series provided by @param dataseries.
	 * Is used in App.java.
	 * 
	 * @param chartName Will appear on top of the chart
	 * @param xLabel	Label on x-axis
	 * @param yLabel	Label on y-axis
	 * @param xlimLower	Lower limit of x-axis
	 * @param xlimUpper Upper limit of x-axis
	 * @param xTickSize Tick size on x-axis
	 * @param ylimLower Lower limit on y-axis
	 * @param ylimUpper	Upper limit on y-axis
	 * @param yTickSize Ticker size on y-axis
	 * @return <code>LineChart<Number, Number></code> object
	 */
	@SafeVarargs
	public static LineChart<Number, Number> createLineChart(final String chartName, final String xLabel, final String yLabel, 
															 final int xlimLower, final int xlimUpper, final int xTickSize,
															 final int ylimLower, final int ylimUpper, final int yTickSize,
															 final XYChart.Series<Number, Number>... dataseries)
	{
		NumberAxis xAxis = new NumberAxis(xLabel, xlimLower, xlimUpper, xTickSize);
		NumberAxis yAxis = new NumberAxis(yLabel, ylimLower, ylimUpper, yTickSize);
		
		LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
		chart.setTitle(chartName);
		chart.setLegendVisible(true);
		
		for(XYChart.Series<Number, Number> series : dataseries)
		{
			chart.getData().add(series);
		}
		return chart;
	}
	
	/**
	 * Creates an XY series that is then used in <code>createLineChart()</code>.
	 * 
	 * @param seriesName  Will appear as legend name for this XY series
	 * @return	<code>XYChart.Series<Number, Number></code> object
	 */
	public static XYChart.Series<Number, Number> createXYChartSeries(final String seriesName)
	{
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.setName(seriesName);
		return series;
	}
}
