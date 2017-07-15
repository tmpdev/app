package com.sim.app;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.util.LongAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sim.agent.*;
import com.sim.agent.Agent.BREED;
import com.sim.app.common.SparkConnection;

import static com.google.common.base.Preconditions.*;
import static com.sim.app.SimulationUtils.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs simulation on spark cluster (localhost). Spark configuration is specified <code>com.sim.app.common.SparkConnection</code>.
 * Data is retrieved from <code>DataResource.PATH_SIM</code> and the <code>Agent</code>s are stored in a <code>JavaRDD</code>
 * on which certain transformations are performed.
 * 
 * Simulation is performed by <code>simulate()</code>
 * Results are stored in a list of <code>com.sim.app.SimulationRecord</code>s and can be used for charting in App.java.
 * 
 * @author Max Hoefl
 */
public class Simulator implements Serializable{

	private static final long serialVersionUID = 87067650863877846L;
	private final JavaSparkContext spCtxt;
	private JavaRDD<Agent> simulationResult = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(Simulator.class);
	
	public Simulator()
	{
		this.spCtxt = SparkConnection.getContext();
	}
	
	/**
	 * Run simulation for @param numYears. 
	 * 
	 * @param numYears		Number of years to run the simulation for
	 * @param brandFactor	is a parameter that influences the <code>evolve()</code> method in each <code>Agent</code>
	 * @return <code>List<SimulationRecord</code> that contains per list item the number of <code>Agent</code>s from each <code>BREED</code>,
	 * 			the number of <code>BREED</code>s lost so far and the number of <code>BREED.C</code>s regained.
	 * @see com.sim.agent.Agent
	 * @see com.sim.agent.Agent.BREED
	 * @see com.sim.app.SimulationRecord
	 */
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
	
	/**
	 * Function to filter <code>JavaRDD</code> for <code>Agent.BREED</code>.
	 */
	public static class BreedFilter implements Function<Agent, Boolean>
	{
		private static final long serialVersionUID = -3802744511396165538L;
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
}
