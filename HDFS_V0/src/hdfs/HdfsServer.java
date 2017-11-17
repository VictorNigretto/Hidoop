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
import java.util.ArrayList;
import java.util.Iterator;

import static formats.Format.Commande.CMD_DELETE;


public class HdfsServer {

	private static String fname;
	private static File file;

	public static void main(String[] args) throws IOException {

		int port = Integer.parseInt(args[0]);

		Message m = new Message();

		ServerSocket ss;
		ss = new ServerSocket(port);
		System.out.println("Serveur démarré :)");
		while (true) {
			// Récupérer la commande demandé
			m.openServer(ss);
			Commande cmd = (Commande) m.receive();
			// Traiter la commande reçu
			switch (cmd) {

				case CMD_READ:
					System.out.print(" Demande de lecture reçue ...");

					String fname = (String) m.receive();
					
					FileInputStream fis = new FileInputStream (fname);
					ObjectInputStream ois = new ObjectInputStream (fis);
					try {
						
						ArrayList<Object> listToSend = (ArrayList<Object>) ois.readObject();

						m.send(listToSend.get(0));
						m.send(listToSend);
						System.out.println("fragment du fichier envoyé");
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					fis.close();
					ois.close();
					break;
					
				case CMD_WRITE:
					System.out.print(" Demande d'écriture reçue ...");

					// Recuperer write Hdfs Client
					fname = (String) m.receive();
					file = new File(fname);

					Type fmt = (Type) m.receive();
					//file.createNewFile();
					// Reception de la liste

					ArrayList<Object> listreceived = (ArrayList<Object>) m.receive();
					// Ecrire son contenu dans le fichier

					FileOutputStream fos = new FileOutputStream(file);
					ObjectOutputStream oos = new ObjectOutputStream(fos);

					oos.writeObject(listreceived);

					oos.close();
					fos.close();
					System.out.println("fragment du fichier enregistré");
					break;
					
				case CMD_DELETE:
					// Supprimer contenu fragFile du serveur ; gérer en lste(remove file)
					System.out.print("Demande de suppression reçue ...");
					String Fname = (String) m.receive();
					File f = new File(Fname);
					f.delete();
					System.out.println("fichier supprimé");

				default:
					break;

			}
		//m.close(ss); ?
		}

	}
}
