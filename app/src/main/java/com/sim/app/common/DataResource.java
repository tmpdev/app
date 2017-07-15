package com.sim.app.common;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class DataResource {

	public static final String PATH_SIM = "./data/Simudyne_Backend_Test.csv";
	
	public static JavaRDD<Integer> getCollData() {
		JavaSparkContext spContext = SparkConnection.getContext();
		List<Integer> data = Arrays.asList(3,6,3,4,8);
		JavaRDD<Integer> collData = spContext.parallelize(data);
		collData.cache();
		return collData;
	}
	
	public static JavaRDD<String> getCSVData(final String path)
	{
		JavaSparkContext spContext = SparkConnection.getContext();
		return spContext.textFile(path);
	}
}
