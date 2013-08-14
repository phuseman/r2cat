/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackages.de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model;

import TestConstants.ExportConstantsTest;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model.Cluster;
import de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model.ClusterOrganizer;
import de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model.ExportMainModel;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author Mark Ugarov
 */
public class ModelTest extends TestCase {
    
        /**
     * Test of run method, of class ExportMainModel.
     */
    public void testRun() {
        System.out.println("run");
        //ExportMainModel instance = null;
        //instance.run();
        testConstruct();
    }

    private void testConstruct(){
        ClusterOrganizer expRes = new ClusterOrganizer();
        expRes.add(new Cluster(ExportConstantsTest.M1));
        expRes.add(new Cluster(ExportConstantsTest.M2));
        
        MatchList matches = new MatchList();
        matches.addMatch(ExportConstantsTest.M1);
        matches.addMatch(ExportConstantsTest.M2);
        ExportMainModel instance = new ExportMainModel(matches,60,false, false,0, false, false );
        
        Method construct = this.getMethod("construct");
        this.invokeMethod(instance, construct);
        Field filteredQField = this.getField("filteredQ");
        try {
            ClusterOrganizer filteredQ = (ClusterOrganizer) filteredQField.get(instance);
            assertEquals(expRes, filteredQ);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ModelTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ModelTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    

    /**
     * Test of getOuput method, of class ExportMainModel.
     */
    public void testGetOuput() {
//        System.out.println("getOuput");
//        ExportMainModel instance = null;
//        String expResult = "";
//        String result = instance.getOuput();
//        assertEquals(expResult, result);
        
    }
    
    private Method getMethod(String name){
        Method ret = null;
        try {
            ret = ExportMainModel.class.getDeclaredMethod(name);
            ret.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            fail("NoSuchMethodException by getting method "+name);
        } catch (SecurityException ex) {
            fail("SecurityException by getting method  "+name);    
        }
        return ret;
    }
    
    private void invokeMethod(ExportMainModel instance, Method method){
        try {
            method.invoke(instance, (Object[]) null);
        } catch (IllegalAccessException ex) {
            fail("IllegalAccessException by getting method "+method.getName());
        } catch (IllegalArgumentException ex) {
            fail("IllegalArgumentException by getting method "+method.getName());
        } catch (InvocationTargetException ex) {
            fail("InvocationTargetException by getting method "+method.getName());
        }
    }
    
    private Field getField(String name){
        Field ret = null;
        try {
            ret = ExportMainModel.class.getDeclaredField(name);
            ret.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(ModelTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ModelTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
}

