package com.sim.app.common;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class DataResource {

	public static final String PATH_IRIS = "/Users/mh/workspaceSpark/spark-big-data-analytics/spark-big-data-analytics/data/iris.csv";
	public static final String PATH_TWEETS = "/Users/mh/workspaceSpark/spark-big-data-analytics/spark-big-data-analytics/data/movietweets.csv";
	public static final String PATH_SMS = "/Users/mh/workspaceSpark/spark-big-data-analytics/spark-big-data-analytics/data/sms_spam_short.csv";
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
