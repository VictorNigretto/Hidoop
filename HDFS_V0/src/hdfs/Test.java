package hdfs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import formats.Format;
import formats.Format.Type;
import formats.FormatKV;
import formats.KV;
import formats.FormatLine;
import formats.Format.Commande;

public class Test {
	

	public static void main(String[] args) {
		

		FormatLine lf = new FormatLine("test.txt");

		lf.open(Format.OpenMode.R);
		KV kv = lf.read();
		System.out.println(kv);
		
		kv = lf.read();
		System.out.println(kv);
		
		kv = lf.read();
		System.out.println(kv);
		
		lf.close();
		lf.setFname("testWrite.txt");
	
		
		kv = new KV("Cours","Math");
		lf.open(Format.OpenMode.W);
		lf.write(kv);
		
		kv = new KV("Prof","Ellips");
		lf.write(kv);

		lf.close();

		
		
		FormatKV lk = new FormatKV("testKV.txt");

		lk.open(Format.OpenMode.R);
		kv = lk.read();
		System.out.println(kv);
		
		kv = lk.read();
		System.out.println(kv);
		
		kv = lk.read();
		System.out.println(kv);
		
		lf.close();
		lk.setFname("testKVWrite.txt");
	
		
		kv = new KV("Cours","Math");
		lk.open(Format.OpenMode.W);
		lk.write(kv);
		
		kv = new KV("Prof","Ellips");
		lk.write(kv);

		lk.close();

	}
}
