/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.util;

import uk.ac.ebi.lipidhome.fastlipid.util.LipidChainConfigEstimate;
import uk.ac.ebi.lipidhome.fastlipid.mass.ChainEstimatorByMass;
import uk.ac.ebi.lipidhome.fastlipid.mass.FutureEstimatesIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import junit.framework.TestCase;
import org.openscience.cdk.exception.CDKException;
import structure.HeadGroup;
import structure.SingleLinkConfiguration;

/**
 *
 * @author pmoreno
 */
public class FutureEstimatesIteratorTest extends TestCase {
    
    public FutureEstimatesIteratorTest(String testName) {
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
     * Test of hasNext method, of class FutureEstimatesIterator.
     */
    public void testNext() throws CDKException, IOException {
        System.out.println("next");
        Double minMass = 180d;
        Double maxMass = 300d;
        List<HeadGroup> allowedHeadGroups = Arrays.asList(HeadGroup.DG, HeadGroup.PE, HeadGroup.PI);
        List<SingleLinkConfiguration> allowedLinkers = Arrays.asList(SingleLinkConfiguration.Acyl,SingleLinkConfiguration.Alkyl);
        List<Future<LipidChainConfigEstimate>> estimates = new ArrayList<Future<LipidChainConfigEstimate>>();
        ExecutorService execServ = Executors.newFixedThreadPool(4);
        for (HeadGroup headGroup : allowedHeadGroups) {
            ChainEstimatorByMass estimatorByMass = new ChainEstimatorByMass(minMass, maxMass, headGroup, allowedLinkers);
            estimates.add(execServ.submit(estimatorByMass));
        }
        execServ.shutdown();
        FutureEstimatesIterator instance = new FutureEstimatesIterator(estimates);
        while(instance.hasNext()) {
            LipidChainConfigEstimate estimate = instance.next();
            System.out.println("Res: "+estimate.getHeadGroup()+":"+estimate.getMaxCarbons()+"-"+estimate.getMinCarbons());
        }
    }


}
