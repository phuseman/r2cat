/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import static junit.framework.Assert.fail;

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
    

    public void testConstruct(){
        System.out.println("construct");
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
        Field filteredQField = this.getField("sortedByQuery");
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
        Field filteredQField = this.getField("sortedByQuery");
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
        Field filteredQField = this.getField("sortedByQuery");
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
    
    public void testDetecting(){
        String methodName = "detectPath";
        System.out.println(methodName);
        ExportMainModel instance;
        
        Match m1 = ModelTestConstants.M1;
        Match m2 = ModelTestConstants.M2;
        Match m3 = ModelTestConstants.M3;
        Match m4 = ModelTestConstants.M4;
        Match r1 = ModelTestConstants.E1;
        
        ArrayList<Cluster> expRes = new ArrayList();
        expRes.add(new Cluster(m1));
//        expRes.add(new Cluster(r1));
        expRes.add(new Cluster(m2));
        expRes.add(new Cluster(m3));
        expRes.add(new Cluster(m4));
        
        MatchList matches = new MatchList();
        matches.addMatch(m1);
        matches.addMatch(r1);
        matches.addMatch(m2);
        matches.addMatch(m3);
        matches.addMatch(m4);
        
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        Method construct = this.getMethod("construct");
        Method detect = this.getMethod(methodName);
        this.invokeMethod(instance, construct);

        try {
            ClusterOrganizer filteredQ = new ClusterOrganizer();
            try {
                filteredQ = (ClusterOrganizer) detect.invoke(instance, (Object[]) null);

            } catch (InvocationTargetException ex) {
                Logger.getLogger(ExportMainModelTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            assertEquals(expRes.size(), filteredQ.size());
            for(int i =0; i<filteredQ.size(); i++){
                this.testClusterIndexes(expRes.get(i), filteredQ.get(i), methodName);
//                if(filteredQ.get(i).getBestPredecessor() != null){
//                    System.out.println(filteredQ.get(i).getQueryName()+" on index " +i+": score "+ filteredQ.get(i).getBestScore() +" by best pred "+filteredQ.get(i).getBestPredecessor().getQueryName());
//                    System.out.println("Score addition: "+(filteredQ.get(i).getBestScore()-filteredQ.get(i).getBestPredecessor().getBestScore()));
//                }
//                else{
//                     System.out.println(filteredQ.get(i).getQueryName()+" on index " +i+" without best pred");
//                }
                
            }
        } catch (IllegalArgumentException ex) {
            fail("IllegalArgumentException by testing construct()");
        } catch (IllegalAccessException ex) {
            fail("IllegalAccessException by testing construct()");
        }
    }
    
    public void testRepeats(){
        String methodName = "searchRepeats";
        System.out.println(methodName);
        ExportMainModel instance;
        
        Match r1 = ModelTestConstants.R1;
        Match r2 = ModelTestConstants.R2;
        Match r3 = ModelTestConstants.R3;
        Match r4 = ModelTestConstants.R4;
        Match r5 = ModelTestConstants.R5;
        
        Method construct = this.getMethod("construct");
        Method searchRepeats = this.getMethod(methodName);
        MatchList matches;
        String queryExpRes;
        String targetExpRes;
        
        //scenario forward1
        System.out.println("Scenario forward1");
        matches= new MatchList();
        matches.addMatch(r1);
        matches.addMatch(r2);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "50matches50 repeat0.0 repeat0.0 100matches150 ";;
        targetExpRes = "50matches50 repeat0.0 repeat0.0 repeat0.0 100matches150 ";  
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario forward2
        System.out.println("Scenario forward2");
        matches= new MatchList();
        matches.addMatch(r2);
        matches.addMatch(r3);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "100matches150 repeat0.0 repeat0.5 repeat0.0 repeat0.5 repeat0.0 250matches250 ";;
        targetExpRes = "100matches150 repeat0.0 repeat0.5 repeat0.0 250matches250 ";  
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario forward3
        System.out.println("Scenario forward3");
        matches = new MatchList();
        matches.addMatch(r3);
        matches.addMatch(r5);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "250matches250 repeat0.0 400matches450 ";
        targetExpRes = "250matches250 repeat0.0 repeat0.0 400matches450 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario forward4
        System.out.println("Scenario forward4");
        matches = new MatchList();
        matches.addMatch(r1);
        matches.addMatch(r4);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "50matches50 repeat0.0 repeat0.0 300matches150 ";
        targetExpRes = "50matches50 repeat0.0 300matches150 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        Match r6 = ModelTestConstants.R6;
        Match r7 = ModelTestConstants.R7;
        Match r8 = ModelTestConstants.R8;
        Match r9 = ModelTestConstants.R9;
        Match r10 = ModelTestConstants.R10;
        
        //scenario backward1
        System.out.println("Scenario backward1");
        matches = new MatchList();
        matches.addMatch(r7);
        matches.addMatch(r9);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "300matches700 repeat0.0 repeat0.0 repeat0.0 400matches600 ";
        targetExpRes = "-400matches600 -repeat0.0 -repeat0.0 -300matches700 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario backward2
        System.out.println("Scenario backward2");
        matches = new MatchList();
        matches.addMatch(r7);
        matches.addMatch(r8);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "300matches700 repeat0.0 repeat0.5 repeat0.0 350matches550 ";
        targetExpRes = "-350matches550 -repeat0.0 -repeat0.5 -repeat0.0 -repeat0.5 -repeat0.0 -300matches700 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario backward3
        System.out.println("Scenario backward3");
        matches = new MatchList();
        matches.addMatch(r6);
        matches.addMatch(r9);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "200matches700 repeat0.0 repeat0.0 400matches600 ";
        targetExpRes = "-400matches600 -repeat0.0 -200matches700 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario backward4
        System.out.println("Scenario backward4");
        matches = new MatchList();
        matches.addMatch(r9);
        matches.addMatch(r10);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "400matches600 repeat0.0 500matches400 ";
        targetExpRes = "-500matches400 -repeat0.0 -repeat0.0 -400matches600 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        Match r11 = ModelTestConstants.R11;
        Match r12 = ModelTestConstants.R12;
        Match r13 = ModelTestConstants.R13;
        Match r14 = ModelTestConstants.R14;
        Match r15 = ModelTestConstants.R15;
        
        //scenario forwardbackward1
        System.out.println("Scenario forwardbackward1");
        matches = new MatchList();
        matches.addMatch(r11);
        matches.addMatch(r13);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "300matches300 repeat0.5 500matches800 repeat0.5 ";
        targetExpRes = "300matches300 repeat0.5 -500matches800 -repeat0.5 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario forwardbackward2
        System.out.println("Scenario forwardbackward2");
        matches = new MatchList();
        matches.addMatch(r11);
        matches.addMatch(r12);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "300matches300 repeat0.0 400matches1000 ";
        targetExpRes = "300matches300 repeat0.0 -400matches1000 -repeat0.0 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario forwardbackward3
        System.out.println("Scenario forwardbackward3");
        matches = new MatchList();
        matches.addMatch(r11);
        matches.addMatch(r15);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "300matches300 repeat0.0 700matches800 repeat0.0 ";
        targetExpRes = "300matches300 repeat0.0 -700matches800 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario forwardbackward4
        System.out.println("Scenario forwardbackward4");
        matches = new MatchList();
        matches.addMatch(r11);
        matches.addMatch(r14);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "repeat0.0 300matches300 repeat0.0 700matches400 ";
        targetExpRes = "-700matches400 repeat0.0 300matches300 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        
        Match r16 = ModelTestConstants.R16;
        Match r17 = ModelTestConstants.R17;
        Match r18 = ModelTestConstants.R18;
        Match r19 = ModelTestConstants.R19;
        Match r20 = ModelTestConstants.R20;
        
        //scenario backwardforward1
        System.out.println("Scenario backwardforward1");
        matches = new MatchList();
        matches.addMatch(r16);
        matches.addMatch(r17);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "400matches600 repeat0.0 repeat0.5 500matches200 repeat0.5 ";
        targetExpRes = "repeat0.0 repeat0.5 500matches200 -repeat0.5 -repeat0.0 -400matches600 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario backwardforward2
        System.out.println("Scenario backwardforward2");
        matches = new MatchList();
        matches.addMatch(r16);
        matches.addMatch(r18);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "400matches600 repeat0.0 550matches100 ";
        targetExpRes = "repeat0.0 550matches100 -repeat0.0 -400matches600 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario backwardforward3
        System.out.println("Scenario backwardforward3");
        matches = new MatchList();
        matches.addMatch(r16);
        matches.addMatch(r20);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "400matches600 repeat0.0 700matches300 repeat0.0 ";
        targetExpRes = "700matches300 -repeat0.0 -400matches600 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
        
        //scenario backwardforward4
        System.out.println("Scenario backwardforward4");
        matches = new MatchList();
        matches.addMatch(r16);
        matches.addMatch(r19);
        instance = new ExportMainModel(matches, 0, false, false, 0, false, false);
        queryExpRes = "repeat0.0 400matches600 repeat0.0 600matches500 ";
        targetExpRes = "-400matches600 -repeat0.0 600matches500 ";
        this.invokeMethod(instance, construct);
        this.invokeMethod(instance, searchRepeats);
        this.testNames(instance, methodName, queryExpRes, targetExpRes);
    }
    
    private void testNames(ExportMainModel instance, String methodName, String queryExpRes, String targetExpRes){
        Field filteredQField = this.getField("sortedByQuery");
        StringBuilder insQueryBuild = new StringBuilder();
        StringBuilder insTargetBuild = new StringBuilder();
       
        try {
            ClusterOrganizer filteredQ = (ClusterOrganizer) filteredQField.get(instance);
            
        for(Cluster c: filteredQ){
            insQueryBuild.append(c.getQueryName()+" ");
        }
            filteredQ.createSortedTargetStartList();
        for(int i: filteredQ.getTargetOrder()){
            insTargetBuild.append(filteredQ.get(i).getTargetName()+" ");
        }
        } catch (IllegalArgumentException ex) {
            System.out.println("IllegalArgumentException while trying to get sortedByQuery in Method "+methodName);
        } catch (IllegalAccessException ex) {
             System.out.println("IllegalAccessException while trying to get sortedByQuery in Method "+methodName);
        }
        
        System.out.println(insQueryBuild.toString());
        assertEquals(queryExpRes, insQueryBuild.toString());
        System.out.println(insTargetBuild.toString());
        assertEquals(targetExpRes, insTargetBuild.toString());
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
            fail("InvocationTargetException by getting method "+method.getName()+" "+ex.getTargetException());
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
