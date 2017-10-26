package formats;

// On utilisera surtout les KV au début pour ne pas s'embrouiller avec les lignes
// Un KV est un ensemble clé/valeur. Une clé c'est genre un entier. Une valeur c'est genre une ligne d'un fichier.
public class KV {

	// On va vouloir stocker des KV dans des fichiers
	// Sur chaque ligne on va vouloir écrire un KV
	// On va écrire la valeur ,puis le symbole SEPARATOR, et enfin la clé !
	public static final String SEPARATOR = "<->";
	
	public String k; // la clé
	public String v; // la valeur
	
	public KV() {} // un constructeur vide
	
	public KV(String k, String v) { // un plein !
		super();
		this.k = k;
		this.v = v;
	}

	// Bon on redéfinie la fonction d'affichage d'un KV pour que ce soit plus lisible
	public String toString() {
		return "KV [k=" + k + ", v=" + v + "]";
	}
	
}
