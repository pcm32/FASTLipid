/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import uk.ac.ebi.lipidhome.fastlipid.exec.MassRangeIsomersGetter;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import junit.framework.TestCase;
import uk.ac.ebi.lipidhome.fastlipid.generator.ChainFactoryGenerator;
import org.openscience.cdk.exception.CDKException;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SpeciesInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondDistance3nPlus2Rule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.NoDoubleBondsTogetherRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.StarterDoubleBondRule;
import uk.ac.ebi.lipidhome.fastlipid.mass.ChainEstimatorByMass;
import uk.ac.ebi.lipidhome.fastlipid.mass.FutureEstimatesIterator;
import uk.ac.ebi.lipidhome.fastlipid.util.LipidChainConfigEstimate;

/**
 *
 * @author pmoreno
 */
public class MassRangeIsomersGetterTest extends TestCase {

    public MassRangeIsomersGetterTest(String testName) {
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
     * Test of next method, of class MassRangeIsomersGetter.
     */
    public void testNext() throws CDKException, IOException {
        System.out.println("next");
        Long startSetup = System.currentTimeMillis();
        
        List<HeadGroup> allowedHeadGroups = Arrays.asList(HeadGroup.DG, HeadGroup.PE, HeadGroup.PI);
        List<SingleLinkConfiguration> allowedLinkers = Arrays.asList(SingleLinkConfiguration.Acyl, SingleLinkConfiguration.Alkyl);

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

        List<Double> minMasses = Arrays.asList(180d, 190d, 200d, 210d, 220d, 230d, 240d, 250d, 255d, 260d, 258.1, 272.12);
        List<Double> maxMasses = Arrays.asList(180.001d, 190.01d, 200.001, 210.01d, 220.1d, 230.02d, 240.01d, 244d, 255d, 260d, 258.2, 272.13);
        
        Long elapsedSetup = System.currentTimeMillis() - startSetup;
        System.out.println("Setup time : "+elapsedSetup);

        System.out.println("Min\tMax\tNumOfMols\tElapsed");
        
        for (int i = 0; i < minMasses.size(); i++) {
            Double minMass = minMasses.get(i);
            Double maxMass = maxMasses.get(i);

            Long startTime = System.currentTimeMillis();
            MassRangeIsomersGetter instance = new MassRangeIsomersGetter(allowedHeadGroups, allowedLinkers, cfGen, minMass, maxMass, Boolean.FALSE, cicg);
            int numOfMols = 0;
            while (instance.hasNext()) {
                SpeciesInfoContainer res = instance.next();
                numOfMols += res.getNumOfMolsGenerated();
                //System.out.print("\tTC:" + res.getNumOfCarbons() + "::TD:" + res.getNumOfDoubleBonds());
                //if (res.getNumOfMolsGenerated() > 0) {
                //System.out.println("\t" + res.getNumOfMolsGenerated() + "\t" + res.getMolecularFormula() + ":Head::" + res.getHeadGroup()
                //        + "::Linkers:" + res.getLinkers() + "::Mass:" + res.getMass());
                //} else {
                //System.out.println("\t" + res.getNumOfMolsGenerated());
                //}
            }
            Long elapsed = System.currentTimeMillis() - startTime;
            System.out.println(minMass + "\t" + maxMass + "\t" + numOfMols + "\t" + elapsed);
        }

    }
}
