package com.sim.app;

public class SimulationRecord
{
	private long countLostC;
	private long countLostNC;
	private long countRegainedC;
	private long countC;
	private long countNC;
	
	public SimulationRecord(final long countLostC, final long countLostNC, final long countRegainedC, final long countC, final long countNC)
	{
		this.countLostC = countLostC;
		this.countLostNC = countLostNC;
		this.countRegainedC = countRegainedC;
		this.countC = countC;
		this.countNC = countNC;
	}
	
	public static class Builder
	{
		private long countLostC;
		private long countLostNC;
		private long countRegainedC;
		private long countC;
		private long countNC;
		
		public Builder() {};
		
		public Builder setCountLostC(final long countLostC)
		{
			this.countLostC = countLostC;
			return this;
		}
		
		public Builder setCountLostNC(final long countLostNC)
		{
			this.countLostNC = countLostNC;
			return this;
		}
		
		public Builder setCountRegainedC(final long countRegainedC)
		{
			this.countRegainedC = countRegainedC;
			return this;
		}
		
		public Builder setCountC(final long countC)
		{
			this.countC = countC;
			return this;
		}
		
		public Builder setCountNC(final long countNC)
		{
			this.countNC = countNC;
			return this;
		}
		
		public SimulationRecord build()
		{
			return new SimulationRecord(countLostC, countLostNC, countRegainedC, countC, countNC);
		}
	}

	public long getCountLostC() {
		return countLostC;
	}

	public long getCountLostNC() {
		return countLostNC;
	}

	public long getCountRegainedC() {
		return countRegainedC;
	}
	
	public long getCountC() {
		return countC;
	}
	
	public long getCountNC() {
		return countNC;
	}
}
