/* une PROPOSITION de squelette, incomplète et adaptable... */

package hdfs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import formats.Format;
import formats.KV;
import formats.FormatLine;
//import formats.KVFormat;
//import formats.LineFormat;

public class HdfsClient {
	
	private int servers[] = {2000,3000,4000};

    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    public static void HdfsDelete(String hdfsFname) {}
	
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, int repFactor) {
    	if (fmt == Format.Type.LINE) {	
    		try {  		
    			File fichier = new File(localFSSourceFname);
    			FileReader fr = new FileReader(fichier);
    			BufferedReader br = new BufferedReader(fr);
    			
    			//Lire ligne par ligne
    			/*String line = "";
    			int indLine = 1;
    			LineFormat lf = new LineFormat();
    			while ((line = br .readLine()) != null) {
    				//KV kv = new KV(Integer.toString(indLine),line);
    				//lf.write(kv);
    				System.out.print(Integer.toString(indLine) + " : " + line + "\n");
    				indLine++;
    			}*/
    			
    			// Creer tableau de lignes
    			int taille = (int) fichier.length();
    			char[] buf = new char[taille];
    			fr.read(buf);
    			String text = new String(buf);
    			String lines[] = text.split("\n");
    			for (int i = 0 ; i < lines.length-1 ; i++) {
    				System.out.print((i+1) + " : " + lines[i] + "\n");
    			}
   			
    			br.close();
    			fr.close();
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }

    public static void HdfsRead(String hdfsFname, String localFSDestFname) { }

	
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
