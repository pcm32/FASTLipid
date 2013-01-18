/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.generator;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondDistance3nPlus2Rule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.NoDoubleBondsTogetherRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.StarterDoubleBondRule;

/**
 *
 * @author pmoreno
 */
public class GeneralIsomersGeneratorDefinedFattyAcidsTest extends TestCase {
    
    public GeneralIsomersGeneratorDefinedFattyAcidsTest(String testName) {
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

    public void testJoeExecPlan(){
       List<BondRule> rules = new ArrayList<BondRule>();
       rules.add(new BondDistance3nPlus2Rule());
       rules.add(new NoDoubleBondsTogetherRule());
       rules.add(new StarterDoubleBondRule(2));
       ChainFactoryGenerator cfGenerator = new ChainFactoryGenerator(rules, new BooleanRBCounterStartSeeder(rules), true);
       GeneralIsomersGeneratorDefinedFattyAcids generator = new GeneralIsomersGeneratorDefinedFattyAcids();
       generator.setHeadGroup(HeadGroup.PC);
       List<Integer> cList = new ArrayList<Integer>();
       List<Integer> dbList = new ArrayList<Integer>();
       List<SingleLinkConfiguration> linkers = new ArrayList<SingleLinkConfiguration>();
       cList.add(18);
       cList.add(16);
       dbList.add(0);
       dbList.add(2);
       linkers.add(SingleLinkConfiguration.Acyl);
       linkers.add(SingleLinkConfiguration.Acyl);

       ChemInfoContainerGenerator chemInfoContainerGenerator = new ChemInfoContainerGenerator();
       chemInfoContainerGenerator.setGenerateSmiles(true);
       chemInfoContainerGenerator.setGenerateChainInfoContainers(true);
       
       generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
       generator.setIterableMode(true);
       generator.setThreaded(false);
       generator.setCarbonsPerChain(cList);
       generator.setDoubleBondsPerChain(dbList);
       generator.setLinkConfigs(linkers.toArray(new SingleLinkConfiguration[linkers.size()]));
       generator.setExoticModeOn(true);
       generator.setChainFactoryGenerator(cfGenerator);
       generator.run();


       ChemInfoContainer res = generator.getNext();
       System.out.println("here");

       while(res!=null) {
           System.out.println((res.getChainsInfo()+" " + res.getSmiles()));
           res = generator.getNext();
       }
    }
}
