package imperial.modaclouds.monitoring.data_retriever;
import java.util.List;
import java.util.Map;

import imperial.modaclouds.monitoring.data_retriever.Client_Server;
import it.polimi.modaclouds.monitoring.metrics_observer.MonitoringDatum;
import it.polimi.modaclouds.monitoring.metrics_observer.MonitoringDatumHandler;


public class MyResultHandler extends MonitoringDatumHandler   {

	/**
	 * Parse the received JSON data.
	 * 
	**/

	@Override
	public void getData(List<MonitoringDatum> monitoringData) {
		for (MonitoringDatum monitoringDatum : monitoringData) {
			String metric = monitoringDatum.getMetric();
			String metricValue = monitoringDatum.getValue();
			String resource = monitoringDatum.getResourceId();
			String timestamps = monitoringDatum.getTimestamp();
			Client_Server.push(resource, metric, metricValue, timestamps);
			System.out.println(resource+" "+metric+" "+metricValue+" "+timestamps);
			
		}
	}

}
