package formats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

import formats.Format.OpenMode;

public class LineFormat implements Format {
	
// Essayer de factoriser en classe générique classes envoi et reception

	private String fname;	// nom du fichier
//	private File file;		// fichier

	private int port = 6666;
	private long index = 1;
	
	// construire a chaque fois et fermé sock ou en attribut mais toujours ouvert ?
	private Socket sock;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	
	public LineFormat(String fname) {
		this.fname = fname;	
	}
	
	public void open(OpenMode mode) {
		try {
			if (mode == OpenMode.R) {
				sock = new Socket("localhost",port);
				oos = new ObjectOutputStream(sock.getOutputStream());
				oos.writeObject(Commande.CMD_OPEN_R);
				oos.close();
				sock.close();
				sock = new Socket("localhost",port);
				oos = new ObjectOutputStream(sock.getOutputStream());		
				oos.writeObject(fname);
				oos.close();
				sock.close();
			}
			if (mode == OpenMode.W) {
				sock = new Socket("localhost",port);
				oos = new ObjectOutputStream(sock.getOutputStream());
				oos.writeObject(Commande.CMD_OPEN_W);
				oos.close();
				sock.close();
				sock = new Socket("localhost",port);
				oos = new ObjectOutputStream(sock.getOutputStream());		
				oos.writeObject(fname);
				oos.close();
				sock.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			

	}
	
	public void close() {
		try {
			sock = new Socket("localhost",port);
			oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(Commande.CMD_CLOSE);
			oos.close();
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public KV read() {
		KV kv = new KV();
		try {
			sock = new Socket("localhost",port);
			oos = new ObjectOutputStream(sock.getOutputStream());		
			oos.writeObject(Commande.CMD_READ);
			oos.close();
			sock.close();
			sock = new Socket("localhost",port);
			oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(Type.LINE);
			oos.close();
			sock.close();
			sock = new Socket("localhost",port);
			ois = new ObjectInputStream(sock.getInputStream());
			kv = (KV) ois.readObject();
			ois.close();
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		index++;
		return kv;
	}

	@Override
	public void write(KV record) {
		try {
			sock = new Socket("localhost",port);
			oos = new ObjectOutputStream(sock.getOutputStream());		
			oos.writeObject(Commande.CMD_WRITE);
			oos.close();
			sock.close();
			sock = new Socket("localhost",port);
			oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(Type.LINE);
			oos.close();
			sock.close();
			sock = new Socket("localhost",port);
			oos = new ObjectOutputStream(sock.getOutputStream());		
			oos.writeObject(record);
			oos.close();
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public long getIndex() {
		return this.index;

	}

	@Override
	public String getFname() {
		return this.fname;

	}

	@Override
	public void setFname(String fname) {
		this.fname = fname;
	}
	
}
