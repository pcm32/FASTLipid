/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import mass.ChainEstimatorByMass;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import structure.HeadGroup;
import structure.SingleLinkConfiguration;

/**
 *
 * @author pmoreno
 */
public class ChainEstimatorByMassTest extends TestCase {
    
    public ChainEstimatorByMassTest(String testName) {
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
     * Test of call method, of class ChainEstimatorByMass.
     */
    public void testCall() throws Exception {
        System.out.println("call");
        List<SingleLinkConfiguration> linkers = new ArrayList<SingleLinkConfiguration>();
        linkers.add(SingleLinkConfiguration.Acyl);
        ChainEstimatorByMass instance = new ChainEstimatorByMass(180d, 300d, HeadGroup.DG, linkers);
        LipidChainConfigEstimate result = instance.call();
        System.out.println("Max carbons : "+result.getMaxCarbons()+" : "+result.getMaxDoubleBonds(result.getMaxCarbons())+" : "+result.getMinDoubleBonds(result.getMaxCarbons()) + "::" + result.getMaxMass());
        System.out.println("Min carbons : "+result.getMinCarbons()+" : "+result.getMaxDoubleBonds(result.getMinCarbons())+" : "+result.getMinDoubleBonds(result.getMinCarbons()) + "::" + result.getMinMass());
        
    }
}
