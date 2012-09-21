/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import junit.framework.TestCase;
import org.openscience.cdk.exception.CDKException;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import uk.ac.ebi.lipidhome.fastlipid.generator.ChainFactoryGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.GeneralIsomersGenerator;
import uk.ac.ebi.lipidhome.fastlipid.mass.ChainEstimatorByMass;
import uk.ac.ebi.lipidhome.fastlipid.mass.FutureEstimatesIterator;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.IsomerInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondDistance3nPlus2Rule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.NoDoubleBondsTogetherRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.StarterDoubleBondRule;
import uk.ac.ebi.lipidhome.fastlipid.util.LipidChainConfigEstimate;

/**
 *
 * @author pmoreno
 */
public class EstimateBasedGeneratorIteratorTest extends TestCase {

    public EstimateBasedGeneratorIteratorTest(String testName) {
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
     * Test of hasNext method, of class EstimateBasedGeneratorIterator.
     *
     * public void testHasNext() { System.out.println("hasNext"); EstimateBasedGeneratorIterator instance = null;
     * boolean expResult = false; boolean result = instance.hasNext(); assertEquals(expResult, result); // TODO review
     * the generated test code and remove the default call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of next method, of class EstimateBasedGeneratorIterator.
     */
    public void testNext() throws CDKException, IOException {
        System.out.println("Iterator nextTest");
        Double minMass = 180d;
        Double maxMass = 300d;
        System.out.println("Mass range : "+minMass+" : "+maxMass);
        List<HeadGroup> allowedHeadGroups = Arrays.asList(HeadGroup.DG, HeadGroup.PE, HeadGroup.PI);
        List<SingleLinkConfiguration> allowedLinkers = Arrays.asList(SingleLinkConfiguration.Acyl, SingleLinkConfiguration.Alkyl);
        List<Future<LipidChainConfigEstimate>> estimates = new ArrayList<Future<LipidChainConfigEstimate>>();
        ExecutorService execServ = Executors.newFixedThreadPool(4);
        for (HeadGroup headGroup : allowedHeadGroups) {
            ChainEstimatorByMass estimatorByMass = new ChainEstimatorByMass(minMass, maxMass, headGroup, allowedLinkers);
            estimates.add(execServ.submit(estimatorByMass));
        }
        execServ.shutdown();
        FutureEstimatesIterator estimatesIterator = new FutureEstimatesIterator(estimates);
        List<BondRule> rules = Arrays.asList(new BondDistance3nPlus2Rule(), new NoDoubleBondsTogetherRule(), new StarterDoubleBondRule(2));
        ChainFactoryGenerator cfGen = new ChainFactoryGenerator(rules, new BooleanRBCounterStartSeeder(rules), true);
        
        ChemInfoContainerGenerator cicg = new ChemInfoContainerGenerator();
        cicg.setGenerateInChi(false);
        cicg.setGenerateInChiKey(false);
        cicg.setGenerateInChIAux(false);
        cicg.setGenerateMolFormula(true);
        cicg.setGenerateMass(true);
        cicg.setGenerateSmiles(false);
        cicg.setUseCachedObjects(true);
                
        while (estimatesIterator.hasNext()) {
            LipidChainConfigEstimate estimate = estimatesIterator.next();
            System.out.println("Res: " + estimate.getHeadGroup() + ":" + estimate.getMaxCarbons() + "-" + estimate.getMinCarbons() + 
                    "\tMaxMass:"+estimate.getMaxMass() + "\tMinMass:"+estimate.getMinMass());

            EstimateBasedGeneratorIterator instance = new EstimateBasedGeneratorIterator(estimate, allowedLinkers, cfGen, cicg, false);
            while(instance.hasNext()) {
                GeneralIsomersGenerator result = instance.next();
                result.execute();
                IsomerInfoContainer res = result.getIsomerInfoContainer();
                System.out.print("\tTC:"+res.getNumOfCarbons()+"::TD:"+res.getNumOfDoubleBonds());
                if(res.getNumOfMolsGenerated()>0) {
                    System.out.println("\t"+res.getNumOfMolsGenerated()+"\t"+res.getMolecularFormula()+":Head::"+res.getHeadGroup()
                        +"::Linkers:"+res.getLinkers()+"::Mass:"+res.getMass());
                } else {
                    System.out.println("\t"+res.getNumOfMolsGenerated());
                }
            }
        }

    }
    /**
     * Test of remove method, of class EstimateBasedGeneratorIterator.
     *
     * public void testRemove() { System.out.println("remove"); EstimateBasedGeneratorIterator instance = null;
     * instance.remove(); // TODO review the generated test code and remove the default call to fail. fail("The test
     * case is a prototype.");
    }
     */
}
