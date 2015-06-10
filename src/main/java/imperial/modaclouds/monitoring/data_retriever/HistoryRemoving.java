package imperial.modaclouds.monitoring.data_retriever;

public class HistoryRemoving implements Runnable{

	private long startTime;
	
	private long period;
	
	private Thread hisr;
	
	public HistoryRemoving(long startTime, long period) {
		this.startTime = startTime;
		this.period = period;
	}
	
	public void start() {
		hisr = new Thread( this, "his-rem");
		hisr.start();
	}
	
	@Override
	public void run() {
		long t1 = System.currentTimeMillis();
		
		if (t1 - startTime > period) {
			Client_Server.removeOldData(t1-period);
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
