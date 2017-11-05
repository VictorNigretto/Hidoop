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

import util.Message;

import formats.Format;
import formats.Format.Commande;
import formats.Format.Type;
import formats.KV;
import formats.FormatLine;

public class HdfsClient {
	
	private static int servers[] = {6666,5555,4444};

    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    public static void HdfsDelete(String hdfsFname) {
    	Message<Commande> mCMD = new Message<Commande>();
    	Message<String> mString = new Message<String>();
    	int nbServer = servers.length;
    	for (int i=0 ; i<nbServer ; i++) {
    		mCMD.send(Commande.CMD_DELETE, servers[i]);
    		mString.send(hdfsFname + String.valueOf(i), servers[i]);
    	}	
    }
	
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, int repFactor) {
    	try { 
    	Message<String> mString = new Message<String>();
		Message<Commande> mCMD = new Message<Commande>();
		Message<Type> mType = new Message<Type>();
		Message<KV> mKV = new Message<KV>();
		
    	File fichier = new File(localFSSourceFname);
		
		int nbServer = servers.length;
		int quotient;
		int reste;
		
		//faire un switch
		switch (fmt) {
			case LINE:
    			FileReader fr = new FileReader(fichier);
    			BufferedReader br = new BufferedReader(fr);
    			
    			//Lire ligne par ligne et compter
    			int indLine = 1;
    			while (br .readLine() != null) {
    				indLine++;
    			}	
    			int nbLine = indLine - 1;

    			quotient = nbLine/nbServer;
    			reste = nbLine%nbServer;
    			
    			br.close();
    			fr.close();
    			fr = new FileReader(fichier);
    			br = new BufferedReader(fr);
    			
    			// Envoyer à chaque serveur, un fragment du fichier
    			// Atention si fragFile null ????
    			for (int i=0 ; i<nbServer ; i++) {
    				int nbLineSent = quotient;
    				if (reste != 0) {
    					nbLineSent++;
    					reste--;
    				}
    				String fragFile = "";
    				for (int j = 0 ; j<nbLineSent-1 ; j++) {
    					fragFile = fragFile + br.readLine() + "\n";
    				}
    				fragFile = fragFile + br.readLine();
    				
    				mCMD.send(Commande.CMD_WRITE, servers[i]);
    				mString.send(fichier.getName() + String.valueOf(i), servers[i]);
    				
    				mType.send(fmt, servers[i]);
    				
    				// envoyer String ou File ???
    				mString.send(fragFile, servers[i]);
    			}
    			br.close();
    			fr.close();	
    			break;
			case KV:
    			//Lire KV par KV et compter
    			int indKV = 1;
    			
    			FileInputStream fis = new FileInputStream (localFSSourceFname);
    			ObjectInputStream ois = new ObjectInputStream (fis);
    			while (fis.available() != 0){
    				ois.readObject();
	    			indKV++;
    			}	
    			ois.close();
    			fis.close();
    			int nbKV = indKV - 1;
    			
    			quotient = nbKV/nbServer;
    			reste = nbKV%nbServer;
    			
    			fis = new FileInputStream (localFSSourceFname);
    			ois = new ObjectInputStream (fis);
    			
    			// Envoyer à chaque serveur, un fragment du fichier
    			for (int i=0 ; i<nbServer ; i++) {
    				int nbKVSent = quotient;
    				if (reste != 0) {
    					nbKVSent++;
    					reste--;
    				}

    				mCMD.send(Commande.CMD_WRITE, servers[i]);
    				mString.send(fichier.getName() + String.valueOf(i), servers[i]);  				
    				mType.send(fmt, servers[i]);
    				
    				for (int j = 0 ; j<nbKVSent ; j++) {				
    					mKV.send((KV)ois.readObject(),servers[i]);
    				}
    				mKV.send(null,servers[i]);
    			} 			
    			ois.close();
    			fis.close();
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
    	Message<String> mString = new Message<String>();
    	Message<Commande> mCMD = new Message<Commande>();
    	Message<Type> mType = new Message<Type>();
    	Message<KV> mKV = new Message<KV>();
    	File file = new File(hdfsFname + "-res");
    	
    	try {
  	  	
	    	for (int i = 0 ; i < servers.length ; i++) {
	    		
	    		mCMD.send(Commande.CMD_READ, servers[i]);
	    		mString.send(hdfsFname + String.valueOf(i), servers[i]);
	    		Type fmt = mType.reception(servers[i]);
	    		
	    		switch (fmt) {
		    		case LINE:
		    			String content = ""; 
			    		if (i == servers.length-1) {
			    	    	content = content + mString.reception(servers[i]);
			    	    } else {
			    	    	content = content + mString.reception(servers[i]) + "\n";
			    	    }
			    		FileWriter fw = new FileWriter(file,true);
						fw.write(content);
						fw.close();
			    	break;
		    		case KV:
		    			FileOutputStream fos = new FileOutputStream (file,true);
		    			ObjectOutputStream oos = new ObjectOutputStream (fos); 
		    			KV kv = new KV();
			    		while ((kv = (KV) mKV.reception(servers[i])) != null) {
							oos.writeObject(kv);
						}
			    		oos.close();
				    	fos.close();
		    		break;
	    		}
	    	} 	

    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
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
              case "read": HdfsRead(args[1],null); break;
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
