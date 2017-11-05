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
	

	private static File file;
	private static Type fmt;
	
	public static void main (String[] args) throws IOException {
		
		int port = Integer.parseInt(args[0]);
		
		Message<Commande> mCMD = new Message<Commande>();
		Message<String> mString = new Message<String>();
		Message<Type> mType = new Message<Type>();
		Message<KV> mKV = new Message<KV>();

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
					mType.send(fmt, ss);
					FileInputStream fis = new FileInputStream(file);
					
					switch (fmt) {
						case LINE:
							byte[] buf = new byte[(int) file.length()];
							fis.read(buf);
							fis.close();
							mString.send(new String(buf),ss);
							break;
						case KV:
							ObjectInputStream ois = new ObjectInputStream(fis);				
							while (fis.available() != 0) {
								try {
									mKV.send((KV) ois.readObject(), ss);
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							mKV.send(null, ss);			
							ois.close();
							break;
					}
					fis.close();	
					break;
				case CMD_WRITE:
					// Recuperer write Hdfs Client
					
					// Modifier pour liste de fichiers,(contenu,non,file)
					// Creer le fichier lecture en dur
					fname = mString.reception(ss);
					file = new File(fname);
					//file.createNewFile();
					
					fmt = mType.reception(ss);
					switch (fmt) {
						case LINE:
							String fragFile = (String) mString.reception(ss);
							// Ecrire son contenu dans le fichier
							FileWriter fw = new FileWriter(file);
							fw.write(fragFile);
							fw.close();
							break;
						case KV:
			    			FileOutputStream fos = new FileOutputStream (file,true);
			    			ObjectOutputStream oos = new ObjectOutputStream (fos);
			    			KV kv = new KV();
							
			    			while ((kv = (KV) mKV.reception(ss)) != null) {
			    				oos.writeObject(kv);
			    			}

			    			oos.close();
			    			fos.close();
							break;
					}
					
					
					
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
