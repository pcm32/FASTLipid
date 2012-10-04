/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.composition;

import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author pmoreno
 */
public class AccAscCompositionTest extends TestCase {
    
    public AccAscCompositionTest(String testName) {
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
     * Test of next method, of class AccAscComposition.
     */
    public void testNext() {
        System.out.println("next");
        AccAscComposition instance = new AccAscComposition(3);
        while(instance.hasNext()) {
            System.out.println(instance.next());
        }
    }
}
