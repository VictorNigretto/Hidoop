package formats;


import util.Message;
import formats.Format.OpenMode;

public class FormatLine implements Format {
	
// Essayer de factoriser en classe générique classes envoi et reception

	
	
	// soit Message m mais attention, ou un par type ???
	private Message<Commande> mCMD;
	private Message<String> mString;
	private Message<Type> mType;
	private Message<KV> mKV;

	private int port = 6666;
	private long index = 1;
	private String fname;	// nom du fichier
	private Type type;		// type du format
	
	public FormatLine(String fname, Type type) {
		this.fname = fname;
		this.type = type;
		this.mCMD = new Message<Commande>();
		this.mString = new Message<String>();
		this.mType = new Message<Type>();
		this.mKV = new Message<KV>();
	}
	
	public void open(OpenMode mode) {
		if (mode == OpenMode.R) {
			mCMD.send(Commande.CMD_OPEN_R,port);
			mString.send(fname,port);
			}
		if (mode == OpenMode.W) {
			mCMD.send(Commande.CMD_OPEN_W,port);
			mString.send(fname,port);
		}	
	}
	
	public void close() {
		mCMD.send(Commande.CMD_CLOSE,port);	
	}

	@Override
	public KV read() {
		mCMD.send(Commande.CMD_READ,port);
		mType.send(type,port);
		index++;
		return (KV) mKV.reception(port);
	}

	@Override
	public void write(KV record) {
		mCMD.send(Commande.CMD_WRITE,port);	
		mType.send(type,port);	
		mKV.send(record,port);
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
