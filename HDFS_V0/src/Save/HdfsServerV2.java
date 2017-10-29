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
	
	private static File fileRead;
	private static File fileWrite;
	private static FileInputStream fr;
	private static FileOutputStream fw;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	private static int indLine = 0;
	private static String lines[];
	private static Type format;
	private static String fname;
	
	public static void main (String[] args) throws IOException {
		
		
		Message m = new Message();
		ServerSocket ss;
		int port = 6666;
		ss = new ServerSocket(port);
		while (true) {
			// Récupérer la commande demandé
			Commande cmd = (Commande) m.reception(ss);
			// Traiter la commande reçu
			switch (cmd) {
				case CMD_OPEN_R:
					//Creer fichier lu
					fname = (String) m.reception(ss);
					fileRead = new File(fname);
					//Creer descripteur
					fr = new FileInputStream(fileRead);
					
					//ajout cond format line ou mauvaise endroit ; mettre dans FormatLine?
					// lecture texte en ligne
					int taille = (int) fileRead.length();
		   			byte[] buf = new byte[taille];
		   			fr.read(buf);
		   			String text = new String(buf);
		   			lines = text.split("\n");
					break;
					
				case CMD_OPEN_W:
					fname = (String) m.reception(ss);
					fileWrite = new File(fname + "-res");
					fw = new FileOutputStream(fileWrite,true);
					break;
				case CMD_CLOSE:
					break;
				case CMD_READ:
					format = (Type) m.reception(ss);
					if (Type.LINE == format) {
						m.send(new KV(String.valueOf(indLine+1),lines[indLine]), ss);
						indLine = (indLine + 1);
					} else if (Type.KV == format){
						// A FAIRE
					}
					fr.close();
					break;
				case CMD_WRITE:
					format = (Type) m.reception(ss);						
					if (Type.LINE == format) {
						KV kv = (KV) m.reception(ss);
						// Ecrire en fin du fichier
						fw.write((kv.k + KV.SEPARATOR + kv.v + "\n").getBytes());
					} else if (Type.KV == format){
						// A FAIRE
					}
					fw.close();
					break;
				default:
					break;
			}
		}
		
	}
	
}
