/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author Mark Ugarov
 */
public class ExportFileChooser extends JFileChooser{
    private File file;
    private FileWriter writer;
    
    public ExportFileChooser(String out){
        int chosenButton = this.showSaveDialog(this);
        if(chosenButton == JFileChooser.APPROVE_OPTION){
            this.file = this.getSelectedFile();
            // TODO : choose the file format
            try {
                this.writer = new FileWriter(file);
                this.writer.write(out);
            } catch (IOException ex) {
                System.err.println("can not write file!");
            }
        }
    }
    
}
