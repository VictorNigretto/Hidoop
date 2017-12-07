package application;

import hdfs.HdfsServer;
import ordo.DaemonImpl;

import java.io.IOException;

public class DaemonRunner extends Thread {

    private String nomDaemon;
    private String nomMachine;
    private int port;
    private String[] cmd;

	public DaemonRunner(String nomDaemon, int port, String nomMachine){
		this.nomDaemon = nomDaemon;
		this.nomMachine = nomMachine;
		this.port = port;
		cmd[0] =  nomDaemon;
		cmd[1] = nomMachine;
		cmd[2] = String.valueOf(port);
	}

	public void run() {
		
        DaemonImpl.main(cmd);
	}
}