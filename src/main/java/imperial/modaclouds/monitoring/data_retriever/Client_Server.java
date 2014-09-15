package imperial.modaclouds.monitoring.data_retriever;

import java.util.ArrayList;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.restlet.Application;

public class Client_Server extends Application{	
	
	/**
	 * The multi key map to hold the received data.
	 */
	@SuppressWarnings("rawtypes")
	public static MultiKeyMap data;
		
	/**
	 * This function is used to obtain the monitoring data given target resource and metric name.
	 * @param resource	the monitoring resource id
	 * @param metricName	the metric name
	 * @return the ValueSet containing the timestamps and values
	 */
	public static synchronized ValueSet obtainData(String resource, String metricName) {
		
		if (data == null)
			return null;
		
		ValueSet result = (ValueSet) data.get(resource,metricName);
		//data.removeMultiKey(resource, metricName);
		
		return result;
	}
	
	public static synchronized void removeOldData(long t0) {
		MapIterator it = data.mapIterator();
		
		while ( it.hasNext() ) {
			it.next();
			
			MultiKey mk = (MultiKey) it.getKey();
			ValueSet result = (ValueSet) it.getValue();
			
			ArrayList<String> timestamps = result.getTimestamps();
			ArrayList<String> values = result.getValues();
			for (int i = 0; i < timestamps.size(); i++) {
				long timestamp_long = Long.valueOf(timestamps.get(i)).longValue();
				if (timestamp_long < t0) {
					timestamps.remove(i);
					values.remove(i);
				}
			}
			
			result.setTimestamps(timestamps);
			result.setValues(values);
			
			data.put(mk, result);
		}
	}
	
	/**
	 * This function is used to push one metric value to the data variable
	 * @param resource	the monitoring resource id
	 * @param metricName	 the metric name
	 * @param value	the metric value
	 * @param timestamps the timestamps for the metric
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static synchronized void push (String resource, String metricName, String value, String timestamps) {
		if (data == null) {
			data = new MultiKeyMap();
			data.put(resource, metricName, new ValueSet(value, timestamps));
		}
		else {
			ValueSet valueSet = (ValueSet) data.get(resource, metricName);
			if (valueSet == null) {
				data.put(resource, metricName, new ValueSet(value, timestamps));
			}
			else {
				valueSet.add(value, timestamps);
				data.put(resource, metricName, valueSet);
			}
		}
	}
	
	/**
	 * This function starts to collect the monitoring data from DDA
	 */
	public static void retrieve(int port) {	
		
		HistoryRemoving history = new HistoryRemoving(System.currentTimeMillis(), 60*60*1000);
		history.start();
		
		Observer observer = new Observer(Integer.valueOf(port));
		try {
			observer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function starts to collect the monitoring data from DDA
	 */
	public static void retrieve(int port, long period) {	
		
		HistoryRemoving history = new HistoryRemoving(System.currentTimeMillis(),period);
		history.start();
		
		Observer observer = new Observer(Integer.valueOf(port));
		try {
			observer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
