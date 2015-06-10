package imperial.modaclouds.monitoring.data_retriever;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

public class Observer extends Component {
	
	private Class resultHandler;

	public Observer(int listeningPort) {
		super();
		getServers().add(Protocol.HTTP, listeningPort);
		getClients().add(Protocol.FILE);
		this.resultHandler = MyResultHandler.class;
		getDefaultHost().attach("", new ObServerApp("/data"));
	}
	
	public class ObServerApp extends Application {
		private String observerPath;

		public ObServerApp(String observerPath) {
			this.observerPath = observerPath;
		}

		@Override
		public Restlet createInboundRoot() {
			Router router = new Router(getContext());
			router.setDefaultMatchingMode(Template.MODE_EQUALS);

			router.attach(observerPath, resultHandler);

			return router;
		}
	}
}
