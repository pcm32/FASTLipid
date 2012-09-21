/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.generator;

import uk.ac.ebi.lipidhome.fastlipid.generator.SuccesiveIntegerListIterator;
import java.util.List;
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
     * Test of initialize method, of class SuccesiveIntegerListIterator.
     */
    public void testInitialize() {
        System.out.println("initialize");
        Integer maxToShare = 20;
        Integer minPerSlot = 2;
        SuccesiveIntegerListIterator instance = new SuccesiveIntegerListIterator(4, 2);
        instance.initialize(maxToShare, minPerSlot);
    }

    /**
     * Test of next method, of class SuccesiveIntegerListIterator.
     */
    public void testIteration() {
        System.out.println("Iteration");
        Integer maxToShare = 20;
        Integer minPerSlot = 2;
        SuccesiveIntegerListIterator instance = new SuccesiveIntegerListIterator(4, 2);
        instance.initialize(maxToShare, minPerSlot);
        int counter = 1;
        while (instance.hasNext()) {
            System.out.print(counter + ".- ");
            for (Integer sol : instance.next()) {
                System.out.print(sol + "\t");
            }
            System.out.println("");
            counter++;
        }
    }

    /**
     * Test of next method, of class SuccesiveIntegerListIterator.
     */
    public void testIterationOneSlot() {
        System.out.println("Iteration of a single slot (so should only produce one result)");
        Integer maxToShare = 20;
        Integer minPerSlot = 2;
        SuccesiveIntegerListIterator instance = new SuccesiveIntegerListIterator(1, 2);
        instance.initialize(maxToShare, minPerSlot);
        int counter = 1;
        assertTrue(instance.hasNext());
        List<Integer> sols = instance.next();
        System.out.print(counter + ".- "+ sols.get(0));
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

        SuccesiveIntegerListIterator carbonIterator = new SuccesiveIntegerListIterator(fattyAcids, carbonStep);
        carbonIterator.initialize(totalCarbons, minCarbPerFA);


        while (carbonIterator.hasNext()) {
            List<Integer> carbonDisp = carbonIterator.next();
            SuccesiveIntegerListIterator doubleBondIterator = new SuccesiveIntegerListIterator(fattyAcids, 1);
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
     * Test of hasNext method, of class SuccesiveIntegerListIterator.
     *
     * public void testHasNext() { System.out.println("hasNext"); SuccesiveIntegerListIterator instance = null; boolean
     * expResult = false; boolean result = instance.hasNext(); assertEquals(expResult, result); // TODO review the
     * generated test code and remove the default call to fail. fail("The test case is a prototype.");
    }
     */
}
