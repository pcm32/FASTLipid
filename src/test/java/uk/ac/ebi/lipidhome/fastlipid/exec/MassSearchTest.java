/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import junit.framework.TestCase;

/**
 *
 * @author pmoreno
 */
public class MassSearchTest extends TestCase {
    
    public MassSearchTest(String testName) {
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
     * Test of main method, of class MassSearch.
     */
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = {"-m","411.2022037","-o","/tmp/test","-r","I","-t","5","-h","PE"};
        MassSearch.main(args);
    }
}
