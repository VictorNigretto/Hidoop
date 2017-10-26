package formats;

public interface FormatWriter {
	// La fonction pour pouvoir écrire un KV dans notre fichier
	public void write(KV record);
}
