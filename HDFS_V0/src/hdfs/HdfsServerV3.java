package hdfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import formats.Format;
import formats.Format.Commande;
import formats.Format.Type;
import formats.KV;
import util.Message;

public class HdfsServer {
	
	private static String fragFile;
	
	public static void main (String[] args) throws IOException {
		
		
		Message<Commande> mCMD = new Message<Commande>();
		Message<String> mString = new Message<String>();

		ServerSocket ss;
		int port = 6666;
		ss = new ServerSocket(port);
		while (true) {
			// Récupérer la commande demandé
			Commande cmd = (Commande) mCMD.reception(ss);
			// Traiter la commande reçu
			switch (cmd) {
				case CMD_READ:
					// Envoyer le contenu fragFile
					mString.send(fragFile, ss);
					break;
				case CMD_WRITE:
					// Recupere write Hdfs Client
					fragFile = (String) mString.reception(ss);
				case CMD_DELETE:
					// Supprimer contenu fragFile du serveur
					fragFile = null;
				default:
					break;
			}
		}
		
	}
	
}
