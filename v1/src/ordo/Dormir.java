package ordo;

public class Dormir implements Runnable {

	
	public void run() {
		try {
			Thread.sleep(10000);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
