package formats;

import util.Message;

import java.io.*;

public class FormatKV implements Format{


// Essayer de factoriser en classe générique classes envoi et reception

        private File fileRead;
        private FileInputStream fr;
        private File fileWrite;
        private FileOutputStream fw;

        private boolean OpenR = false;
        private boolean OpenW = false;

        private String filePath;

        private KV kvs[];

        // soit Message m mais attention, ou un par type ???
        private Message<Commande> mCMD;
        private Message<String> mString;
        private Message<Type> mType;
        private Message<KV> mKV;

        private int port = 6666;
        private long index = 1;
        private String fname;	// nom du fichier
        private Type type;		// type du format

        public FormatKV(String fname, Type type) {
            // Mettre le port en  parametre
            this.fname = fname;
            this.type = type;
            this.mCMD = new Message<Commande>();
            this.mString = new Message<String>();
            this.mType = new Message<Type>();
            this.mKV = new Message<KV>();
        }

        public void open(OpenMode mode) {

            try {
                //pas d 'ouverture de descripteur en lecture, envoyer copie fichier ?
                // Ouvrir pour chaques ecriture/lecture, ou une seule fois?
                // Creation fichier resultat dans Format ou serveur si ouverture a chaque fois, creer linesdans read?
                if (mode == OpenMode.R) {
                    // Récupèrer contenu fichier et le découper en lignes
                    mCMD.send(Commande.CMD_OPEN_R, port);
                    //mString.send(fname,port);		précisez le fichier dont on veut obtenir le path
                    //  récupérer PATH du fichier dans le server,ou daemon et serveur au meme endroit?
                    filePath = mString.reception(port);
                    fileRead = new File(filePath);
                    fr = new FileInputStream(fileRead);
                    OpenR = true;
                    ObjectInputStream ois = new ObjectInputStream(fr);
                    int i = fr.available();
                    while (fr.available() > 0) {
                        kvs[i - 1] = (KV) ois.readObject();
                        i--;
                    }
                    ois.close();


                }
                if (mode == OpenMode.W) {
                    // Créer le fichier résultat dans format ou serveur?
                    mCMD.send(Commande.CMD_OPEN_W, port);
                    mString.send(fname, port);
                    filePath = mString.reception(port);
                    fileWrite = new File(filePath);
                    fw = new FileOutputStream(fileWrite, true);
                    OpenW = true;
                }
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
            //mCMD.send(Commande.CMD_CLOSE,port);
            // fermer sock normalement je pense ou descripteurs
            try {
                if (OpenR) {
                    fr.close();
                }
                if (OpenW) {
                    fw.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public KV read() {
            // Créer KV index + ligne à index

            index++;
            return kvs[(int)index];
        }

        @Override
        public void write(KV record) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(fw);
                oos.writeObject(record);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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


