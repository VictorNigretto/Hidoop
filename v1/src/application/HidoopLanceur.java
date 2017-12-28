package application;

public class HidoopLanceur {
    /**
     * Cette classe permet de lancer Hidoop
     */

    public static void main (String[] args) {
        /* On lance hdfs ( ce qui lance aussi les daemons*/
        LanceurHDFS.main(args);

        /* On demande à l'utilisateur s'il veut utiliser hdfs ou hidoop */

    }



}
