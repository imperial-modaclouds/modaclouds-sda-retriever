package imperial.modaclouds.monitoring.data_retriever;
import java.util.List;
import java.util.Map;

import imperial.modaclouds.monitoring.data_retriever.Client_Server;
import it.polimi.modaclouds.monitoring.metrics_observer.ResultsHandler;
import it.polimi.modaclouds.monitoring.metrics_observer.model.Variable;


public class MyResultHandler extends ResultsHandler  {

	/**
	 * Parse the received JSON data.
	 * 
	 * @param varNames the var names
	 * @param bindings the bindings of the JSON messages
	 */
	@Override
	public void getData(List<String> varNames, List<Map<String, Variable>> bindings) {
		
		// TODO Auto-generated method stub
		String metric = null;
		String metricValue = null;
		String resource = null;
		String timestamps = null;

		int count = 0;

		for (Map<String, Variable> m : bindings) {
			for (int i = 0; i < varNames.size(); i++) {
				System.out.println(varNames.get(i));
				System.out.println(m.get(varNames.get(i)).getValue());
				//System.out.println(m.get(varNames.get(i)).getValue());
				Variable var;
				var = m.get(varNames.get(i));
				switch (varNames.get(i)) {
				case "target":
					resource = var.getValue();
					count = count + 1;
					break;
				case "metric":
					metric = var.getValue();
					count = count + 1;
					break;
				case "input":
					metricValue = var.getValue();
					count = count + 1;
					break;
				case "timestamp":
					timestamps = var.getValue();
					count = count + 1;
					break;
				}
				if (count == 4) {
					Client_Server.push(resource, metric, metricValue, timestamps);
					System.out.println(resource+" "+metric+" "+metricValue+" "+timestamps);
					count = 0;
				}
			}

		}		
	}

}
