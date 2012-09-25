/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author pmoreno
 */
public class SuccesiveIntegerListIteratorTest extends TestCase {

    public SuccesiveIntegerListIteratorTest(String testName) {
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
     * Test of initialize method, of class MultiSetBasedIntegerListIterator.
     */
    public void testInitialize() {
        System.out.println("initialize");
        Integer maxToShare = 20;
        Integer minPerSlot = 2;
        MultiSetBasedIntegerListIterator instance = new MultiSetBasedIntegerListIterator(4, 2);
        instance.initialize(maxToShare, minPerSlot);
    }

    /**
     * Test of next method, of class MultiSetBasedIntegerListIterator.
     */
    public void testIteration() {
        System.out.println("Iteration");
        Integer maxToShare = 20;
        Integer minPerSlot = 2;
        MultiSetBasedIntegerListIterator instance = new MultiSetBasedIntegerListIterator(4, 2);
        instance.initialize(maxToShare, minPerSlot);
        int counter = 0;
        Map<List<Integer>, Integer> seenVectors = new HashMap<List<Integer>, Integer>();
        Integer ch = 1;
        while (instance.hasNext()) {
            counter++;
            System.out.print(counter + ".- ");
            List<Integer> sols = instance.next();
            String repeatStr = " -- " + ch;
            if (seenVectors.containsKey(sols)) {
                repeatStr = " -- Repeated " + seenVectors.get(sols);
            } else {
                seenVectors.put(sols, ch++);
            }
            System.out.println(sols+repeatStr);            
        }
        assertEquals(70, counter);
    }

    /**
     * Test of next method, of class MultiSetBasedIntegerListIterator.
     */
    public void testIterationOneSlot() {
        System.out.println("Iteration of a single slot (so should only produce one result)");
        Integer maxToShare = 20;
        Integer minPerSlot = 2;
        MultiSetBasedIntegerListIterator instance = new MultiSetBasedIntegerListIterator(1, 2);
        instance.initialize(maxToShare, minPerSlot);
        int counter = 1;
        assertTrue(instance.hasNext());
        List<Integer> sols = instance.next();
        System.out.print(counter + ".- " + sols.get(0));
        assertEquals(1, sols.size());
        assertFalse(instance.hasNext());
    }

    public void testDoubleBondsAndCarbons() {
        System.out.println("Carbons and double bonds trial");
        Integer totalCarbons = 40;
        Integer minCarbPerFA = 2;
        Integer carbonStep = 2;
        Integer fattyAcids = 4;
        Integer totalDoubleBonds = 5;
        Integer minDoubleBondPerFA = 0;

        MultiSetBasedIntegerListIterator carbonIterator = new MultiSetBasedIntegerListIterator(fattyAcids, carbonStep);
        carbonIterator.initialize(totalCarbons, minCarbPerFA);


        while (carbonIterator.hasNext()) {
            List<Integer> carbonDisp = carbonIterator.next();
            MultiSetBasedIntegerListIterator doubleBondIterator = new MultiSetBasedIntegerListIterator(fattyAcids, 1);
            doubleBondIterator.initialize(totalDoubleBonds, minDoubleBondPerFA);
            System.out.println("Carbons : " + carbonDisp);
            while (doubleBondIterator.hasNext()) {
                List<Integer> doubleBonds = doubleBondIterator.next();
                if (allDoubleBondPosSmaller(doubleBonds, carbonDisp)) {
                    System.out.println(" DBonds : " + doubleBonds);
                }
            }
        }
    }

    private boolean allDoubleBondPosSmaller(List<Integer> doubleBonds, List<Integer> carbonDisp) {
        for (int i = 0; i < doubleBonds.size(); i++) {
            if (doubleBonds.get(i) >= carbonDisp.get(i)) {
                return false;
            }
        }
        return true;
    }
    /**
     * Test of hasNext method, of class MultiSetBasedIntegerListIterator.
     *
     * public void testHasNext() { System.out.println("hasNext"); MultiSetBasedIntegerListIterator instance = null; boolean
     * expResult = false; boolean result = instance.hasNext(); assertEquals(expResult, result); // TODO review the
     * generated test code and remove the default call to fail. fail("The test case is a prototype."); }
     */
}
