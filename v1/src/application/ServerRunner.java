package application;

import hdfs.HdfsServer;

import java.io.IOException;

public class ServerRunner extends Thread {

    String[] cmd;

	public ServerRunner(String[] cmd){
		this.cmd = cmd;
	}

	public void run() {
		try {
			HdfsServer.main(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}