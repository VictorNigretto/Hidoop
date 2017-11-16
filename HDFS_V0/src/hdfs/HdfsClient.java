/* une PROPOSITION de squelette, incomplète et adaptable... */

package hdfs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import util.Message;

import formats.Format;
import formats.Format.Commande;
import formats.Format.Type;
import formats.KV;


public class HdfsClient {
	
	private static int servers[] = {6666,5555,4444};

    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    public static void HdfsDelete(String hdfsFname) {
		int nbServer = servers.length;
		System.out.println("Demande de suppression du fichier : " + hdfsFname + "..." );
		Message m = new Message();

		for (int i = 0; i < nbServer; i++) {
			m.openClient(servers[i]);
			m.send(Commande.CMD_DELETE);
			m.send(hdfsFname  + String.valueOf(i));
			m.close();
			System.out.println("envoyee au serveur " + i );
		}
	}
	
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, int repFactor) {
    	try {
    		Message m = new Message();

			File fichier = new File(localFSSourceFname);

			int nbServer = servers.length;

			if (fmt == Format.Type.LINE) {
				
				
				System.out.println("Demande d'écriture d'un fichier (LINE)...");

				FileReader fr = new FileReader(fichier);
				BufferedReader br = new BufferedReader(fr);

				//Lire ligne par ligne et compter
				int indLine = 1;
				while (br .readLine() != null) {
					indLine++;
				}
				int nbLine = indLine - 1;

				int quotient = nbLine/nbServer;
				int reste = nbLine%nbServer;

				br.close();
				fr.close();
				fr = new FileReader(fichier);
				br = new BufferedReader(fr);

				// Envoyer à chaque serveur, un fragment du fichier sous la forme d'une liste de String contenant une seule String
				for (int i=0 ; i<nbServer ; i++) {
					ArrayList<String> listS = new ArrayList<String>();
					int nbLineSent = quotient;
					if (reste != 0) {
						nbLineSent++;
						reste--;
					}
					for (int j = 0 ; j<nbLineSent ; j++) {
						listS.add(br.readLine());
					}
					m.openClient(servers[i]);
					m.send(Commande.CMD_WRITE);
					System.out.println("envoyée au serveur " + i);
					m.send(fichier.getName() + String.valueOf(i));
					m.send(Type.LINE);
					m.send(listS);
					m.close();
					System.out.println("fragment envoyé au serveur " + i);
				}
				br.close();
				fr.close();
				} else if (fmt == Format.Type.KV) {

				//Lire KV par KV et compter
					System.out.println("Demande d'écriture d'un fichier (KV)...");

					int indKV = 1;

					FileInputStream fis = new FileInputStream (localFSSourceFname);
					ObjectInputStream ois = new ObjectInputStream (fis);
					KV unKV;

					while (fis.available() > 0) {
						unKV = (KV) ois.readObject();
						System.out.println(unKV.toString());
						indKV++;
					}
					System.out.println(indKV);

					int nbKV = indKV - 1;

					int quotient = nbKV/nbServer;
					int reste = nbKV%nbServer;

					ois.close();
					fis.close();
					FileInputStream fis2 = new FileInputStream (localFSSourceFname);
					ObjectInputStream ois2 = new ObjectInputStream (fis2);

					// Envoyer à chaque serveur, un fragment du fichier sous la forme d'une liste de KV
					for (int i=0 ; i<nbServer ; i++) {
						ArrayList<KV> KVlist = new ArrayList<KV>();
						int nbKVSent = quotient;

						if (reste != 0) {
							nbKVSent++;
							reste --;
						}

						for (int j = 0 ; j<nbKVSent ; j++) {
							KV newKV = (KV) ois2.readObject();
							System.out.println(newKV.toString());
							KVlist.add(newKV);

						}
						m.openClient(servers[i]);
						m.send(Commande.CMD_WRITE);
						System.out.println("envoyée au serveur " + i);
						m.send(fichier.getName() + String.valueOf(i));
						m.send(Type.KV);
						m.send(KVlist);
						m.close();
						System.out.println("fragment envoyé au serveur " + i);
					}
					ois2.close();
					fis2.close();
				}
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    public static void HdfsRead(String hdfsFname, String localFSDestFname) {
		System.out.println("Demande de lecture d'un fichier ...");
		Message m = new Message();

		File file = new File(localFSDestFname);
		try {
			

			for (int i = 0; i < servers.length; i++) {
				m.openClient(servers[i]);
				m.send(Commande.CMD_READ);
				System.out.println("envoyée au serveur " + i);
				m.send(hdfsFname + String.valueOf(i));
				Type fmt = (Type) m.receive();
				ArrayList<Object> listReceived = (ArrayList<Object>) m.receive();
				m.close();

				for (Object o : listReceived) {


					switch (fmt) {
						case LINE:
							FileWriter fw = new FileWriter(file,true);
							fw.write(o.toString() + "\n");				
							fw.close();						
							break;
						case KV:
							FileOutputStream fos = new FileOutputStream(file, true);
							ObjectOutputStream oos = new ObjectOutputStream(fos);

							oos.writeObject(o);
							oos.close();
							fos.close();
							break;
						default:
							break;
					}
				}
			}
			System.out.print("Ecriture des données dans un fichier local ...");
			
			System.out.println("données écrites");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}


	public static void main(String[] args) {
        // java HdfsClient <read|write> <line|kv> <file>

        try {
            if (args.length<2) {usage(); return;}

            switch (args[0]) {
              case "read": HdfsRead(args[1],args[2]); break;
              case "delete": HdfsDelete(args[1]); break;
              case "write": 
                Format.Type fmt;
                if (args.length<3) {usage(); return;}
                if (args[1].equals("line")) fmt = Format.Type.LINE;
                else if(args[1].equals("kv")) fmt = Format.Type.KV;
                else {usage(); return;}
                HdfsWrite(fmt,args[2],1);
            }	
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
