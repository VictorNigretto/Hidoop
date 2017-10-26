/* une PROPOSITION de squelette, incomplète et adaptable... */

package hdfs;
import formats.Format;
import formats.KV;
import formats.KVFormat; // TODO ==> groupe Yanis/Marine
import formats.LineFormat; // TODO ==> groupe Yanis/Marine

// Nous on s'occupe pas trop du client en fait !!! =)
public class HdfsClient {

	// La fonction pour engueuler le client quand il fait de la merde
    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    // TODO ==> groupe Yanis/Marine
    public static void HdfsDelete(String hdfsFname) {}
	
    // TODO ==> groupe Yanis/Marine
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, 
     int repFactor) { }

    // TODO ==> groupe Yanis/Marine
    public static void HdfsRead(String hdfsFname, String localFSDestFname) { }

	
    // L'application que lance le client depuis sa machine !
    // Il lance HdfsClient avec java
    // Il doit dire s'il veut lire ou écrire ou détruire un fichier
    // Puis si il utilise le format KV ou LINE
    // Et enfin spécifier le nom du fichier (String ?)
    public static void main(String[] args) {
        // java HdfsClient <read|write|delete> <line|kv> <file>

        try {
            if (args.length<2) {usage(); return;}

            switch (args[0]) {
              case "read": HdfsRead(args[1],null); break; // pour lire un fichier
              case "delete": HdfsDelete(args[1]); break; // pour le supprimer
              case "write": // pour l'écire
            	// on récupère le type de Format
                Format.Type fmt;
                if (args.length<3) {usage(); return;}
                if (args[1].equals("line")) fmt = Format.Type.LINE;
                else if(args[1].equals("kv")) fmt = Format.Type.KV;
                else {usage(); return;}
                // puis on écrit
                HdfsWrite(fmt,args[2],1);
            }	
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
