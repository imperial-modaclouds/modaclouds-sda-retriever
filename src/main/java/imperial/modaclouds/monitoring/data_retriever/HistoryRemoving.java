package imperial.modaclouds.monitoring.data_retriever;

public class HistoryRemoving implements Runnable{

	private long startTime;
	
	private Thread hisr;
	
	public HistoryRemoving(long startTime) {
		this.startTime = startTime;
	}
	
	public void start() {
		hisr = new Thread( this, "his-rem");
		hisr.start();
	}
	
	@Override
	public void run() {
		long t1 = System.currentTimeMillis();
		
		if (t1 - startTime > 30*60*1000) {
			Client_Server.removeOldData(startTime);
			startTime = startTime + 5*60*1000;
		}
		else {
			try {
				Thread.sleep(5*60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}