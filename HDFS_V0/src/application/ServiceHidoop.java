package application;
import java.io.IOException;
import java.util.Scanner;

// Une ébauche pour pouvoir lancer les différentes applications
// Mais nous n'avons pas réussi à la faire fonctionner ...
public class ServiceHidoop {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String file = null;
        String mapReduce = null;

        // Récupérer les informations
        System.out.println("Bienvenue dans le Service Hidoop.");
        System.out.println("Vous pourrez ici lancer vos propres Map/Reduces sur vos propres fichiers.");
        System.out.println("Entrez le nom du fichier à traiter (devant être stocké sur HDFS) :");
        file = sc.nextLine();
        System.out.println("Entrez le nom du fichier contenant le Map/Reduce (devant être stocké en local) :");
        System.out.println("(Le fichier doit être compilé, et écrit sans le \".class\")");
        mapReduce = sc.nextLine();

        // Lancer le map/reduce sur le bon fichier
        /*
        System.out.println();
        System.out.println("Compilation ...");
        String[] compiler = new String[2];
        compiler[0] = "javac";
        compiler[1] = mapReduce + ".java";
        try {
            Runtime.getRuntime().exec(compiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        System.out.println("Éxécution ...");
        String[] run = new String[3];
        run[0] = "java";
        run[1] = mapReduce;
        run[2] = file;
        try {
            Runtime.getRuntime().exec(run);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}



















