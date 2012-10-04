/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.composition;

import junit.framework.TestCase;

/**
 *
 * @author pmoreno
 */
public class AccAscCompositionConstrainedTest extends TestCase {
    
    public AccAscCompositionConstrainedTest(String testName) {
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

    public void testSomeMethod() {
        System.out.println("next");
        AccAscComposition instance = new AccAscCompositionConstrained(20,3,2,2);
        while(instance.hasNext()) {
            System.out.println(instance.next());
        }
    }
}
