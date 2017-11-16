package formats;


import util.Message;


import java.io.*;
import java.util.ArrayList;

public class FormatKV implements Format{


// Essayer de factoriser en classe générique classes envoi et reception

    private File fileRead;
    private FileInputStream fis;
    private ObjectInputStream ois;
    private File fileWrite;
    private FileOutputStream fos;
    private ObjectOutputStream oos;

    private boolean OpenR = false;
    private boolean OpenW = false;


    private ArrayList<KV> KVstoRead;
    private ArrayList<KV> KVstoWrite;

        // soit Message m mais attention, ou un par type ???
        private int port;
        private long index = 1;
        private String fname;	// nom du fichier

        public FormatKV(String fname, int port) {
            // Mettre le port en  parametre
            this.fname = fname;
            this.port = port;
        }

        public void open(OpenMode mode) {

            try {
            	Message m = new Message();
            	m.openClient(port);
                // Creation fichier resultat dans Format ou serveur si ouverture a chaque fois, creer linesdans read?
                if (mode == OpenMode.R) {
                    // Récupèrer contenu fichier et le découper en lignes
                    m.send(Commande.CMD_OPEN_R);
                    m.send(fname);		//précisez le fichier dont on veut obtenir le path
                    //  récupérer PATH du fichier dans le server,ou daemon et serveur au meme endroit?
                    String filePath = (String) m.receive();
                    fileRead = new File(filePath);
                    fis = new FileInputStream(fileRead);
                    ois = new ObjectInputStream(fis);
                    Type fmt = (Type) ois.readObject();
                    KVstoRead = (ArrayList<KV>) ois.readObject();


                }
                if (mode == OpenMode.W) {
                    // Créer le fichier résultat dans format ou serveur?
                    m.send(Commande.CMD_OPEN_W);
                    m.send(fname);
                    String filePath = (String) m.receive();
                    fileWrite = new File(filePath);
                    fos = new FileOutputStream(fileWrite,true);
                    oos = new ObjectOutputStream(fos);
                    OpenW = true;

                    KVstoWrite = new ArrayList<KV>();
                    oos.writeObject(Type.KV);
                    OpenW = true;
                }
                m.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    public void close() {
            // fermer sock normalement je pense ou descripteurs
            try {
                if (OpenR) {
                    ois.close();
                    fis.close();
                }
                if (OpenW) {
                    oos.writeObject(KVstoRead);
                    oos.close();
                    fos.close();                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public KV read() {
            // Créer KV index + ligne à index

            index++;
            return KVstoRead.get((int)index-1);
        }

        @Override
        public void write(KV record) {
            KVstoWrite.add(record);
        }

        @Override
        public long getIndex() {
            return this.index;

        }

        @Override
        public String getFname() {
            return this.fname;

        }

        @Override
        public void setFname(String fname) {
            this.fname = fname;
        }

    }



