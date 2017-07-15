package com.sim.agent;

import scala.NotImplementedError;
import static com.google.common.base.Preconditions.*;
import static com.sim.app.SimulationUtils.*;

import com.sim.agent.Agent.BREED;

/**
 * TODO: Check for nulls in constructor (line String line), after split some of those variables must be checked.
 * @author Max Hoefl
 *
 */
public class AgentPlain implements Agent {

	private BREED breed;
	private String policyId;
	private int age;
	private int socialGrade;
	private int paymentAtPurchase;
	private double attrBrand;
	private double attrPrice;
	private double attrProm;
	private boolean autoRenew;
	private int inertia;
	
	private int countLostNC = 0;
	private int countLostC = 0;
	private int countRegainedC = 0;
	private BREED previousBreed = null;
	
	public AgentPlain(final BREED breed, final String policyId, final int age, final int socialGrade, final int paymentAtPurchase, final double attrBrand,
					final double attrPrice, final double attrProm, final boolean autoRenew, final int inertia)
	{
		this.breed = breed;
		this.policyId = policyId;
		this.age = age;
		this.socialGrade = socialGrade;
		this.paymentAtPurchase = paymentAtPurchase;
		this.attrBrand = attrBrand;
		this.attrPrice = attrPrice;
		this.attrProm = attrProm;
		this.autoRenew = autoRenew;
		this.inertia = inertia;
	}
	
	public AgentPlain(final String line)
	{
		checkNotNull(line, "Cannot construct AgentPlain from null String.");
		int count = -1;
		String[] split = line.split(",");
		
		BREED breed = BREED.getEnum(split[++count]);
		String policyId = split[++count];
		int age = Integer.parseInt(split[++count]);
		int socialGrade = Integer.parseInt(split[++count]);
		int paymentAtPurchase = Integer.parseInt(split[++count]);
		double attrBrand = Double.parseDouble(split[++count]);
		double attrPrice = Double.parseDouble(split[++count]);
		double attrProm = Double.parseDouble(split[++count]);
		boolean autoRenew = Boolean.parseBoolean(split[++count]);
		int inertia = Integer.parseInt(split[++count]);
		
		this.breed = breed;
		this.policyId = policyId;
		this.age = age;
		this.socialGrade = socialGrade;
		this.paymentAtPurchase = paymentAtPurchase;
		this.attrBrand = attrBrand;
		this.attrPrice = attrPrice;
		this.attrProm = attrProm;
		this.autoRenew = autoRenew;
		this.inertia = inertia;
	}

	@Override
	public void evolve(final double brandFactor) {

		this.age++;
		
		if(autoRenew) { return; }
		else 
		{
			double rand = Math.random() * 3d;
			checkArgument(compareDbl(attrPrice, 0d) != 0, "Division by zero attrPrice.");
			double affinity = (double) paymentAtPurchase/(double) attrPrice + (rand * attrProm * inertia);
			
			if(breed == BREED.C && affinity < (socialGrade * attrBrand))
			{
				previousBreed = BREED.C;
				breed = BREED.NC;
				countLostC++;
				
			}
			else if(breed == BREED.NC  && affinity < socialGrade * attrBrand * brandFactor)
			{				
				if(previousBreed == BREED.C)
				{
					countRegainedC++;
				}
				previousBreed = BREED.NC;
				breed = BREED.C;
				countLostNC++;
				
			}	        
		}
	}
	
	public BREED getBreed() {
		return breed;
	}

	public void setBreed(BREED breed) {
		this.breed = breed;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getPaymentAtPurchase() {
		return paymentAtPurchase;
	}

	public void setPaymentAtPurchase(int paymentAtPurchase) {
		this.paymentAtPurchase = paymentAtPurchase;
	}

	public double getAttrBrand() {
		return attrBrand;
	}

	public void setAttrBrand(double attrBrand) {
		this.attrBrand = attrBrand;
	}

	public double getAttrPrice() {
		return attrPrice;
	}

	public void setAttrPrice(double attrPrice) {
		this.attrPrice = attrPrice;
	}

	public double getAttrProm() {
		return attrProm;
	}

	public void setAttrProm(double attrProm) {
		this.attrProm = attrProm;
	}

	public boolean isAutoRenew() {
		return autoRenew;
	}

	public void setAutoRenew(boolean autoRenew) {
		this.autoRenew = autoRenew;
	}

	public int getInertia() {
		return inertia;
	}

	public void setInertia(int inertia) {
		this.inertia = inertia;
	}
	
	public int getCountLostNC() {
		return countLostNC;
	}
	
	public int getCountLostC() {
		return countLostC;
	}
	
	public int getCountRegainedC() {
		return countRegainedC;
	}
	
	

}
