package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Message<T> {
	

	public void send(T objet,int port) {
		Socket sock;
		try {
			sock = new Socket("localhost",port);
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(objet);
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
	
	public void send(T objet, ServerSocket ss) {
		Socket ssock;
		try {
			ssock = ss.accept();
			ObjectOutputStream oos = new ObjectOutputStream(ssock.getOutputStream());
			oos.writeObject(objet);
			oos.close();
			ssock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public T reception(int port) {
		T objet = null;
		try {
			Socket sock = new Socket("localhost",port);
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			objet = (T) ois.readObject();
			ois.close();
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objet;
	}
	
	public T reception(ServerSocket ss) {
		T objet = null;
		try {
			Socket ssock = ss.accept();
			ObjectInputStream ois = new ObjectInputStream(ssock.getInputStream());
			objet = (T) ois.readObject();
			ois.close();
			ssock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objet;
	}
	
}
