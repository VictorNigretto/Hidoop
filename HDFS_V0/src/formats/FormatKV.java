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


    private ArrayList<Object> KVstoRead;
    private ArrayList<Object> KVstoWrite;
    private int port;
        // soit Message m mais attention, ou un par type ???

        private long index = 1;
        private String fname;	// nom du fichier

        public FormatKV(String fname) {
            // Mettre le port en  parametre
            this.fname = fname;

        }

        public void open(OpenMode mode) {

            try {
            	Message m = new Message();
            	m.openClient(port);
                // Creation fichier resultat dans Format ou serveur si ouverture a chaque fois, creer linesdans read?
                if (mode == OpenMode.R) {
                    // Récupèrer contenu fichier et le découper en lignes


                    fileRead = new File(fname);

                    fis = new FileInputStream(fileRead);
                    ois = new ObjectInputStream(fis);
                    KVstoRead = (ArrayList<Object>) ois.readObject();
                    Type fmt = (Type) KVstoRead.get(0);


                }
                if (mode == OpenMode.W) {
                    // Créer le fichier résultat dans format ou serveur?

                    fileWrite = new File(fname);
                    fos = new FileOutputStream(fileWrite,true);
                    oos = new ObjectOutputStream(fos);
                    OpenW = true;

                    KVstoWrite = new ArrayList<Object>();
                    KVstoWrite.add(Type.KV);
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
                    oos.writeObject(KVstoWrite);
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
            if (index < KVstoRead.size() ) {
                return (KV) KVstoRead.get((int) index );
            }else {
                return null;
            }
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



