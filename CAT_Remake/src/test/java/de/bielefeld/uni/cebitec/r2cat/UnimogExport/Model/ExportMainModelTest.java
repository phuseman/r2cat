/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model;

import de.bielefeld.uni.cebitec.qgram.Match;
import de.bielefeld.uni.cebitec.qgram.MatchList;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import junit.framework.TestCase;

/**
 *
 * @author mugarov86
 */
public class ExportMainModelTest extends TestCase {
    
        /**
     * Test of run method, of class ExportMainModel.
     */
    public void testRun() {
        System.out.println("run");
        //ExportMainModel instance = null;
        //instance.run();

    }

    public void testConstruct(){
        String methodName = "construct";
        ArrayList<Cluster> expRes = new ArrayList();
        expRes.add(new Cluster(ModelTestConstants.M1));
        expRes.add(new Cluster(ModelTestConstants.M2));

        MatchList matches = new MatchList();
        matches.addMatch(ModelTestConstants.M2);
        matches.addMatch(ModelTestConstants.M1);
        //the distance "60" is irrelevant, we dont run mergeNeighbors() here
        ExportMainModel instance = new ExportMainModel(matches,60,false, false,0, false, false );
        
        Method construct = this.getMethod(methodName);
        this.invokeMethod(instance, construct);
        Field filteredQField = this.getField("filteredQ");
        try {
            ClusterOrganizer filteredQ = (ClusterOrganizer) filteredQField.get(instance);
            for(int i =0; i<filteredQ.size(); i++){
                this.testClusterIndexes(expRes.get(i), filteredQ.get(i), methodName);
            }
        } catch (IllegalArgumentException ex) {
            fail("IllegalArgumentException by testing construct()");
        } catch (IllegalAccessException ex) {
            fail("IllegalAccessException by testing construct()");
        }
    }
    
    public void testMergeNeighours(){
        String methodName = "mergeNeighbors";
        System.out.println(methodName);
        Match m1 = ModelTestConstants.M1;
        Match m2 = ModelTestConstants.M2;
        Match m3 = ModelTestConstants.M3;
        ArrayList<Cluster> expRes = new ArrayList();
        Cluster c1 = new Cluster(m1.getQueryStart(), m2.getQueryEnd(), m1.getTargetStart(), m2.getTargetEnd(), true);
        Cluster c3 = new Cluster(m3);
        expRes.add(c1);
        expRes.add(c3);
        
        MatchList matches = new MatchList();
        matches.addMatch(m1);
        matches.addMatch(m2);
        matches.addMatch(m3);
        ExportMainModel instance = new ExportMainModel(matches, 60, false, false, 0, false, false);
        
        Method construct = this.getMethod("construct");
        Method merge = this.getMethod(methodName);
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, merge);
        Field filteredQField = this.getField("filteredQ");
        try {
            ClusterOrganizer filteredQ = (ClusterOrganizer) filteredQField.get(instance);
            for(int i =0; i<filteredQ.size(); i++){
                this.testClusterIndexes(expRes.get(i), filteredQ.get(i), methodName);
            }
        } catch (IllegalArgumentException ex) {
            fail("IllegalArgumentException by testing construct()");
        } catch (IllegalAccessException ex) {
            fail("IllegalAccessException by testing construct()");
        }  
    }
    
    public void testRejection(){
        String methodName = "rejectShortClusters";
        System.out.println(methodName);
        Match m1 = ModelTestConstants.M1;
        Match m2 = ModelTestConstants.M2;
        Match m3 = ModelTestConstants.M3;
        Match m4 = ModelTestConstants.M4;
        
        ArrayList<Cluster> expRes = new ArrayList();
        expRes.add(new Cluster(m1));
        expRes.add(new Cluster(m2));
        expRes.add(new Cluster(m3));
        
        MatchList matches = new MatchList();
        matches.addMatch(m1);
        matches.addMatch(m2);
        matches.addMatch(m3);
        matches.addMatch(m4);
        ExportMainModel instance = new ExportMainModel(matches, 0, false, false, 80, false, false);
        
        Method construct = this.getMethod("construct");
        Method reject = this.getMethod(methodName);
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, reject);
        Field filteredQField = this.getField("filteredQ");
         try {
            ClusterOrganizer filteredQ = (ClusterOrganizer) filteredQField.get(instance);
            for(int i =0; i<filteredQ.size(); i++){
                // 80*80 = 6400
                if(filteredQ.get(i).getSquareSize()<6400){
                    fail("Cluster "+ i +" was not rejected but longer than 80.");
                }
                this.testClusterIndexes(expRes.get(i), filteredQ.get(i), methodName);
            }
        } catch (IllegalArgumentException ex) {
            fail("IllegalArgumentException by testing construct()");
        } catch (IllegalAccessException ex) {
            fail("IllegalAccessException by testing construct()");
        }
         
        // comparison / second test without minimum 
        expRes.add(new Cluster(m4));
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, reject);
        try {
            ClusterOrganizer filteredQ = (ClusterOrganizer) filteredQField.get(instance);
            for(int i =0; i<filteredQ.size(); i++){
                this.testClusterIndexes(expRes.get(i), filteredQ.get(i), methodName);
            }
        } catch (IllegalArgumentException ex) {
            fail("IllegalArgumentException by testing construct()");
        } catch (IllegalAccessException ex) {
            fail("IllegalAccessException by testing construct()");
        }
    }
    
    private void testClusterIndexes(Cluster expC, Cluster filtC, String methodName){
            if(expC.getQueryStart() != filtC.getQueryStart()){
                fail("Startindex in query was expected "+expC.getQueryStart()+" but was "+filtC.getQueryStart()+
                        " while testing "+ methodName);
            }
            if(expC.getQueryEnd() != filtC.getQueryEnd()){
                fail("Endindex in query was expected "+expC.getQueryEnd()+" but was "+filtC.getQueryEnd()+
                        " while testing "+ methodName);
            }
            if(expC.getTargetStart() != filtC.getTargetStart()){
                fail("Startindex in target was expected "+expC.getTargetStart()+" but was "+filtC.getTargetStart()+
                        " while testing "+ methodName);
            }
            if(expC.getTargetEnd() != filtC.getTargetEnd()){
                fail("Endindex in target was expected "+expC.getTargetEnd()+" but was "+filtC.getTargetEnd()+
                        " while testing "+ methodName);
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
            Logger.getLogger(ExportMainModelTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ExportMainModelTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
}
