package com.sim.app;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.util.LongAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sim.agent.*;
import com.sim.agent.Agent.BREED;
import com.sim.app.common.DataResource;
import com.sim.app.common.SparkConnection;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.google.common.base.Preconditions.*;
import static com.sim.app.SimulationUtils.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Simulator implements Serializable{

	private final JavaSparkContext spCtxt;
	private JavaRDD<Agent> simulationResult = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(Simulator.class);
	
	public Simulator()
	{
		this.spCtxt = SparkConnection.getContext();
	}
	
	public List<SimulationRecord> simulate(final int numYears, final double brandFactor)
	{
		checkArgument(numYears >= 0, "Non-negative number of years required.");
		checkArgument(compareDbl(brandFactor, 0.1d) >= 0 && compareDbl(brandFactor, 2.9d) <= 0, "Brand factor must be between 0.1 and 2.9.");
		
		JavaRDD<String> baseRDD = loadData(false);
		JavaRDD<Agent> agentRDD = baseRDD.map(s -> new AgentPlain(s));
		
		List<SimulationRecord> res = new ArrayList<>();
		LongAccumulator countLostC = spCtxt.sc().longAccumulator();
		LongAccumulator countLostNC = spCtxt.sc().longAccumulator();
		LongAccumulator countRegainedC = spCtxt.sc().longAccumulator();
		
		for(int year = 0; year < numYears; year++)
		{
			LOG.info("YEAR: {}", year);
			countLostC.reset();
			countLostNC.reset();
			countRegainedC.reset();
			
			agentRDD = agentRDD.map(a -> { a.evolve(brandFactor); return a; });	
			
			long countC = agentRDD.filter(new BreedFilter(BREED.C)).count();
			long countNC = agentRDD.filter(new BreedFilter(BREED.NC)).count();
			agentRDD.foreach(a -> { AgentPlain agent = (AgentPlain) a; 
									countLostC.add(agent.getCountLostC()); 
									countLostNC.add(agent.getCountLostNC());
									countRegainedC.add(agent.getCountRegainedC());
								   });
		
			LOG.info("COUNT LOST C: {}", countLostC.value());
			LOG.info("COUNT LOST NC: {}", countLostNC.value());
			LOG.info("COUNT REGAINED C: {}", countRegainedC.value());
			
			res.add(new SimulationRecord.Builder()
									  .setCountC(countC)
									  .setCountNC(countNC)
									  .setCountLostC(countLostC.value())
									  .setCountLostNC(countLostNC.value())
									  .setCountRegainedC(countRegainedC.value())
									  .build());
		
		}
		return res;
	}

	
	public JavaRDD<Agent> getSimulationResult()
	{
		return simulationResult;
	}
	
	public static class CountsAccumulator implements Function<Agent, Agent>
	{		
		private final LongAccumulator accLostC;
		private final LongAccumulator accLostNC;
		private final LongAccumulator accRegainedC;
		
		public CountsAccumulator(final LongAccumulator accLostC, final LongAccumulator accLostNC, final LongAccumulator accRegainedC)
		{
			this.accLostC = accLostC;
			this.accLostNC = accLostNC;
			this.accRegainedC = accRegainedC;
		}
		
		@Override
		public Agent call(Agent agent) throws Exception {
			AgentPlain agentPlain = (AgentPlain) agent;
			accLostC.add(agentPlain.getCountLostC());
			accLostNC.add(agentPlain.getCountLostNC());
			accRegainedC.add(agentPlain.getCountRegainedC());
			return agent;
		}
	}
	
	
	public static class BreedFilter implements Function<Agent, Boolean>
	{
		private final BREED breed;
		public BreedFilter(final BREED breed)
		{
			this.breed = breed;
		}
		
		@Override
		public Boolean call(Agent agent) throws Exception {
			AgentPlain agentPlain = (AgentPlain) agent;
			return agentPlain.getBreed() == breed;
		}
	}
	
	
	
	/*public static void main(String[] args)
	{
		SparkOperationsSimudyne simudyne = new SparkOperationsSimudyne();
		simudyne.simulate(15, 1);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	/*@Override
	public void start(Stage primaryStage) throws Exception
	{
		VBox root = new VBox();
		
		NumberAxis xAxis = new NumberAxis("Time", 0, 20, 5);
		NumberAxis yAxis = new NumberAxis("Price", 0, 50, 5);
		
		XYChart.Series<Number, Number> seriesApple = new XYChart.Series<>();
		seriesApple.setName("Apple");
		seriesApple.getData().add(new XYChart.Data<Number, Number>(0.0, 33.0));
		seriesApple.getData().add(new XYChart.Data<Number, Number>(1.0, 35.0));
		seriesApple.getData().add(new XYChart.Data<Number, Number>(2.0, 34.0));
		seriesApple.getData().add(new XYChart.Data<Number, Number>(3.0, 31.0));
		seriesApple.getData().add(new XYChart.Data<Number, Number>(4.0, 33.0));
		seriesApple.getData().add(new XYChart.Data<Number, Number>(5.0, 35.0));
		seriesApple.getData().add(new XYChart.Data<Number, Number>(6.0, 37.0));
		seriesApple.getData().add(new XYChart.Data<Number, Number>(7.0, 40.0));
		
		LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
		chart.setTitle("Price of apples");
		chart.getData().add(seriesApple);
		chart.setLegendVisible(true);
		
		root.getChildren().add(chart);
		
		Scene scene = new Scene(root, 400,400);
		primaryStage.setTitle("ChartsMain");
		primaryStage.setScene(scene);
		primaryStage.show();
	}*/

	
}
