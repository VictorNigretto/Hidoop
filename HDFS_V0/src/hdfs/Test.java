package hdfs;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import formats.Format;
import formats.Format.Type;
import formats.KV;
import formats.FormatLine;
import formats.Format.Commande;

public class Test {
	

	public static void main(String[] args) {
		

		FormatLine lf = new FormatLine("test.txt",Type.LINE);

		lf.open(Format.OpenMode.R);
		KV kv = lf.read();
		System.out.println(kv.v);
		System.out.println(kv.k);
		
		//lf.open(Format.OpenMode.R);
		kv = lf.read();
		System.out.println(kv.v);
		System.out.println(kv.k);
	
		//lf.open(Format.OpenMode.R);
		kv = lf.read();
		System.out.println(kv.v);
		System.out.println(kv.k);
		
		kv = new KV("Cours","Math");
		lf.open(Format.OpenMode.W);
		lf.write(kv);
		
		kv = new KV("Prof","Ellips");
		//lf.open(Format.OpenMode.W);
		lf.write(kv);

	}
}
