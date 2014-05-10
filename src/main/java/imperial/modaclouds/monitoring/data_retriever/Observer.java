package imperial.modaclouds.monitoring.data_retriever;

import it.polimi.modaclouds.monitoring.metrics_observer.model.Sparql_json_results;
import it.polimi.modaclouds.monitoring.metrics_observer.model.Variable;

import java.io.StringReader;
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
import com.google.gson.stream.JsonReader;


public class Observer extends ServerResource {

	/**
	 * The logger to log the information.
	 */
	private Logger logger = LoggerFactory.getLogger(Observer.class.getName());
	
	/**
	 * Retrieve data through this function.
	 * 
	 * @param entity the data received.
	 */
	@Post
	public void getData(Representation entity) {
						
		String results = null;
		JsonReader reader = null;
		Gson gson = new Gson();
				
		try {
			results = entity.getText();
			System.out.println(results);
			
			reader = new JsonReader(new StringReader(results));
			Sparql_json_results s = gson.fromJson(reader,
					Sparql_json_results.class);
			parseData(s.getHead().getVars(), s.getResults().getBindings());
			this.getResponse().setStatus(Status.SUCCESS_OK,
					"Result succesfully received");
			this.getResponse().setEntity(
					gson.toJson("Result succesfully received"),
					MediaType.APPLICATION_JSON);

		} catch (Exception e) {
			logger.error("Error while receiving results: " + results, e);
			this.getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,
					"Error while receiving data");
			this.getResponse().setEntity(
					gson.toJson("Error while receiving data"),
					MediaType.APPLICATION_JSON);
		} finally {
			this.getResponse().commit();
			this.commit();
			this.release();
		}
	}

	/**
	 * Parse the received JSON data.
	 * 
	 * @param varNames the var names
	 * @param bindings the bindings of the JSON messages
	 */
	public void parseData(List<String> varNames, List<Map<String, Variable>> bindings) {
		String currentDatum = null;
		String metric = null;
		String metricValue = null;
		String resource = null;
		
		int count = 0;
		
		for (Map<String, Variable> m : bindings) {
			String type = null;
			for (int i = 0; i < varNames.size(); i++) {
				Variable var;
				switch (i) {
				case 0:
					var = m.get(varNames.get(i));
					if (var != null) {
						String value = var.getValue();
						if (value.substring(value.indexOf('#') + 1).equals(currentDatum)) {
							count = count + 1;
						}
						else {
							currentDatum = value.substring(value.indexOf('#') + 1);
							count = 1;
						}
					}
					break;
				case 1:
					var = m.get(varNames.get(i));
					if (var != null) {
						String value = var.getValue();
						type = value.substring(value.lastIndexOf("/")+1);
					}
					break;
				case 2:
					var = m.get(varNames.get(i));
					if (var != null) {
						String value = var.getValue();
						
						switch (type) {
						case "metric":
							metric = value.substring(value.lastIndexOf("/")+1);
							break;
						case "value":
							metricValue = value;
							break;
						case "aboutResource":
							resource = value;
							break;
						}
					}
					if (count == 4) {
						Client_Server.push(resource, metric, metricValue, String.valueOf(System.currentTimeMillis()));
					}
					break;
				}
			}
			
		}
	}
}
