package formats;

import java.io.Serializable;

public interface Format extends FormatReader, FormatWriter, Serializable {
    public enum Type { LINE, KV };
    public enum OpenMode { R, W };
    public enum Commande {CMD_OPEN_R, CMD_OPEN_W, CMD_CLOSE, CMD_READ, CMD_WRITE, CMD_DELETE};

	public void open(OpenMode mode);
	public void close();
	public long getIndex();
	public String getFname();
	public void setFname(String fname);

}
