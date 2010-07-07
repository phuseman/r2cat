package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;



public class RunBlast {
	File file = null;
	File dir = null;

	public RunBlast(File tempFile, File tempDir) throws IOException {
		file = tempFile;
		dir = tempDir;
		makeBlastDB();
	}

	public void makeBlastDB() throws IOException{
		file.setWritable(true);
		String command = new String("formatdb -i "+file.getName()+" -p F");
		System.out.println(command);
		Process p = Runtime.getRuntime().exec(command,null,dir);
	/*	try {
			p.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//p.wait();
	/*	 Scanner s = new Scanner(p.getErrorStream()).useDelimiter( "\\Z" ); 
		 System.out.println(s.next()); */
		//runBlastCommand();
		//System.out.println(dir.listFiles().length);
	}
	
	public void runBlastCommand() throws IOException{
		String command = new String("blastall -p blastn -i "+file.getName()+" -d "+file.getName()+" -F F -m 8 -e 1e-04 -o blastout.txt");
		Process p = Runtime.getRuntime().exec(command,null,dir);
		 Scanner s = new Scanner(p.getErrorStream()).useDelimiter( "\\Z" ); 
		 System.out.println(s.next());
	}
}
