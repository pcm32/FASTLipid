/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package counter;

import junit.framework.TestCase;

/**
 *
 * @author pmoreno
 */
public class RecursiveBinaryCounterTest extends TestCase {

    public RecursiveBinaryCounterTest(String testName) {
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
     * Test of nextBinary method, of class RecursiveBinaryCounter.
     */
    public void testNextBinary() {
        long start = System.currentTimeMillis();
        long current;
        RecursiveBinaryCounter binCounter = new RecursiveBinaryCounter(10, 1);
        int generatedBinaries = 0;
        while (binCounter.hasNext()) {
            char[] binary = binCounter.nextBinary();
            assertNotNull(binary);
            generatedBinaries++;
            System.out.println(new String(binary));
        }
        System.out.println("Generated structs:" + generatedBinaries);
        current = System.currentTimeMillis() - start;
        System.out.println("Time:" + current);
    }
}
