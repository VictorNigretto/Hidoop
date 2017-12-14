package application;

import hdfs.HdfsServer;
import ordo.DaemonImpl;

import java.io.IOException;

public class DaemonRunner extends Thread {


    private String[] cmd;

	public DaemonRunner(String nomDaemon, int port, String nomMachine){
		String portStr = String.valueOf(port);
		String[] newcmd = {nomDaemon, portStr, nomMachine};
		this.cmd = newcmd;
	}

	public void run() {
		
        DaemonImpl.main(cmd);
	}
}