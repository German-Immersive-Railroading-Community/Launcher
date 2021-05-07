package com.troblecodings.launcher.assets;

public class LauncherClock extends Thread {
	
	public static boolean running = true;
	
	// don't know if this will work or if that file is even in the right package of the project XD
	public void run() {
		
		while(running) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
