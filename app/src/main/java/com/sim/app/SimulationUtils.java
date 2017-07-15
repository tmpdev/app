package com.sim.app;

import java.math.BigDecimal;

import org.apache.spark.api.java.JavaRDD;

import com.sim.app.common.DataResource;

public class SimulationUtils {
	
	protected static JavaRDD<String> loadData(final boolean keepHeader)
	{
		JavaRDD<String> resRDD = DataResource.getCSVData(DataResource.PATH_SIM);
		if(!keepHeader)
		{
			String header = resRDD.first();
			resRDD = resRDD.filter(s -> !s.equals(header));
		}
		return resRDD;
	}
	
	public static int compareDbl(final double d1, final double d2)
	{
		BigDecimal b1 = BigDecimal.valueOf(d1);
		BigDecimal b2 = BigDecimal.valueOf(d2);
		return b1.compareTo(b2);
	}
}
