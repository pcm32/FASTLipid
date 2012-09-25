/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.generator;

import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author pmoreno
 */
public class IntegerListIteratorTest extends TestCase {
    
    public IntegerListIteratorTest(String testName) {
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
     * Test of hasNext method, of class IntegerListIterator.
     */
    public void testNextPerformanceComp() {
        System.out.println("Next performance");
        Integer carbons = 20;
        Integer slots = 4;
        Integer stepSize = 2;
        Integer minPerSlot = 2;
        IntegerListIterator multiSetB = new MultiSetBasedIntegerListIterator(slots, stepSize);
        multiSetB.initialize(carbons, minPerSlot);
        IntegerListIterator listB = new ListBasedIntegerListIterator(slots, stepSize);
        listB.initialize(carbons, minPerSlot);
        
        Long startMS = System.currentTimeMillis();
        Integer counts = 0;
        while(multiSetB.hasNext()) {
            counts++;
            List<Integer> res = multiSetB.next();
            System.out.println(res);
        }
        Long time = System.currentTimeMillis() - startMS;
        System.out.println("Finish MS : "+counts+" Time: "+time);
        
        Long startL = System.currentTimeMillis();
        counts=0;
        while (listB.hasNext()) {            
            counts++;
            List<Integer> res = listB.next();
            System.out.println(res);
        }
        time = System.currentTimeMillis() - startL;
        System.out.println("Finish LB : "+counts+" Time: "+time);
    }

    /**
     * Test of initialize method, of class IntegerListIterator.
     *
    public void testInitialize() {
        System.out.println("initialize");
        Integer maxToShare = null;
        Integer minPerSlot = null;
        IntegerListIterator instance = new IntegerListIteratorImpl();
        instance.initialize(maxToShare, minPerSlot);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of next method, of class IntegerListIterator.
     *
    public void testNext() {
        System.out.println("next");
        IntegerListIterator instance = new IntegerListIteratorImpl();
        List expResult = null;
        List result = instance.next();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/
}
