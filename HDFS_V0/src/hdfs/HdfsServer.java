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

		Message<Commande> mCMD = new Message<Commande>();
		Message<String> mString = new Message<String>();
		Message<ArrayList<Object>> mList = new Message<ArrayList<Object>>();
		Message<Type> mType = new Message<Type>();

		ServerSocket ss;
		ss = new ServerSocket(port);
		System.out.println("Serveur démarré :)");
		while (true) {
			// Récupérer la commande demandé
			Commande cmd = (Commande) mCMD.reception(ss);
			// Traiter la commande reçu
			switch (cmd) {
				case CMD_READ:
					System.out.print(" Demande de lecture reçue ...");

					String ffname = mString.reception(ss);
					
					FileInputStream fis = new FileInputStream (ffname);
					ObjectInputStream ois = new ObjectInputStream (fis);
					try {
						System.out.println("coucou");
						ArrayList<Object> listToSend = (ArrayList<Object>) ois.readObject();
						mType.send((Type) (listToSend.get(0)),ss);
						mList.send(listToSend,ss);
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
					fname = mString.reception(ss);
					file = new File(fname);

					Type fmt = mType.reception(ss);
					//file.createNewFile();
					// Reception de la liste
					ArrayList<Object> listreceived = mList.reception(ss);

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
					String Fname = mString.reception(ss);
					File f = new File(Fname);
					f.delete();
					System.out.println("fichier supprimé");

				default:
					break;

			}

		}

	}
}
