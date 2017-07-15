package com.sim.app;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sim.agent.AgentPlain;
import com.sim.agent.Agent.BREED;

/**
 * Unit tests for <code>com.sim.Agent.AgentPlain</code>
 * 
 * @author Max Hoefl
 */
public class Test_AgentPlain {

	private static final Logger LOG = LoggerFactory.getLogger(Test_AgentPlain.class);
	
	private AgentPlain agent;
	
	@Before
	public void setup()
	{
		agent = new AgentPlain(BREED.C, "1", 2, 3, 4, 5d, 6d, 7d, false, 8);
	}
	
	@Test
	public void A_checkEvolveToNCFromC() 
	{
		agent.setBreed(BREED.C);
		agent.setPaymentAtPurchase(0);
		agent.setAttrProm(0d);
		
		LOG.info("Switching from Brand C to brand NC");
		agent.evolve(1d);
		
		assertTrue("Breed should be NC", agent.getBreed() == BREED.NC);
	}
	
	@Test
	public void B_checkEvolveToCFromNC() 
	{
		agent.setBreed(BREED.NC);
		
		LOG.info("Switching from Brand NC to brand C");
		agent.evolve(1000d);
		
		assertTrue("Breed should be C", agent.getBreed() == BREED.C);
	}
	
	@Test
	public void C_checkDontChangeFromC() 
	{
		agent.setBreed(BREED.C);
		agent.setPaymentAtPurchase(1000);
		
		LOG.info("Not switching from C");
		agent.evolve(1d);
		
		assertTrue("Breed should be C", agent.getBreed() == BREED.C);
	}
	
	@Test
	public void C_checkDontChangeFromNC() 
	{
		agent.setBreed(BREED.NC);
		agent.setPaymentAtPurchase(1000);
		
		LOG.info("Not switching from NC");
		agent.evolve(1d);
		
		assertTrue("Breed should be NC", agent.getBreed() == BREED.NC);
	}
	
	@Test
	public void D_checkCounts() 
	{
		agent.setBreed(BREED.NC);
		
		LOG.info("NC -> C");
		agent.evolve(1000d);
		assertTrue("Breed should be C", agent.getBreed() == BREED.C);
		
		LOG.info("C -> NC");
		agent.setPaymentAtPurchase(0);
		agent.setAttrProm(0d);
		agent.evolve(1d);
		assertTrue("Breed should be NC", agent.getBreed() == BREED.NC);
		assertTrue("Loss count for C should be 1", agent.getCountLostC() == 1);
		
		LOG.info("NC -> NC");
		agent.setPaymentAtPurchase(1000);
		agent.setAttrProm(1d);
		agent.evolve(1d);
		assertTrue("Breed should be NC", agent.getBreed() == BREED.NC);
		assertTrue("Loss count for C should be 1", agent.getCountLostC() == 1);
		
		LOG.info("NC -> C");
		agent.setPaymentAtPurchase(1);
		agent.evolve(1000d);
		assertTrue("Breed should be C", agent.getBreed() == BREED.C);
		assertTrue("Loss count for C should be 1", agent.getCountLostC() == 1);
		assertTrue("Regain count for C should be 1", agent.getCountRegainedC() == 1);
	}

}
