package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.File;
import java.io.IOException;


public class ConnectToBlast {
	File file = null;

	public ConnectToBlast(File tempFile) throws IOException {
		file = tempFile;
		makeBlastDB();
	}

	public void makeBlastDB() throws IOException{
		//System.out.println("test");
		//Process t = Runtime.getRuntime().exec("cmd /c formatdb -i "+file.getAbsolutePath()+" -p F");
		//System.out.println(file.toString());
		//t.destroy();
		//System.out.println(t.exitValue());
		
	}
	public void runBlastCommand() throws IOException{
		//Runtime.getRuntime().exec("cmd /c notepad.exe c:\\autoexec.bat");
        
	}
}
