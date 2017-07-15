package com.sim.agent;

/**
 * TODO: Docs
 * 
 * @author Max Hoefl
 *
 */
public interface Agent
{
	enum BREED
	{
		C, NC;
		
		@Override
	    public String toString() {
			switch(this)
			{
				case C: return "BREED_C";
				case NC: return "BREED_NC";
				default: throw new IllegalStateException("Breed invalid.");
			}
	    }
		
		public static BREED getEnum(final String s)
		{
			switch(s.toUpperCase())
			{
				case "BREED_C": return C;
				case "BREED_NC": return NC;
				default: throw new IllegalArgumentException("Breed " + s + " invalid.");
			}
		}
	}
	
	void evolve(double brandFactor);
}
