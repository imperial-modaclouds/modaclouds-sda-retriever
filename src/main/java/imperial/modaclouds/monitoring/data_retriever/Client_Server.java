package imperial.modaclouds.monitoring.data_retriever;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

public class Client_Server extends Application{
	
	/**
	 * The restlet component.
	 */
	private static Component component;
	
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
		data.removeMultiKey(resource, metricName);
		
		return result;
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
		component = new Component();
		
		component.getServers().add(Protocol.HTTP, Integer.valueOf(port));
		
		component.getClients().add(Protocol.FILE);
		
		Client_Server localObserver = new Client_Server();
		
		component.getDefaultHost().attach("",localObserver);
		
		try {
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This function is used to direct the received data to a specific class for processing
	 */
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		
		router.setDefaultMatchingMode(Template.MODE_EQUALS);
		
		router.attach("/results", Observer.class);
		
		return router;
	}

}
