/* une PROPOSITION, SAUF startJob(), setInputFormat(Format.Type ft) et setInputFname(String fname),  qui sont EXIGÉES.
 * tout le reste peut être complété ou adapté
 */

package ordo;

import map.MapReduce;
import formats.Format;

public interface JobInterface {
    public void setNumberOfReduces(int tasks); // on le prendra égal a 1 dans la V0, il devra s'occuper du résultat de tous les maps
    public void setNumberOfMaps(int tasks); // autant de maps que de chunks du fichier lu (sans compter les doublons)
    public void setInputFormat(Format.Type ft); // on choisit ligne ou kv
    public void setOutputFormat(Format.Type ft); // idem pour la sortie
    public void setInputFname(String fname); // on choisit le nom du fichier d'entrée
    public void setOutputFname(String fname); // on choisit le nom du fichier contenant le résultat
    public void setSortComparator(SortComparator sc); // ON VERRA PLUS TARD
    
    public int getNumberOfReduces();
    public int getNumberOfMaps();
    public Format.Type getInputFormat();
    public Format.Type getOutputFormat();
    public String getInputFname();
    public String getOutputFname();
    public SortComparator getSortComparator();
    
    // La fonction qui fait tout !!!    
    // À SAVOIR :
    // 1) lancer les maps sur tous les chunks du fichier
    // 2) les récupérer quand ils ont finis
    // 3) les concatener dans le fichier résultat avec le reduce qui s'exécutera sur tous les résultats des maps    
    public void startJob (MapReduce mr);
}