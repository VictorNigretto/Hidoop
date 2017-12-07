package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import hdfs.HdfsClient;

public class TerminalHDFS {
	
	public static void main(String[] args) {
	    Scanner sc = new Scanner(System.in);
	    String choixService = null;
	    String actionHdfs = null;
	    String file = null;
	    
	while(true) {

    // Texte d'introduction
	System.out.println("Bienvenue dans le Service Hidoop.");
	System.out.println("Vous pouvez soit utiliser le service HDFS :");
	System.out.println("permettant d'écrire, lire et supprimer des fichiers sur le serveur HDFS.");
	System.out.println("Vous pouvez sinon utiliser le service MapReduce :");
	System.out.println("permettant de lancer vos propres Map/Reduces sur vos propres fichiers.");
	System.out.println("(tous ces services sont pour le moment exclusiement utilisés en local)");
	
	// Choix HDFS / MapReduce
	do {
	    System.out.println("Choisissez votre service à utliser :");
	    System.out.println("(hdfs ou mapreduce)");
	    System.out.println("");
	    choixService = sc.nextLine();
	} while (!choixService.equals("hdfs") && !choixService.equals("mapreduce")) ;
	
	// si HDFS
	if(choixService.equals("hdfs")) {
	    System.out.println("Vous avez choisi Hdfs");
	    System.out.println("");
	
	    // Choix action HDFS
	    do {
	        System.out.println("Voulez-vous écrire, lire ou supprimer un fichier ?");
	        System.out.println("(write, read, delete)");
	        actionHdfs = sc.nextLine();
	    } while (!actionHdfs.equals("write") && !actionHdfs.equals("read") && !actionHdfs.equals("delete")) ;
	
	    if(actionHdfs.equals("write")) {
	        System.out.println("Vous avez choisi d'écrire.");
	        System.out.println("");
	
	        // Choix du fichier
	        System.out.println("Quel fichier voulez-vous écrire ?");
	        file = sc.nextLine();
	
	        // Choix du format
	        String format = null;
	        do {
	            System.out.println("En quel format est écrit ce fichier ?");
	            System.out.println("(line, kv)");
	            format = sc.nextLine();
	        } while (!format.equals("line") && !format.equals("kv")) ;
	
	        // Lancer l'opération
	        System.out.println("Ecriture ...");
	            // Lancer l'opération
	            String[] cmd = {"write", format, file};
	            HdfsClient.main(cmd);
	        System.out.println("Ecriture terminée !");
	
	    } else if (actionHdfs.equals("read")) {
	        System.out.println("Vous avez choisi de lire.");
	        System.out.println("");
	
	        // Choix du fichier
	        System.out.println("Quel fichier voulez-vous lire ?");
	        file = sc.nextLine();
	
	        // Choix de la sortie
	        String fileOutput = null;
	        System.out.println("Quel est le nom du fichier résultat ?");
	        fileOutput = sc.nextLine();
	
	        // Lancer l'opération
	        System.out.println("Lecture ...");
	            // Lancer l'opération
	            String[] cmd = {"read", file, fileOutput};
	            HdfsClient.main(cmd);
	        System.out.println("Lecture terminée !");
	
	    } else {
	        System.out.println("Vous avez choisi de supprimer.");
	        System.out.println("");
	
	        // Choix du fichier
	        System.out.println("Quel fichier voulez-vous supprimer ?");
	        file = sc.nextLine();
	
	        // Lancer l'opération
	        System.out.println("Suppression ...");
	        String[] cmd = {"delete", file};
	        HdfsClient.main(cmd);
	        System.out.println("Suppression terminée !");
	    }
	
	// si Map Reduce
	} else {
	    System.out.println("Vous avez choisi MapReduce");
	    System.out.println("");
	
	    // On récupère le MapReduce
	    System.out.println("Quel fichier contenant le Map/Reduce voulez-vous exécuter ?");
	    System.out.println("(le fichier doit être sans extension)");
	    String fileMr = sc.nextLine();
	
	    // On récupère le fichier sur lequel le lancer
	    System.out.println("Sur quel fichier voulez-vous appliquer MyMapReduce ?");
	    file = sc.nextLine();
	
	    // Lancer l'opération
	    System.out.println("Maping/Reducing ...");
	        
        // Lancer le MapReduce !
        // Pour le moment on va le lancer à la main ! :D
        //String [] cmdMr = {file};
        //MyMapReduce.main(cmdMr);
        //new MyMapReduceRunner(cmdMr).start();
        
        // Lancer un script qui lance le MyMapReduce !!!
        // Recupérer le repertoire courant
        String pwd = System.getProperty("user.dir");
        System.out.println(executeCommand(pwd + "/src/mapreduceLanceur.sh " + fileMr + " " + file));
	    /*    
	    try {
	        Thread.sleep(1000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	    */
	    //System.out.println("Vous pouvez lancer votre map manuellement !");
	    System.out.println("Maping/Reducing terminée !");
		}
	}
}

private static String executeCommand(String command) {
	StringBuffer output = new StringBuffer();
	Process p;

	try {
	    // On lance la commande
		p = Runtime.getRuntime().exec(command);

		// On récupère sa sortie
		p.waitFor();
		BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = "";
		while ((line = reader.readLine())!= null) {
			output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
}









