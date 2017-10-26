package formats;

import java.io.Serializable;

// Un format correspond à la façon dont un fichier est écrit
// Un format extends un FormatReader pour pouvoir lire un fichier
// Un format extends un FormatWriter pour pouvoir lire un fichier
public interface Format extends FormatReader, FormatWriter, Serializable {
	// Il peut soit être écrit sous forme de lignes (LINE)
	// Soit sous forme de KV
    public enum Type { LINE, KV };
    // Chaque fichier peut être ouvert soit en mode lecture (R)
    // Soit en mode écriture (W)
    public enum OpenMode { R, W };

	public void open(OpenMode mode);    // Il faut spécifier le mode d'ouverture d'un fichier lorsqu'on l'ouvre
	public void close();	// pour fermer un fichier
	public long getIndex();    // JE NE SAIS PAS À QUOI ÇA SERT !!!
	public String getFname();    // pour récupérer le nom d'un fichier
	public void setFname(String fname);    // pour renommer un fichier
}
