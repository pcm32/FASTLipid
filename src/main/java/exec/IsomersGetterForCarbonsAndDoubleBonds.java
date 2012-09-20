/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exec;

import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lnetmoleculegenerator.IsomersGenerator;
import lnetmoleculegenerator.LNetMoleculeGeneratorException;
import structure.ChainFactory;
import structure.ChemInfoContainerGenerator;
import structure.PooledChainFactory;
import structure.SingleLinkConfiguration;
import structure.rule.BondDistance3nPlus2Rule;
import structure.rule.BondRule;
import structure.rule.NoDoubleBondsTogetherRule;
import structure.rule.StarterDoubleBondRule;

/**
 *
 * @author pmoreno
 */
public class IsomersGetterForCarbonsAndDoubleBonds {

    private Integer carbons;
    private Integer doubleBonds;
    private String pathToHead;
    private InputStream headMolStream;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    private IsomersGenerator generator;
    private Integer numOfStructs;
    private String formula;
    private Double exactMass;
    private Double naturalMass;
    private Set<String> chainConfigs;

    public static void main(String[] args) {
        String pathToHead = "";// args[0];
        Integer carbons = 36;//Integer.parseInt(args[1]);
        Integer doubleBonds = 0;//Integer.parseInt(args[2]);

        ChainFactory cfA = new PooledChainFactory();
        ChainFactory cfB = new PooledChainFactory();
        //ChainFactory cfA = new ChainFactory();
        //ChainFactory cfB = new ChainFactory();

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

        System.out.println("C\tDB\tNumStruc\tMolForm\tMass\tTime");
        //for (int i = 4; i<16; i++) {
        for (int i = carbons - 10; i < carbons + 10; i++) {
            for (int b = 0; b < carbons / 2; b++) {
                //igfcadb.setCarbons(i);
                //int i=0;
                //int b=0;
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
                long elapsed = System.currentTimeMillis() - start;
                if (igfcadb.getFormula() != null) {
                    System.out.println(i + "\t" + b + "\t" + igfcadb.getNumOfStructs()
                            + "\t" + igfcadb.getFormula() + "\t" + igfcadb.getExactMass()
                            + "\t" + elapsed);
                }
                Set<String> chainConfigs = igfcadb.getChainConfigs();
                //for (String config : chainConfigs) {
                //    System.out.println("Possible: "+config);
                //}
                if (igfcadb.getNumOfStructs() == 0) {
                    break;
                }
                //System.out.println("Carbons & DB:"+i+"\t"+b);
                //System.out.println("Number of structures:\t" + igfcadb.getNumOfStructs());
                //System.out.println("Formula:\t" + igfcadb.getFormula());
                //System.out.println("Mass:\t" + igfcadb.getExactMass());
            }
        }



    }
    private SingleLinkConfiguration linkConfR1;
    private SingleLinkConfiguration linkConfR2;

    public IsomersGetterForCarbonsAndDoubleBonds() {
        this.init();
    }

    private void init() {
        chemInfoContainerGenerator = new ChemInfoContainerGenerator();
        chemInfoContainerGenerator.setGenerateInChi(false);
        chemInfoContainerGenerator.setGenerateInChiKey(false);
        chemInfoContainerGenerator.setGenerateInChIAux(false);
        chemInfoContainerGenerator.setGenerateMolFormula(true);
        chemInfoContainerGenerator.setGenerateMass(true);
        chemInfoContainerGenerator.setGenerateSmiles(false);
        chemInfoContainerGenerator.setUseCachedObjects(true);

        chainConfigs = new HashSet<String>();
        generator = new IsomersGenerator();
        generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        generator.setThreaded(false);
        generator.setPrintOut(false);
        generator.setChainsConfigContainer(chainConfigs);

    }

    public void reset() {
        this.init();
    }

    public void exec() {
        chainConfigs.clear();
        this.generator.execute();
        this.setExactMass(this.generator.getMass());
        this.setFormula(this.generator.getMolFormula());
        this.setNumOfStructs(this.generator.getTotalGeneratedStructs());
    }

