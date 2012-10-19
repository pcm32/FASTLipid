/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import java.io.InputStream;
import java.util.*;
import uk.ac.ebi.lipidhome.fastlipid.generator.ChainFactoryGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.GeneralIsomersGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.LNetMoleculeGeneratorException;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SpeciesInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.structure.SubSpecies;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondDistance3nPlus2Rule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.NoDoubleBondsTogetherRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.StarterDoubleBondRule;

/**
 *
 * @author pmoreno
 */
public class GeneralIsomersGetterForCarbonsAndDoubleBonds {

    private Integer carbons;
    private Integer doubleBonds;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    private GeneralIsomersGenerator generator;
    private Integer numOfStructs;
    private String formula;
    private Double exactMass;
    private Double naturalMass;
    private Set<String> chainConfigs;
    private Iterator<SubSpecies> subspecies;

    /**
     * TODO move this to a test class.
     * 
     * @param args 
     */
    public static void main(String[] args) {
        Integer carbons = 36;
        
        /**
         * Before setting up chain factories, we need to read the head and decide the number of chains for it.
         * We should avoid having to deal with chainFactories at this level. What is the need for it??
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
                } catch(LNetMoleculeGeneratorException e) {
                    //System.err.println("Not generating for "+i+" carbons, invalid entry. Turn on exotic mode for this.");
                    continue;
                }
                igfcadb.setDoubleBonds(b);
                if(i==32 && b==11) {
                    System.out.println("We are at the weird case!!!");
                }
                igfcadb.exec();
                SpeciesInfoContainer stats = igfcadb.getIsomerStatistics();
                long elapsed = System.currentTimeMillis() - start;
                if(igfcadb.getFormula()!=null)
                    System.out.println(i+"\t"+b+"\t"+igfcadb.getNumOfStructs()+
                        "\t"+igfcadb.getFormula()+"\t"+igfcadb.getExactMass()+
                        "\t"+elapsed);

                if(igfcadb.getNumOfStructs()==0)
                    break;
            }
        }



    }
    private SpeciesInfoContainer speciesStats;
    

    public GeneralIsomersGetterForCarbonsAndDoubleBonds() {
        this.init();
    }

    public GeneralIsomersGetterForCarbonsAndDoubleBonds(SpeciesInfoContainer cont) {
        this.init();
        this.setCarbons(cont.getNumOfCarbons());
        this.setDoubleBonds(cont.getNumOfDoubleBonds());
        this.setHead(cont.getHeadGroup());
        this.setLinkConfigs(cont.getLinkers().toArray(new SingleLinkConfiguration[cont.getLinkers().size()]));
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
        chemInfoContainerGenerator.setGenerateChainInfoContainers(true);

        chainConfigs = new HashSet<String>();
        generator = new GeneralIsomersGenerator();
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
        this.speciesStats = this.generator.getIsomerInfoContainer();
        this.setExactMass(speciesStats.getMass());
        this.setFormula(speciesStats.getMolecularFormula());
        this.setNumOfStructs(speciesStats.getNumOfMolsGenerated());
        
        
        this.subspecies = this.generator.getSubSpeciesIterator();
        
    }
    
    /**
     * Returns an iterator for the different subspecies produced by the underlying generator. Should only be called after
     * invoking {@link #exec() } method.
     * 
     * @return 
     */
    public Iterator<SubSpecies> getSupSpeciesIterator() {
        return this.subspecies;
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
     * @param generator the generator to set
     */
    public void setGenerator(GeneralIsomersGenerator generator) {
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

    /**
     * We would like this to generate a set objects (SubSpecie objects)
     * @return the chainConfigs
     */
    public Set<String> getChainConfigs() {
        return chainConfigs;
    }

    public void setMaxNumberOfCarbonsInSingleChain(Integer maxNumCarbons) {
        this.generator.setMaxCarbonsPerSingleChain(maxNumCarbons);
    }

    public void setExoticMode(boolean exotic) {
        this.generator.setExoticModeOn(exotic);
    }
    
    public void setStepOfChangForNumberOfCarbonsInChains(Integer step) throws LNetMoleculeGeneratorException {
        this.generator.setStepOfChange(step);
    }

    public void setChainFactoryGenerator(ChainFactoryGenerator cfGenerator) {
        this.generator.setChainFactoryGenerator(cfGenerator);
    }

    public void setLinkConfigs(SingleLinkConfiguration... configs) {
        this.generator.setLinkConfigs(configs);
    }

    public SpeciesInfoContainer getIsomerStatistics() {
        return this.speciesStats;
    }

    public void setHead(HeadGroup headGroup) {
        this.generator.setHeadGroup(headGroup);
    }
}

