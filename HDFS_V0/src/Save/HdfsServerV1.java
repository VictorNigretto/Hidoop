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
		
		// recuperer nom fichier et le creer avant de l'ouvrir fileread est fname, filewrite est fname-res
		
		ServerSocket ss;
		int port = 6666;
		ss = new ServerSocket(port);
		while (true) {
			try {
				Socket ssock = ss.accept();
				ois = new ObjectInputStream(ssock.getInputStream());
				Commande cmd = (Format.Commande) ois.readObject();
				ois.close();
				ssock.close();
				switch (cmd) {
					case CMD_OPEN_R:
						//Creer fichier lu
						ssock = ss.accept();
						ois = new ObjectInputStream(ssock.getInputStream());
						fname = (String) ois.readObject();
						ois.close();
						ssock.close();
						fileRead = new File(fname);
						//Creer descripteur
						fr = new FileInputStream(fileRead);
						//ajout cond format line ou mauvaise endroit? lecture texte en ligne
						int taille = (int) fileRead.length();
		    			byte[] buf = new byte[taille];
		    			fr.read(buf);
		    			String text = new String(buf);
		    			lines = text.split("\n");
						break;
					case CMD_OPEN_W:
						ssock = ss.accept();
						ois = new ObjectInputStream(ssock.getInputStream());
						fname = (String) ois.readObject();
						ois.close();
						ssock.close();
						fileWrite = new File(fname + "-res");
						// Créer descripteur
						fw = new FileOutputStream(fileWrite,true);
						break;
					case CMD_CLOSE:
						// Pas sur du close
						ssock.close();
						//fr.close();
						//fw.close();
						break;
					case CMD_READ:
						ssock = ss.accept();
						ois = new ObjectInputStream(ssock.getInputStream());
						format = (Type) ois.readObject();
						ois.close();
						ssock.close();
						ssock = ss.accept();
						oos = new ObjectOutputStream(ssock.getOutputStream());
						if (Type.LINE == format) {
							
							oos.writeObject(new KV(String.valueOf(indLine+1),lines[indLine]));
							// Lire jusqu'à la dernière ligne, attention lecture apres
							indLine = (indLine + 1);
						} else if (Type.KV == format){
							// A FAIRE
							oos.writeObject(new KV("",""));
						}
						oos.close();
						fr.close();
						ssock.close();
						break;
					case CMD_WRITE:
						ssock = ss.accept();
						ois = new ObjectInputStream(ssock.getInputStream());
						format = (Type) ois.readObject();
						ois.close();
						ssock.close();
						if (Type.LINE == format) {
							ssock = ss.accept();
							ois = new ObjectInputStream(ssock.getInputStream());
							KV kv = (KV) ois.readObject();
							ois.close();
							ssock.close();
							// Ecrire en fin du fichier fileWrite?
							fw.write((kv.k + KV.SEPARATOR + kv.v + "\n").getBytes());
						} else if (Type.KV == format){
							// A FAIRE
						}
						fw.close();
						break;
					default:
						break;
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