    public void configForSmilesOutput() {
        this.chemInfoContainerGenerator.setGenerateSmiles(true);
        this.chemInfoContainerGenerator.setGenerateMass(false);
        this.chemInfoContainerGenerator.setGenerateMolFormula(false);
        this.chemInfoContainerGenerator.setGenerateInChi(false);
        this.chemInfoContainerGenerator.setGenerateInChIAux(false);
        this.chemInfoContainerGenerator.setGenerateInChiKey(false);
        this.generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        this.generator.setPrintOut(true);
    }

    public void configForMassAndFormula() {
        this.chemInfoContainerGenerator.setGenerateSmiles(false);
        this.chemInfoContainerGenerator.setGenerateMass(true);
        this.chemInfoContainerGenerator.setGenerateMolFormula(true);
        this.generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        this.generator.setPrintOut(false);
    }

    /**
     * @param carbons the carbons to set
     */
    public void setCarbons(Integer carbons) throws LNetMoleculeGeneratorException {
        this.carbons = carbons;
        this.generator.setTotalCarbons(this.carbons);
    }

    /**
     * @param doubleBonds the doubleBonds to set
     */
    public void setDoubleBonds(Integer doubleBonds) {
        this.doubleBonds = doubleBonds;
        this.generator.setTotalDoubleBonds(this.doubleBonds);
    }

    /**
     * @param pathToHead the pathToHead to set
     */
    public void setPathToHead(String pathToHead) {
        this.pathToHead = pathToHead;
        this.generator.setHeadMolFile(this.pathToHead);
    }

    /**
     * @param generator the generator to set
     */
    public void setGenerator(IsomersGenerator generator) {
        this.generator = generator;
    }

    /**
     * @return the numOfStructs
     */
    public Integer getNumOfStructs() {
        return numOfStructs;
    }

    /**
     * @param numOfStructs the numOfStructs to set
     */
    public void setNumOfStructs(Integer numOfStructs) {
        this.numOfStructs = numOfStructs;
    }

    /**
     * @return the formula
     */
    public String getFormula() {
        return formula;
    }

    /**
     * @param formula the formula to set
     */
    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     * @return the mass
     */
    public Double getExactMass() {
        return exactMass;
    }

    /**
     * @param exactMass the mass to set
     */
    public void setExactMass(Double exactMass) {
        this.exactMass = exactMass;
    }

    /**
     * @return the mass
     */
    public Double getNaturalMass() {
        return exactMass;
    }

    /**
     * @param exactMass the mass to set
     */
    public void setNaturalMass(Double exactMass) {
        this.exactMass = exactMass;
    }

    public void setChainFactories(ChainFactory cfA, ChainFactory cfB) {
        this.generator.setCfA(cfA);
        this.generator.setCfB(cfB);
    }

    /**
     * We would like this to generate a set objects (SubSpecie objects)
     *
     * @return the chainConfigs
     */
    public Set<String> getChainConfigs() {
        return chainConfigs;
    }

    /**
     * @param headMolStream the headMolStream to set
     */
    public void setHeadMolStream(InputStream headMolStream) {
        this.headMolStream = headMolStream;
        this.generator.setHeadMolStream(headMolStream);
    }

    public void setMaxNumberOfCarbonsInSingleChain(Integer maxNumCarbons) {
        this.generator.setMaxCarbonsPerSingleChain(maxNumCarbons);
    }

    public void setLinkConfigsR1R2(SingleLinkConfiguration confR1, SingleLinkConfiguration confR2) {
        this.generator.setLinkConfigsR1R2(confR1, confR2);
    }

    public void setExoticMode(boolean exotic) {
        this.generator.setExoticModeOn(exotic);
    }

    public void setStepOfChangForNumberOfCarbonsInChains(Integer step) throws LNetMoleculeGeneratorException {
        this.generator.setStepOfChange(step);
    }
}
