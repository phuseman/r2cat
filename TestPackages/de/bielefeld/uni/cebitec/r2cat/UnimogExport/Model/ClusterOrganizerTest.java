/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackages.de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model;

import de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model.Cluster;
import de.bielefeld.uni.cebitec.r2cat.UnimogExport.Model.ClusterOrganizer;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 *
 * @author Mark Ugarov
 */
public class ClusterOrganizerTest extends TestCase {
    
    public ClusterOrganizerTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of add method, of class ClusterOrganizer.
     */
    public void testAdd_Cluster() {
        System.out.println("add");
        Cluster c = null;
        ClusterOrganizer instance = new ClusterOrganizer();
        boolean expResult = false;
        boolean result = instance.add(c);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class ClusterOrganizer.
     */
    public void testAdd_int_Cluster() {
        System.out.println("add");
        int index = 0;
        Cluster element = null;
        ClusterOrganizer instance = new ClusterOrganizer();
        instance.add(index, element);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findNextMatchInQuery method, of class ClusterOrganizer.
     */
    public void testFindNextMatchInQuery() {
        System.out.println("findNextMatchInQuery");
        int index = 0;
        ClusterOrganizer instance = new ClusterOrganizer();
        int expResult = 0;
        int result = instance.findNextMatchInQuery(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of join method, of class ClusterOrganizer.
     */
    public void testJoin() {
        System.out.println("join");
        int index1 = 0;
        int index2 = 0;
        ClusterOrganizer instance = new ClusterOrganizer();
        instance.join(index1, index2);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createSortedTargetStartList method, of class ClusterOrganizer.
     */
    public void testCreateSortedTargetStartList() {
        System.out.println("createSortedTargetStartList");
        ClusterOrganizer instance = new ClusterOrganizer();
        instance.createSortedTargetStartList();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resortTargets method, of class ClusterOrganizer.
     */
    public void testResortTargets() {
        System.out.println("resortTargets");
        int index = 0;
        ClusterOrganizer instance = new ClusterOrganizer();
        instance.resortTargets(index);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTargetOrder method, of class ClusterOrganizer.
     */
    public void testGetTargetOrder() {
        System.out.println("getTargetOrder");
        ClusterOrganizer instance = new ClusterOrganizer();
        ArrayList expResult = null;
        ArrayList result = instance.getTargetOrder();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSquareDistance method, of class ClusterOrganizer.
     */
    public void testGetSquareDistance_int_int() {
        System.out.println("getSquareDistance");
        int i1 = 0;
        int i2 = 0;
        ClusterOrganizer instance = new ClusterOrganizer();
        double expResult = 0.0;
        double result = instance.getSquareDistance(i1, i2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSquareDistance method, of class ClusterOrganizer.
     */
    public void testGetSquareDistance_Cluster_Cluster() {
        System.out.println("getSquareDistance");
        Cluster ce1 = null;
        Cluster ce2 = null;
        ClusterOrganizer instance = new ClusterOrganizer();
        double expResult = 0.0;
        double result = instance.getSquareDistance(ce1, ce2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRepeatlessScore method, of class ClusterOrganizer.
     */
    public void testGetRepeatlessScore() {
        System.out.println("getRepeatlessScore");
        Cluster c1 = null;
        Cluster c2 = null;
        long qSize = 0L;
        long tSize = 0L;
        boolean qCirc = false;
        boolean tCirc = false;
        ClusterOrganizer instance = new ClusterOrganizer();
        double expResult = 0.0;
        double result = instance.getRepeatlessScore(c1, c2, qSize, tSize, qCirc, tCirc);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaximalOverlap method, of class ClusterOrganizer.
     */
    public void testGetMaximalOverlap() {
        System.out.println("getMaximalOverlap");
        Cluster c1 = null;
        Cluster c2 = null;
        ClusterOrganizer instance = new ClusterOrganizer();
        long expResult = 0L;
        long result = instance.getMaximalOverlap(c1, c2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMinimalOverlap method, of class ClusterOrganizer.
     */
    public void testGetMinimalOverlap() {
        System.out.println("getMinimalOverlap");
        Cluster c1 = null;
        Cluster c2 = null;
        ClusterOrganizer instance = new ClusterOrganizer();
        long expResult = 0L;
        long result = instance.getMinimalOverlap(c1, c2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of follows method, of class ClusterOrganizer.
     */
    public void testFollows() {
        System.out.println("follows");
        Cluster c1 = null;
        long cut = 0L;
        Cluster c2 = null;
        ClusterOrganizer instance = new ClusterOrganizer();
        boolean expResult = false;
        boolean result = instance.follows(c1, cut, c2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setClusters method, of class ClusterOrganizer.
     */
    public void testSetClusters() {
        System.out.println("setClusters");
        ArrayList<Cluster> clusters = null;
        ClusterOrganizer instance = new ClusterOrganizer();
        instance.setClusters(clusters);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
