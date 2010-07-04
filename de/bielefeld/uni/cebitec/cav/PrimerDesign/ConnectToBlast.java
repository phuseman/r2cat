package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;



public class ConnectToBlast {
	File file = null;

	public ConnectToBlast(File tempFile) throws IOException {
		file = tempFile;
		makeBlastDB();
	}

	public void makeBlastDB() throws IOException{
		file.setWritable(true);
		String command = new String("cmd.exe CHDIR C:\\Users\\Mini-Yvi\\Uni "+"&"+" formatdb -i contigs.fas -p F");
		//String command = new String("cmd.exe /c cd C:\\Users\\Yvisunshine "+"&"+" formatdb -i "+file.getName()+" -p F");
		//String[] command = new String[]{"cmd.exe /c cd C:\\Users\\Yvisunshine"," formatdb -i "+file.getName()+" -p F"};
		//ProcessBuilder builder = new ProcessBuilder(command);
		//builder.directory(new File("C:\\Users\\Yvisunshine"));
		//Process p = builder.start();
		//System.out.println(file.getAbsolutePath());
		System.out.println(command);
		Process p = Runtime.getRuntime().exec(command);
		 Scanner s = new Scanner(p.getErrorStream()).useDelimiter( "\\Z" ); 
		 System.out.println(s.next()); 
	}
	
	public void runBlastCommand() throws IOException{
		ProcessBuilder builder = new ProcessBuilder("cmd","/c","blastall -p blastn -i  -d  -F F -m 8 -e 1e-04 >blastout.txt");
		//builder.directory(new File("C:/Users/Yvisunshine/AppData/Local/Temp"));
		Process p = builder.start();
        
	}
}
