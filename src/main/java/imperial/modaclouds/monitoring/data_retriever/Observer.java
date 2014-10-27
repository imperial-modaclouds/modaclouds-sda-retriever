package imperial.modaclouds.monitoring.data_retriever;

import it.polimi.modaclouds.monitoring.metrics_observer.MetricsObServer;

public class Observer extends MetricsObServer {

	public Observer(int listeningPort) {
		super(listeningPort, "/v1/results", MyResultHandler.class);
	}
}
