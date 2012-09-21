/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.generator;

import uk.ac.ebi.lipidhome.fastlipid.generator.LinkersIterator;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;

/**
 *
 * @author pmoreno
 */
public class LinkersIteratorTest extends TestCase {
    
    public LinkersIteratorTest(String testName) {
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
     * Test of next method, of class LinkersIterator.
     *
    public void testNext() {
        System.out.println("next");
        LinkersIterator instance = null;
        List expResult = null;
        List result = instance.next();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasNext method, of class LinkersIterator.
     */
    public void testWithGlycerol() {
        System.out.println("hasNext");
        HeadGroup hg = HeadGroup.Glycerol;
        List<SingleLinkConfiguration> possibleLinkers = Arrays.asList(SingleLinkConfiguration.Acyl,SingleLinkConfiguration.Alkyl);
        LinkersIterator iterator = new LinkersIterator(possibleLinkers, hg);
        Integer expectedPerms = new Double(Math.pow(possibleLinkers.size(), hg.getNumOfSlots())).intValue(); // since both are integers, this should also is an integer.
        Integer perms=0;
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
            perms++;
        }
        System.out.println("Expected permutations : "+expectedPerms);
        System.out.println("Actual permutations : "+perms);
        assertEquals(expectedPerms, perms);
        
    }
}
