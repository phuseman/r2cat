package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;



public class RunBlast {
	File contigToBlast = null;
	File directoryForTempFiles = null;
	File blastOutput = null;

	public RunBlast(File tempFile, File tempDir) throws IOException, InterruptedException {
		contigToBlast = tempFile;
		directoryForTempFiles = tempDir;
		makeBlastDB();
		runBlastCommand();
	}

	public void makeBlastDB() throws IOException, InterruptedException{
		contigToBlast.setWritable(true);
		String command = new String("formatdb -i "+contigToBlast.getName()+" -p F");
		Process p = Runtime.getRuntime().exec(command,null,directoryForTempFiles);
		p.waitFor();
		Scanner s = new Scanner(p.getErrorStream()).useDelimiter( "\\Z" ); 
		 if(s.hasNext()){
			 System.out.println(s.next());
		 }
	}
	
	public void runBlastCommand() throws IOException, InterruptedException{
		blastOutput = File.createTempFile("blastout",".txt",directoryForTempFiles);
		String command = new String("blastall -p blastn -i "+contigToBlast.getName()+" -d "+contigToBlast.getName()+" -F F -m 8 -e 1e-04 -o " +blastOutput.getName());
		Process p = Runtime.getRuntime().exec(command,null,directoryForTempFiles);
		p.waitFor();
		Scanner s = new Scanner(p.getErrorStream()).useDelimiter( "\\Z" ); 
		 if(s.hasNext()){
			 System.out.println(s.next());
		 }
	}

	public File getBlastOutput() {
		return blastOutput;
	}

	public void setBlastOutput(File blastOutput) {
		this.blastOutput = blastOutput;
	}
}
