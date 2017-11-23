package application;

import hdfs.HdfsServer;
import ordo.DaemonImpl;

import java.io.IOException;

public class DaemonRunner extends Thread {

    String[] cmd;

	public DaemonRunner(String[] cmd){
		this.cmd = cmd;
	}

	public void run() {
        DaemonImpl.main(cmd);
	}
}