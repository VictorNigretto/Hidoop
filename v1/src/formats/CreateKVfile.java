package formats;

import formats.KV;

import java.io.*;
import java.rmi.RemoteException;


/* Fichier obsolète, permettant de créer un fichier de KV
 * Cet ancien format était un fichier contenant des objets javas
 */
public class CreateKVfile {

    public static void main (String[] args){
        File f = new File("test.kvread");
        KV kv1 = new KV("premier","1");
        KV kv2 = new KV("deuxieme","2");
        try {
            /*FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);


            oos.writeObject(kv1);
            oos.writeObject(kv2);

            oos.close();
            fos.close();*/

            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                KV newkv1 = (KV) ois.readObject();
                System.out.println(newkv1.toString());

                KV newkv2 = (KV) ois.readObject();
                System.out.println(newkv2.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }catch(RemoteException e){
            e.printStackTrace();

        }catch(FileNotFoundException e){
            e.printStackTrace();

        }catch(IOException e) {
            e.printStackTrace();

        }

    }
}
