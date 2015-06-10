package imperial.modaclouds.monitoring.data_retriever;

import it.polimi.modaclouds.monitoring.metrics_observer.MonitoringDatum;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class JSONParser {

	private static Gson gson = new Gson();
	
	public static List<MonitoringDatum> jsonToMonitoringDatum(String json) throws IOException {
		
		JsonReader reader = null;
		reader = new JsonReader(new StringReader(json));
		Type type = new TypeToken<List<MonitoringDatum>>() {}.getType();
		List<MonitoringDatum> jsonMonitoringData = gson.fromJson(reader, type);
		
		System.out.println(jsonMonitoringData.size());
		
		return jsonMonitoringData;
	}

	public static List<MonitoringDatum> fromJson(Representation json) throws IOException {
		String jsonText = json.getText();
		return jsonToMonitoringDatum(jsonText);
	}
}
