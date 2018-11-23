package com.cloud.tcup.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ObservationDataToTCUP {
	
	public void insertEMXObservation(List<EMXNodeData> observations){
		JSONObject observationJSON= tcupEMXObservationJSON(observations);
		try{
		Properties prop = new Properties();
		prop.load(new FileInputStream("C:\\ConnectedChillersConfig\\CloudToTCUPAdapter.properties"));
		String apikey = prop.getProperty("x-api-key");
		String observationurl = prop.getProperty("observationurl");
		/*---observationurl=https://in.tcupiot.com/api/sos/v2.0/observations---*/
		String obsRet= insertObservation(apikey, observationurl, observationJSON);
		if(!obsRet.equalsIgnoreCase("error")){
			CloudDataPull.logger.info("Observation inserted successfully ::::::::  "+obsRet);
		}else{
			//CloudDataPull.logger.error("Error in observation insertion ::::::::  "+obsRet);
			CloudDataPull.critical.error("Error in observation insertion ::::::::  "+obsRet);
			
		}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }

	
	@SuppressWarnings("unchecked")
	public static JSONObject tcupDXObservationJSON(List<DistronixNodeData> obsSublist) throws SQLException {
		int count=0;
		JSONObject observe = new JSONObject();
		observe.put("version", "1.0.1");
		JSONArray arr = new JSONArray();
		CloudDataPull.logger.debug("Build Observation JSON to insert observations in TCUP");
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("C:\\ConnectedChillersConfig\\CloudToTCUPAdapter.properties"));
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	//	NodeRegisteration nr = new NodeRegisteration();
		String channel=prop.getProperty("channelType");
		String[] channelType=channel.split(",");
			for(DistronixNodeData nodeObs:obsSublist){
				//String[] channelType= nr.getChannelList(nodeObs.getNodeId());
				for(int i=0;i<channelType.length-1;i++){
							JSONObject sensorOb = new JSONObject();
							String sensor=nodeObs.getNodeId()+"_"+channelType[i];
							String feature=nodeObs.getNodeId();
							sensorOb.put("sensor", sensor);
							sensorOb.put("feature", feature);
							// comment above line and uncomment below line before running in production/cloud
							//sensorOb.put("feature", feature);
							
							JSONArray arr2 = new JSONArray();
							
							JSONObject record = new JSONObject();
							record.put("starttime", nodeObs.getStarttime());
							//Read he config file for channel Types. i.e. voltage, Current, temp,hum, location,xxx
							JSONObject out = new JSONObject();
							//TODO - changes to be done based on hashmap implementation of Channeltype in pojo.
							/*
							if(channelType.contains("Voltage")){
								out.put("name", "Voltage");
								out.put("value", nodeObs.getVoltage());
								}
							else if(channelType.contains("Current")){
								out.put("name", "Current");
								out.put("value", nodeObs.getCurrent());
								}
							else if(channelType.contains("temperature")){
								out.put("name", "Temperature");
								out.put("value", nodeObs.getTemperature());
								}
							else if(channelType.contains("humidity")){
								out.put("name", "Humidity");
								out.put("value", nodeObs.getHumidity());
								}	
							else */
							if(!channelType[i].contains("location")){
								out.put("name",channelType[i]);
								HashMap<String, Object> channelName=nodeObs.getChannelTypeKeyValueHM();
									Object value = channelName.get(channelType[i]);
									if (value != null) {
										out.put("value",value);
									}
							}else{
								out.put("name", "Latitude");
								out.put("value", nodeObs.getLatitude());
								JSONObject out1 = new JSONObject();
								out1.put("name", "Longitude");
								out1.put("value", nodeObs.getLongitude());
								arr2.add(out1);
							}		
							arr2.add(out);
							record.put("output", arr2);
							JSONArray arr1 = new JSONArray();
							arr1.add(record);
							sensorOb.put("record", arr1);
							arr.add(sensorOb);
							count++;
						}
					}
	observe.put("observations",arr);
	CloudDataPull.logger.debug("count of observation created for TCUP:"+count);
	CloudDataPull.cloudJson.fatal(observe);
	return observe;

	}
//}


	
	@SuppressWarnings("unchecked")
	public JSONObject tcupEMXObservationJSON(List<EMXNodeData> observation){
		int count=0;
		JSONObject observe = new JSONObject();
		observe.put("version", "1.0.1");
		JSONArray arr = new JSONArray();
		for(EMXNodeData nodeObs:observation){
			if((nodeObs.getObsType()).startsWith("Voltage") || (nodeObs.getObsType()).startsWith("Current")||nodeObs.getObsType().startsWith("Humidity")||nodeObs.getObsType().startsWith("Temperature")){
					JSONObject record = new JSONObject();
					record.put("starttime", nodeObs.getStarttime());
					
					JSONObject out = new JSONObject();
				/*	if((nodeObs.getObsType()).startsWith("Energy")){
						out.put("name", "Energy");
					}
					else if((nodeObs.getObsType()).startsWith("Power")){
						out.put("name", "Power");
						}
					else */if((nodeObs.getObsType()).startsWith("Voltage")){
						out.put("name", "Voltage");
						}
					else if((nodeObs.getObsType()).startsWith("Current")){
						out.put("name", "Current");
						}
					else if((nodeObs.getObsType()).startsWith("Temperature")){
						out.put("name", "Temperature");
						}
						else if((nodeObs.getObsType()).startsWith("Humidity")){
						out.put("name", "Humidity");
						}/*
					else if((nodeObs.getObsType()).startsWith("Frequency")){
						out.put("name", "Frequency");
						}
					else if((nodeObs.getObsType()).startsWith("VAR")){
						out.put("name", "VA");
						}
					
					else{
						System.out.println("no parameter type matches");
					}*/
					out.put("value", nodeObs.getObsValue());
								
					JSONArray arr2 = new JSONArray();
					arr2.add(out);
					record.put("output", arr2);
					
					JSONArray arr1 = new JSONArray();
					arr1.add(record);
						
						JSONObject sensorOb = new JSONObject();
						String sensor=nodeObs.getNodeId()+"_"+nodeObs.getObsType();
						sensorOb.put("sensor", sensor);
						sensorOb.put("record", arr1);
						count++;
						arr.add(sensorOb);
			}
		}
		//System.out.println(arr);
	observe.put("observations",arr);
	CloudDataPull.logger.debug("count of observation created for TCUP:"+count);
	CloudDataPull.cloudJson.fatal("TCUP Feature Registeration JSON : "+observe.toString());
	return observe;
}
	
	public static String insertObservation(String apiKey,String observationurl,JSONObject tcupObservationJSON){
        try {
        	CloudDataPull.logger.debug("Connecting with TCUP to insert observations");
			String posturl = observationurl;
			System.setProperty("https.proxyHost", "proxy.tcs.com");
			System.setProperty("https.proxyPort", "8080");
			System.getProperties().put("https.proxySet", "true");
			System.setProperty("http.proxyHost", "proxy.tcs.com");
			System.setProperty("http.proxyPort", "8080");
			System.setProperty("https.protocols", "TLSv1.2");
			URL url = new URL(posturl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("x-api-key", apiKey);
			conn.addRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(tcupObservationJSON.toString());
			wr.close();
			if (conn.getResponseCode() != 200) {
				CloudDataPull.logger.error("Failure Connection from TCUP to Insert Observations Response Code: "+conn.getResponseCode()+" message: "+conn.getResponseMessage());
				CloudDataPull.critical.error("Failure Connection from TCUP to Insert Observations Response Code: "+conn.getResponseCode()+" message: "+conn.getResponseMessage());
				String errStr = "";
				errStr = conn.getResponseMessage().trim();
				InputStream is = null; 
				is = conn.getErrorStream();
				//System.out.println(is.read());
				//is.close();
				BufferedReader _error_ = new BufferedReader(new InputStreamReader(is));
				String line = null;
				if((line=_error_.readLine())!=null){
					errStr = line.trim();
					CloudDataPull.logger.error(errStr);
				}
				_error_.close();
				is.close();
				conn.disconnect();
				return "error";
			}else{
				CloudDataPull.logger.debug("Success Connection from TCUP to Insert Observations Response Code: "+conn.getResponseCode()+" message: "+conn.getResponseMessage());
				String ret = "";
				InputStream is = null; 
				is = conn.getInputStream();
				
				BufferedReader isb = new BufferedReader(new InputStreamReader(is));
				String line = null;
				if((line=isb.readLine())!=null){
					ret = line;
				}
				isb.close();
				is.close();
				conn.disconnect();
				return ret;
			}
		
		}catch (Exception e) {
			e.printStackTrace();
			CloudDataPull.critical.error(e.getClass().getName()+": "+e.getMessage());
			return "error";
		}
	}


}
