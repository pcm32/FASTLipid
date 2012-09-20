/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import uk.ac.ebi.lipidhome.fastlipid.exec.IterativePhospholipidGetterDefCarbsDoubleBondPerChain;
import uk.ac.ebi.lipidhome.fastlipid.exec.IsomersGetterForCarbonsAndDoubleBonds;
import java.util.Set;
import junit.framework.TestCase;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChainFactory;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.structure.PooledChainFactory;
import structure.rule.BondDistance3nPlus2Rule;
import structure.rule.NoDoubleBondsTogetherRule;
import structure.rule.StarterDoubleBondRule;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import java.util.Arrays;
import java.util.List;
import uk.ac.ebi.lipidhome.fastlipid.lnetmoleculegenerator.LNetMoleculeGeneratorException;
import structure.rule.BondRule;

/**
 *
 * @author pmoreno
 */
public class IsomersGetterForCarbonsAndDoubleBondsTest extends TestCase {

    public IsomersGetterForCarbonsAndDoubleBondsTest(String testName) {
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

    public void testGetOddNumberedChainsLipids_7_1() throws LNetMoleculeGeneratorException {
        Integer numOfCarbons = 7;
        Integer numOfDoubleBonds = 1;
        Integer expectedArrangements = 4;
        runGeneratorForNumOfCarbonsAndDoubleBonds(numOfCarbons, numOfDoubleBonds, expectedArrangements);

    }

    public void testGetOddNumberedChainsLipids_6_1() throws LNetMoleculeGeneratorException {
        Integer numOfCarbons = 6;
        Integer numOfDoubleBonds = 1;
        Integer expectedNumberOfArrangements = 2; // 4:1_2:0 and reverse.
        runGeneratorForNumOfCarbonsAndDoubleBonds(numOfCarbons, numOfDoubleBonds, expectedNumberOfArrangements);

    }

    public void testGetOddNumberedChainsLipids_10_1() throws LNetMoleculeGeneratorException {
        Integer numOfCarbons = 10;
        Integer numOfDoubleBonds = 1;
        Integer expectedArrangements = 10;
        runGeneratorForNumOfCarbonsAndDoubleBonds(numOfCarbons, numOfDoubleBonds, expectedArrangements);

    }

    private void runGeneratorForNumOfCarbonsAndDoubleBonds(Integer numOfCarbons, Integer numOfDoubleBonds, Integer arrangementsExp) throws LNetMoleculeGeneratorException {
        IsomersGetterForCarbonsAndDoubleBonds igfcadb = getIsomersGetterForCarbonsAndDoubleBondsConfigured();
        igfcadb.setExoticMode(true);
        igfcadb.setStepOfChangForNumberOfCarbonsInChains(1);


        System.out.println("C\tDB\tNumStruc\tMolForm\tMass\tTime");
        //for (int i = 4; i<16; i++) {

        long start = System.currentTimeMillis();
        igfcadb.setCarbons(numOfCarbons);
        igfcadb.setDoubleBonds(numOfDoubleBonds);
        igfcadb.exec();
        long elapsed = System.currentTimeMillis() - start;
        if (igfcadb.getFormula() != null) {
            System.out.println(numOfCarbons + "\t" + numOfDoubleBonds + "\t" + igfcadb.getNumOfStructs()
                    + "\t" + igfcadb.getFormula() + "\t" + igfcadb.getExactMass()
                    + "\t" + elapsed);
        }
        Set<String> chainConfigs = igfcadb.getChainConfigs();
        assertEquals(arrangementsExp.intValue(), chainConfigs.size());
        for (String config : chainConfigs) {
            System.out.println("Possible: " + config);
        }

        //System.out.println("Carbons & DB:"+i+"\t"+b);
        //System.out.println("Number of structures:\t" + igfcadb.getNumOfStructs());
        //System.out.println("Formula:\t" + igfcadb.getFormula());
        //System.out.println("Mass:\t" + igfcadb.getExactMass());
    }

    private IsomersGetterForCarbonsAndDoubleBonds getIsomersGetterForCarbonsAndDoubleBondsConfigured() {
        ChainFactory cfA = new PooledChainFactory();
        ChainFactory cfB = new PooledChainFactory();

        List<BondRule> rules = Arrays.asList(new BondDistance3nPlus2Rule(),
                new NoDoubleBondsTogetherRule(),
                new StarterDoubleBondRule(2));

        cfA.setSeeder(new BooleanRBCounterStartSeeder(rules));
        cfB.setSeeder(new BooleanRBCounterStartSeeder(rules));


        // We can have this configurates outside... although this runs only once.
        for (BondRule bondRule : rules) {
            cfA.addAlwaysRule(bondRule);
            cfB.addAlwaysRule(bondRule);
        }

        cfA.setUseRuleBasedBooleanCounter(true);
        cfB.setUseRuleBasedBooleanCounter(true);

        IsomersGetterForCarbonsAndDoubleBonds igfcadb = new IsomersGetterForCarbonsAndDoubleBonds();
        igfcadb.setChainFactories(cfA, cfB);
        //igfcadb.setPathToHead(pathToHead);
        igfcadb.setHeadMolStream(IterativePhospholipidGetterDefCarbsDoubleBondPerChain.class.getResourceAsStream("/structures/models/PC.mol"));
        igfcadb.setMaxNumberOfCarbonsInSingleChain(30);
        igfcadb.setLinkConfigsR1R2(SingleLinkConfiguration.Acyl, SingleLinkConfiguration.Acyl);
        return igfcadb;
    }
    /**
     * Test of main method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testMain() { System.out.println("main"); String[] args = null;
     * IsomersGetterForCarbonsAndDoubleBonds.main(args); // TODO review the generated test code and remove the default
     * call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of reset method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testReset() { System.out.println("reset"); IsomersGetterForCarbonsAndDoubleBonds instance = new
     * IsomersGetterForCarbonsAndDoubleBonds(); instance.reset(); // TODO review the generated test code and remove the
     * default call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of exec method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testExec() { System.out.println("exec"); IsomersGetterForCarbonsAndDoubleBonds instance = new
     * IsomersGetterForCarbonsAndDoubleBonds(); instance.exec(); // TODO review the generated test code and remove the
     * default call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of configForSmilesOutput method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testConfigForSmilesOutput() { System.out.println("configForSmilesOutput");
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.configForSmilesOutput(); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype."); }
     *
     * /**
     * Test of configForMassAndFormula method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testConfigForMassAndFormula() { System.out.println("configForMassAndFormula");
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.configForMassAndFormula(); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype."); }
     *
     * /**
     * Test of setCarbons method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetCarbons() throws Exception { System.out.println("setCarbons"); Integer carbons = null;
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.setCarbons(carbons); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype."); }
     *
     * /**
     * Test of setDoubleBonds method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetDoubleBonds() { System.out.println("setDoubleBonds"); Integer doubleBonds = null;
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.setDoubleBonds(doubleBonds); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype."); }
     *
     * /**
     * Test of setPathToHead method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetPathToHead() { System.out.println("setPathToHead"); String pathToHead = "";
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.setPathToHead(pathToHead); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype."); }
     *
     * /**
     * Test of setGenerator method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetGenerator() { System.out.println("setGenerator"); IsomersGenerator generator = null;
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.setGenerator(generator); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype."); }
     *
     * /**
     * Test of getNumOfStructs method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testGetNumOfStructs() { System.out.println("getNumOfStructs"); IsomersGetterForCarbonsAndDoubleBonds
     * instance = new IsomersGetterForCarbonsAndDoubleBonds(); Integer expResult = null; Integer result =
     * instance.getNumOfStructs(); assertEquals(expResult, result); // TODO review the generated test code and remove
     * the default call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of setNumOfStructs method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetNumOfStructs() { System.out.println("setNumOfStructs"); Integer numOfStructs = null;
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.setNumOfStructs(numOfStructs); // TODO review the generated test code and remove the default call to
     * fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of getFormula method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testGetFormula() { System.out.println("getFormula"); IsomersGetterForCarbonsAndDoubleBonds instance =
     * new IsomersGetterForCarbonsAndDoubleBonds(); String expResult = ""; String result = instance.getFormula();
     * assertEquals(expResult, result); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype."); }
     *
     * /**
     * Test of setFormula method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetFormula() { System.out.println("setFormula"); String formula = "";
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.setFormula(formula); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype."); }
     *
     * /**
     * Test of getExactMass method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testGetExactMass() { System.out.println("getExactMass"); IsomersGetterForCarbonsAndDoubleBonds
     * instance = new IsomersGetterForCarbonsAndDoubleBonds(); Double expResult = null; Double result =
     * instance.getExactMass(); assertEquals(expResult, result); // TODO review the generated test code and remove the
     * default call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of setExactMass method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetExactMass() { System.out.println("setExactMass"); Double exactMass = null;
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.setExactMass(exactMass); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype."); }
     *
     * /**
     * Test of getNaturalMass method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testGetNaturalMass() { System.out.println("getNaturalMass"); IsomersGetterForCarbonsAndDoubleBonds
     * instance = new IsomersGetterForCarbonsAndDoubleBonds(); Double expResult = null; Double result =
     * instance.getNaturalMass(); assertEquals(expResult, result); // TODO review the generated test code and remove the
     * default call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of setNaturalMass method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetNaturalMass() { System.out.println("setNaturalMass"); Double exactMass = null;
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.setNaturalMass(exactMass); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype."); }
     *
     * /**
     * Test of setChainFactories method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetChainFactories() { System.out.println("setChainFactories"); ChainFactory cfA = null;
     * ChainFactory cfB = null; IsomersGetterForCarbonsAndDoubleBonds instance = new
     * IsomersGetterForCarbonsAndDoubleBonds(); instance.setChainFactories(cfA, cfB); // TODO review the generated test
     * code and remove the default call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of getChainConfigs method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testGetChainConfigs() { System.out.println("getChainConfigs"); IsomersGetterForCarbonsAndDoubleBonds
     * instance = new IsomersGetterForCarbonsAndDoubleBonds(); Set expResult = null; Set result =
     * instance.getChainConfigs(); assertEquals(expResult, result); // TODO review the generated test code and remove
     * the default call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of setHeadMolStream method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetHeadMolStream() { System.out.println("setHeadMolStream"); InputStream headMolStream = null;
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.setHeadMolStream(headMolStream); // TODO review the generated test code and remove the default call to
     * fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of setMaxNumberOfCarbonsInSingleChain method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetMaxNumberOfCarbonsInSingleChain() { System.out.println("setMaxNumberOfCarbonsInSingleChain");
     * Integer maxNumCarbons = null; IsomersGetterForCarbonsAndDoubleBonds instance = new
     * IsomersGetterForCarbonsAndDoubleBonds(); instance.setMaxNumberOfCarbonsInSingleChain(maxNumCarbons); // TODO
     * review the generated test code and remove the default call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of setLinkConfigsR1R2 method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetLinkConfigsR1R2() { System.out.println("setLinkConfigsR1R2"); SingleLinkConfiguration confR1 =
     * null; SingleLinkConfiguration confR2 = null; IsomersGetterForCarbonsAndDoubleBonds instance = new
     * IsomersGetterForCarbonsAndDoubleBonds(); instance.setLinkConfigsR1R2(confR1, confR2); // TODO review the
     * generated test code and remove the default call to fail. fail("The test case is a prototype."); }
     *
     * /**
     * Test of setExoticMode method, of class IsomersGetterForCarbonsAndDoubleBonds.
     *
     * public void testSetExoticMode() { System.out.println("setExoticMode"); boolean exotic = false;
     * IsomersGetterForCarbonsAndDoubleBonds instance = new IsomersGetterForCarbonsAndDoubleBonds();
     * instance.setExoticMode(exotic); // TODO review the generated test code and remove the default call to fail.
     * fail("The test case is a prototype.");
    }
     */
}
