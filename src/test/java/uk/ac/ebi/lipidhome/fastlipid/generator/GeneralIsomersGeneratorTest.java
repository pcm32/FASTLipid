/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.generator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SpeciesInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.SubSpecies;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondDistance3nPlus2Rule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.NoDoubleBondsTogetherRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.StarterDoubleBondRule;

/**
 *
 * @author pmoreno
 */
public class GeneralIsomersGeneratorTest extends TestCase {
    
    public GeneralIsomersGeneratorTest(String testName) {
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
     * Test of setChemInfoContainerGenerator method, of class GeneralIsomersGenerator.
     *
    public void testSetChemInfoContainerGenerator() {
        System.out.println("setChemInfoContainerGenerator");
        ChemInfoContainerGenerator generator = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setChemInfoContainerGenerator(generator);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of setLinkConfigs method, of class GeneralIsomersGenerator.
     *
    public void testSetLinkConfigs() {
        System.out.println("setLinkConfigs");
        SingleLinkConfiguration[] configs = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setLinkConfigs(configs);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setHeadMolFile method, of class GeneralIsomersGenerator.
     *
    public void testSetHeadMolFile() {
        System.out.println("setHeadMolFile");
        String headMolFile = "";
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setHeadMolFile(headMolFile);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTotalCarbons method, of class GeneralIsomersGenerator.
     *
    public void testSetTotalCarbons() {
        System.out.println("setTotalCarbons");
        Integer totalCarbons = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setTotalCarbons(totalCarbons);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTotalDoubleBonds method, of class GeneralIsomersGenerator.
     *
    public void testSetTotalDoubleBonds() {
        System.out.println("setTotalDoubleBonds");
        Integer totalDoubleBonds = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setTotalDoubleBonds(totalDoubleBonds);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setThreaded method, of class GeneralIsomersGenerator.
     *
    public void testSetThreaded() {
        System.out.println("setThreaded");
        boolean b = false;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setThreaded(b);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    
    public void testExecute() {
        int carbons = 6;
        int doubleBonds = 1;
        HeadGroup hg = HeadGroup.PC;
        runTestForHeadgroupCarbsAndDBs(hg, carbons, doubleBonds);
    }
    
 
    public void testExecuteForMG1() {
        int carbons = 20;
        int doubleBonds = 2;
        HeadGroup hg = HeadGroup.MG1;
        runTestForHeadgroupCarbsAndDBs(hg, carbons, doubleBonds);
    }
    
    public void testExecuteForGlycerol() {
        int carbons = 16;
        int doubleBonds = 0;
        HeadGroup hg = HeadGroup.Glycerol;
        runTestForHeadgroupCarbsAndDBs(hg, carbons, doubleBonds);
    }
    
    public void testExecuteForBigGlycerol() {
        int carbons = 65;
        int doubleBonds = 10;
        HeadGroup hg = HeadGroup.Glycerol;
        System.out.println("Big glycerol 65:10");
        Long start = System.currentTimeMillis();
        runTestForHeadgroupCarbsAndDBs(hg, carbons, doubleBonds);
        Long elapsed = System.currentTimeMillis() - start;
        System.out.println("Big glycerol 65:10 : "+elapsed);
    }
    
    
    public void testExecuteForPC10_0() {
        int carbons = 10;
        int doubleBonds = 0;
        HeadGroup hg = HeadGroup.PC;
        runTestForHeadgroupCarbsAndDBs(hg, carbons, doubleBonds);
    }
    
    public void testExecuteForLPC() {
        int carbons = 11;
        int doubleBonds = 3;
        HeadGroup hg = HeadGroup.LPC1;
        runTestForHeadgroupCarbsAndDBs(hg, carbons, doubleBonds);
    }

    private void runTestForHeadgroupCarbsAndDBs(HeadGroup hg, int carbons, int doubleBonds) throws LNetMoleculeGeneratorException {
        System.out.println("Test for "+hg.name()+" "+carbons+" carbons and "+doubleBonds+" double bonds");
        ChemInfoContainerGenerator chemInfoContainerGenerator = new ChemInfoContainerGenerator();
        chemInfoContainerGenerator.setUseCachedObjects(Boolean.TRUE);
        chemInfoContainerGenerator.setGenerateInChi(false);
        chemInfoContainerGenerator.setGenerateInChiKey(false);
        chemInfoContainerGenerator.setGenerateInChIAux(false);
        chemInfoContainerGenerator.setGenerateSmiles(false);
        chemInfoContainerGenerator.setGenerateMolFormula(Boolean.TRUE);
        chemInfoContainerGenerator.setGenerateChainInfoContainers(true);
        
        List<BondRule> rules = Arrays.asList(new BondDistance3nPlus2Rule(), new NoDoubleBondsTogetherRule(), new StarterDoubleBondRule(2));
        ChainFactoryGenerator cfGenerator = new ChainFactoryGenerator(rules, new BooleanRBCounterStartSeeder(rules), true);

        GeneralIsomersGenerator generator = new GeneralIsomersGenerator();
        generator.setFirstResultOnly(true);
        generator.setExoticModeOn(true);
        generator.setChainFactoryGenerator(cfGenerator);
        generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        generator.setThreaded(false);
        generator.setHeadGroup(hg);
        generator.setTotalCarbons(carbons);
        generator.setTotalDoubleBonds(doubleBonds);
        generator.setPrintOut(Boolean.TRUE);
        generator.setMaxCarbonsPerSingleChain(30);
        generator.setStepOfChange(1);
        // TODO Check behaviour of TG with stepChange = 1 and exotic mode on.
        

        generator.execute();
        
        Iterator<SubSpecies> subspIt = generator.getSubSpeciesIterator();
        while(subspIt.hasNext()){
            System.out.println(subspIt.next().toString());
        }
        
        SpeciesInfoContainer container = generator.getIsomerInfoContainer();
        assertNotNull(container);
        System.out.println("Mols generated : "+container.getNumOfMolsGenerated());
        assertTrue(container.getNumOfMolsGenerated()>0);
    }

    /**
     * Test of getTotalGeneratedStructs method, of class GeneralIsomersGenerator.
     *
    public void testGetTotalGeneratedStructs() {
        System.out.println("getTotalGeneratedStructs");
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        Integer expResult = null;
        Integer result = instance.getTotalGeneratedStructs();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPrintOut method, of class GeneralIsomersGenerator.
     *
    public void testSetPrintOut() {
        System.out.println("setPrintOut");
        Boolean printOut = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setPrintOut(printOut);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMolFormula method, of class GeneralIsomersGenerator.
     *
    public void testGetMolFormula() {
        System.out.println("getMolFormula");
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        String expResult = "";
        String result = instance.getMolFormula();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMass method, of class GeneralIsomersGenerator.
     *
    public void testGetMass() {
        System.out.println("getMass");
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        Double expResult = null;
        Double result = instance.getMass();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getChainsConfig method, of class GeneralIsomersGenerator.
     *
    public void testGetChainsConfig() {
        System.out.println("getChainsConfig");
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        Set expResult = null;
        Set result = instance.getChainsConfig();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setChainsConfigContainer method, of class GeneralIsomersGenerator.
     *
    public void testSetChainsConfigContainer() {
        System.out.println("setChainsConfigContainer");
        Set<String> chainsConfig = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setChainsConfigContainer(chainsConfig);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setHeadMolStream method, of class GeneralIsomersGenerator.
     *
    public void testSetHeadMolStream() {
        System.out.println("setHeadMolStream");
        InputStream headMolStream = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setHeadMolStream(headMolStream);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMaxCarbonsPerSingleChain method, of class GeneralIsomersGenerator.
     *
    public void testSetMaxCarbonsPerSingleChain() {
        System.out.println("setMaxCarbonsPerSingleChain");
        Integer maxCarbonsPerSingleChain = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setMaxCarbonsPerSingleChain(maxCarbonsPerSingleChain);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStepOfChange method, of class GeneralIsomersGenerator.
     *
    public void testGetStepOfChange() {
        System.out.println("getStepOfChange");
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        Integer expResult = null;
        Integer result = instance.getStepOfChange();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setStepOfChange method, of class GeneralIsomersGenerator.
     *
    public void testSetStepOfChange() {
        System.out.println("setStepOfChange");
        Integer stepOfChange = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setStepOfChange(stepOfChange);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isExoticModeOn method, of class GeneralIsomersGenerator.
     *
    public void testIsExoticModeOn() {
        System.out.println("isExoticModeOn");
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        boolean expResult = false;
        boolean result = instance.isExoticModeOn();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setExoticModeOn method, of class GeneralIsomersGenerator.
     *
    public void testSetExoticModeOn() {
        System.out.println("setExoticModeOn");
        boolean exoticModeOn = false;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setExoticModeOn(exoticModeOn);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setChainFactoryGenerator method, of class GeneralIsomersGenerator.
     *
    public void testSetChainFactoryGenerator() {
        System.out.println("setChainFactoryGenerator");
        ChainFactoryGenerator chainFactoryGenerator = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setChainFactoryGenerator(chainFactoryGenerator);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHeadGroup method, of class GeneralIsomersGenerator.
     *
    public void testGetHeadGroup() {
        System.out.println("getHeadGroup");
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        HeadGroup expResult = null;
        HeadGroup result = instance.getHeadGroup();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setHeadGroup method, of class GeneralIsomersGenerator.
     *
    public void testSetHeadGroup() {
        System.out.println("setHeadGroup");
        HeadGroup headGroup = null;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setHeadGroup(headGroup);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIsomerInfoContainer method, of class GeneralIsomersGenerator.
     *
    public void testGetIsomerInfoContainer() {
        System.out.println("getIsomerInfoContainer");
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        IsomerInfoContainer expResult = null;
        IsomerInfoContainer result = instance.getIsomerInfoContainer();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFirstResultOnly method, of class GeneralIsomersGenerator.
     *
    public void testSetFirstResultOnly() {
        System.out.println("setFirstResultOnly");
        boolean firstResultOnly = false;
        GeneralIsomersGenerator instance = new GeneralIsomersGenerator();
        instance.setFirstResultOnly(firstResultOnly);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/
}
