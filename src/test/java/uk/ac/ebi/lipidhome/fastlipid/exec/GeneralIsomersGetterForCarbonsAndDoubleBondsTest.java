/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import java.util.*;
import junit.framework.TestCase;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import uk.ac.ebi.lipidhome.fastlipid.generator.ChainFactoryGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.GeneralIsomersGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.LNetMoleculeGeneratorException;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.structure.SpeciesInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondDistance3nPlus2Rule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.NoDoubleBondsTogetherRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.StarterDoubleBondRule;

/**
 *
 * @author pmoreno
 */
public class GeneralIsomersGetterForCarbonsAndDoubleBondsTest extends TestCase {

    public GeneralIsomersGetterForCarbonsAndDoubleBondsTest(String testName) {
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
     * Test of complete execution, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     */
    public void testCompleteExecution() {
        Integer carbons = 36;

        /**
         * Before setting up chain factories, we need to read the head and decide the number of chains for it. We should
         * avoid having to deal with chainFactories at this level. What is the need for it??
         */
        List<BondRule> rules = Arrays.asList(new BondDistance3nPlus2Rule(), new NoDoubleBondsTogetherRule(), new StarterDoubleBondRule(2));
        ChainFactoryGenerator cfGenerator = new ChainFactoryGenerator(rules,
                new BooleanRBCounterStartSeeder(rules),
                true);

        GeneralIsomersGetterForCarbonsAndDoubleBonds igfcadb = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        igfcadb.setChainFactoryGenerator(cfGenerator);

        igfcadb.setHead(HeadGroup.PC);
        igfcadb.setMaxNumberOfCarbonsInSingleChain(30);
        List<SingleLinkConfiguration> linkers = new ArrayList<SingleLinkConfiguration>();
        linkers.add(SingleLinkConfiguration.Acyl);
        linkers.add(SingleLinkConfiguration.Acyl);
        igfcadb.setLinkConfigs(linkers.toArray(new SingleLinkConfiguration[2]));

        System.out.println("C\tDB\tNumStruc\tMolForm\tMass\tTime");
        for (int i = carbons - 10; i < carbons + 10; i++) {
            for (int b = 0; b < carbons / 2; b++) {

                long start = System.currentTimeMillis();
                try {
                    igfcadb.setCarbons(i);
                } catch (LNetMoleculeGeneratorException e) {
                    //System.err.println("Not generating for "+i+" carbons, invalid entry. Turn on exotic mode for this.");
                    continue;
                }
                igfcadb.setDoubleBonds(b);
                if (i == 32 && b == 11) {
                    System.out.println("We are at the weird case!!!");
                }
                igfcadb.exec();
                SpeciesInfoContainer stats = igfcadb.getIsomerStatistics();
                long elapsed = System.currentTimeMillis() - start;
                if (igfcadb.getFormula() != null) {
                    System.out.println(i + "\t" + b + "\t" + igfcadb.getNumOfStructs()
                            + "\t" + igfcadb.getFormula() + "\t" + igfcadb.getExactMass()
                            + "\t" + elapsed);
                }

                if (igfcadb.getNumOfStructs() == 0) {
                    break;
                }
            }
        }



    }

    /**
     * Test of reset method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testReset() {
        System.out.println("reset");
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.reset();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of exec method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testExec() {
        System.out.println("exec");
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.exec();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSupSpeciesIterator method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testGetSupSpeciesIterator() {
        System.out.println("getSupSpeciesIterator");
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        Iterator expResult = null;
        Iterator result = instance.getSupSpeciesIterator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of configForSmilesOutput method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testConfigForSmilesOutput() {
        System.out.println("configForSmilesOutput");
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.configForSmilesOutput();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of configForMassAndFormula method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testConfigForMassAndFormula() {
        System.out.println("configForMassAndFormula");
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.configForMassAndFormula();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCarbons method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testSetCarbons() {
        System.out.println("setCarbons");
        Integer carbons = null;
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.setCarbons(carbons);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDoubleBonds method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testSetDoubleBonds() {
        System.out.println("setDoubleBonds");
        Integer doubleBonds = null;
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.setDoubleBonds(doubleBonds);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setGenerator method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testSetGenerator() {
        System.out.println("setGenerator");
        GeneralIsomersGenerator generator = null;
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.setGenerator(generator);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumOfStructs method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testGetNumOfStructs() {
        System.out.println("getNumOfStructs");
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        Integer expResult = null;
        Integer result = instance.getNumOfStructs();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setNumOfStructs method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testSetNumOfStructs() {
        System.out.println("setNumOfStructs");
        Integer numOfStructs = null;
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.setNumOfStructs(numOfStructs);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFormula method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testGetFormula() {
        System.out.println("getFormula");
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        String expResult = "";
        String result = instance.getFormula();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFormula method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testSetFormula() {
        System.out.println("setFormula");
        String formula = "";
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.setFormula(formula);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getExactMass method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testGetExactMass() {
        System.out.println("getExactMass");
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        Double expResult = null;
        Double result = instance.getExactMass();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setExactMass method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testSetExactMass() {
        System.out.println("setExactMass");
        Double exactMass = null;
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.setExactMass(exactMass);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNaturalMass method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testGetNaturalMass() {
        System.out.println("getNaturalMass");
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        Double expResult = null;
        Double result = instance.getNaturalMass();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setNaturalMass method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testSetNaturalMass() {
        System.out.println("setNaturalMass");
        Double exactMass = null;
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.setNaturalMass(exactMass);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getChainConfigs method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testGetChainConfigs() {
        System.out.println("getChainConfigs");
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        Set expResult = null;
        Set result = instance.getChainConfigs();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMaxNumberOfCarbonsInSingleChain method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     *
    public void testSetMaxNumberOfCarbonsInSingleChain() {
        System.out.println("setMaxNumberOfCarbonsInSingleChain");
        Integer maxNumCarbons = null;
        GeneralIsomersGetterForCarbonsAndDoubleBonds instance = new GeneralIsomersGetterForCarbonsAndDoubleBonds();
        instance.setMaxNumberOfCarbonsInSingleChain(maxNumCarbons);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setExoticMode method, of class GeneralIsomersGetterForCarbonsAndDoubleBonds.
     */



}
