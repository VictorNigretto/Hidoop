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
		
		
		Message<Commande> mCMD = new Message<Commande>();
		Message<String> mString = new Message<String>();

		//Pour tester
		File fileRead = new File ("test.txt");
		FileReader fr = new FileReader(fileRead);
		char[] buf = new char[(int) fileRead.length()];
		fr.read(buf);
		fragFile = new String(buf);
		fr.close();
		
		ServerSocket ss;
		int port = 6666;
		ss = new ServerSocket(port);
		while (true) {
			// Récupérer la commande demandé
			Commande cmd = (Commande) mCMD.reception(ss);
			// Traiter la commande reçu
			switch (cmd) {
				case CMD_OPEN_R:
					// Envoyer le contenu fragFile
					mString.send(fragFile, ss);
					break;
				case CMD_READ:
					break;
				case CMD_WRITE:
					// Recuperer write Hdfs Client
					
					// Modifier pour liste de fichiers,(contenu,non,file)
					// Creer le fichier lecture en dur
					fname = mString.reception(ss);
					file = new File(fname);
					fragFile = (String) mString.reception(ss);
					// Ecrire son contenu dans le fichier
					FileWriter fw = new FileWriter(file);
					fw.write(fragFile);
					fw.close();
				case CMD_DELETE:
					// Supprimer contenu fragFile du serveur ; gérer en lste(remove file)
					fragFile = null;
				default:
					break;
			}
		}
		
	}
	
}
