package imperial.modaclouds.monitoring.data_retriever;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import imperial.modaclouds.monitoring.data_retriever.Client_Server;
import it.polimi.modaclouds.monitoring.metrics_observer.MonitoringDatum;
import it.polimi.modaclouds.monitoring.metrics_observer.MonitoringDatumHandler;


public class MyResultHandler extends ServerResource    {

	private static final Logger logger = LoggerFactory
			.getLogger(MonitoringDatumHandler.class.getName());
	private static Gson gson = new Gson();

	public void parseData(List<MonitoringDatum> monitoringData) {
		for (MonitoringDatum monitoringDatum : monitoringData) {
			String metric = monitoringDatum.getMetric();
			String metricValue = monitoringDatum.getValue();
			String resource = monitoringDatum.getResourceId();
			String timestamps = monitoringDatum.getTimestamp();
			Client_Server.push(resource, metric, metricValue, timestamps);
			System.out.println(resource+" "+metric+" "+metricValue+" "+timestamps);
			
		}
	}
	
	/**
	 * Parse the received JSON data.
	 * 
	**/
	@Post
	public void getData(Representation entity) {
		
		try {
			parseData(JSONParser.fromJson(entity));
			this.getResponse().setStatus(Status.SUCCESS_OK,
					"Monitoring datum succesfully received");
			this.getResponse().setEntity(
					gson.toJson("Monitoring datum succesfully received"),
					MediaType.APPLICATION_JSON);

		} catch (Exception e) {
			logger.error("Error while receiving monitoring data", e);
			this.getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,
					"Error while receiving monitoring data");
			this.getResponse().setEntity(
					gson.toJson("Error while receiving monitoring data"),
					MediaType.APPLICATION_JSON);
		} finally {
			this.getResponse().commit();
			this.commit();
			this.release();
		}
		
		
		
	}

}
