package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Message {

	private Socket sock;			// Socket faisant le lien entre client et serveur
	private ObjectOutputStream oos;	// descripteur d'écriture d'objets
	private ObjectInputStream ois;	// descripteur de lecture d'objets
	
	// Ouvre le Socket du client vers le serveurs et les descripteurs d'écriture et de lecture
	public ObjectOutputStream openClient(int port) {
		try {
			 sock = new Socket("localhost",port);
			 oos = new ObjectOutputStream(sock.getOutputStream());
			 ois = new ObjectInputStream(sock.getInputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return oos;
	}
	
	// Ouvre le Socket du serveur vers le client et les descripteurs d'écriture et de lecture
	public ObjectOutputStream openServer(ServerSocket ss) {
		try {
			 sock = ss.accept();
			 oos = new ObjectOutputStream(sock.getOutputStream());
			 ois = new ObjectInputStream(sock.getInputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return oos;
	}

	// Ecrit un objet en paramètre dans le descripteur d'écriture
	public void send(Object objet) {
		try {
			oos.writeObject(objet);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Lit un objet présent dans le descripteur de lecture
	public Object receive() {
		Object objet = null;
		try {
			objet = (Object) ois.readObject();
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
	
	// Ferme les descripteurs et le Socket
	public void close() {
		try {
			ois.close();
			oos.close();
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
