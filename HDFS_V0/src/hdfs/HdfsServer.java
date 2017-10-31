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
	
	private static String fname;
	private static String fragFile;
	private static File file;
	
	public static void main (String[] args) throws IOException {
		
		int port = Integer.parseInt(args[0]);
		//port = 6666; // pour tester, a enlever
		
		Message<Commande> mCMD = new Message<Commande>();
		Message<String> mString = new Message<String>();

		//Pour tester
		/*file = new File ("test.txt");
		FileReader frr = new FileReader(file);
		char[] buff = new char[(int) file.length()];
		frr.read(buff);
		fragFile = new String(buff);
		frr.close();*/
		
		ServerSocket ss;
		
		ss = new ServerSocket(port);
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
					String ffname = mString.reception(ss);
					
					/*FileReader fr = new FileReader(file);
					char[] buf = new char[(int) file.length()];
					fr.read(buf);
					mString.send(new String(buf),ss);*/	
					mString.send(fragFile,ss);
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
				case CMD_DELETE:
					// Supprimer contenu fragFile du serveur ; gérer en lste(remove file)
					file.delete();
				default:
					break;
			}
		}
		
	}
	
}
