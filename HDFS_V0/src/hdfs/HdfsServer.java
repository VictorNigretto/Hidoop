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
	private static File file;
	
	public static void main (String[] args) throws IOException {
		
		int port = Integer.parseInt(args[0]);
		
		Message<Commande> mCMD = new Message<Commande>();
		Message<String> mString = new Message<String>();

		ServerSocket ss;
		ss = new ServerSocket(port);
		String fname;
		while (true) {
			// Récupérer la commande demandé
			Commande cmd = (Commande) mCMD.reception(ss);
			// Traiter la commande reçu
			switch (cmd) {
				case CMD_OPEN_R:
					// Envoyer le path fragFile
					// Gérer plusieus fichiers
					mString.send(file.getAbsolutePath(), ss);
					break;
				case CMD_OPEN_W:
					// Envoyer le path fragFile
					// Gérer plusieurs fichiers
					fname = mString.reception(ss);
					File fileRes = new File(fname + "-res");
					mString.send(fileRes.getAbsolutePath(), ss);
					break;
				case CMD_READ:
					// nom utile pour récupèrer le bon fichier si il y en a plusieurs
					fname = mString.reception(ss);
					file = new File(fname);
					FileReader fr = new FileReader(file);
					char[] buf = new char[(int) file.length()];
					fr.read(buf);
					fr.close();
					mString.send(new String(buf),ss);
					//mString.send(fragFile,ss);
					
					break;
				case CMD_WRITE:
					// Recuperer write Hdfs Client
					
					// Modifier pour liste de fichiers,(contenu,non,file)
					// Creer le fichier lecture en dur
					fname = mString.reception(ss);
					file = new File(fname);
					//file.createNewFile();
					fragFile = (String) mString.reception(ss);
					// Ecrire son contenu dans le fichier
					FileWriter fw = new FileWriter(file);
					fw.write(fragFile);
					fw.close();
					break;
				case CMD_DELETE:
					// Supprimer contenu fragFile du serveur ; gérer en lste(remove file)
					fname = mString.reception(ss);
					File f = new File(fname);
					f.delete();
					break;
				default:
					break;
			}
		}
		
	}
	
}
