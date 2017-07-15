package com.sim.app.common;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession ;

import org.apache.spark.SparkConf;

public class SparkConnection {

	private static String appName = "Simulation";
	private static String sparkMaster = "local[2]";
	
	private static JavaSparkContext spContext = null;
	private static SparkSession sparkSession = null;
	private static String tempDir = "./data-warehouse/"; // this is not going to work
	
	private static void getConnection() {
		
		if (spContext == null) {	
			SparkConf conf = new SparkConf()
									.setAppName(appName)
									.setMaster(sparkMaster);
			
			System.setProperty("hadoop.home.dir", "/");	
			
			spContext = new JavaSparkContext(conf);
			
			sparkSession = SparkSession
								  .builder()
								  .appName(appName)
								  .master(sparkMaster)
								  .config("spark.sql.warehouse.dir", tempDir)
								  .getOrCreate();
		}
		
	}
	
	public static JavaSparkContext getContext() {
		
		if ( spContext == null ) {
			getConnection();
		}
		return spContext;
	}
	
	public static SparkSession getSession() {
		if ( sparkSession == null) {
			getConnection();
		}
		return sparkSession;
	}

}
