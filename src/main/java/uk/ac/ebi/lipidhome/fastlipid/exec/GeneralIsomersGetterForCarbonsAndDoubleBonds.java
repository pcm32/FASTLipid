/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import java.util.*;
import uk.ac.ebi.lipidhome.fastlipid.generator.ChainFactoryGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.GeneralIsomersGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.LNetMoleculeGeneratorException;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SpeciesInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.structure.SubSpecies;

/**
 * Enumerates lipids for a particular configuration of {@link HeadGroup}, {@link SingleLinkConfiguration} linkers, 
 * total number of carbons (in the fatty acids), and total number of double bonds (in the fatty acids).
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

    private SpeciesInfoContainer speciesStats;
    

    /**
     * Default initialization constructor. Settings must be supplied later through the setter methods.
     */
    public GeneralIsomersGetterForCarbonsAndDoubleBonds() {
        this.init();
    }

    /**
     * Initialize with settings based on the given {@link SpeciesInfoContainer}.
     * 
     * @param cont container with the required settings for the enumeration.
     */
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

    /**
     * Resets the {@link ChemInfoContainerGenerator} object.
     * 
     */
    public void reset() {
        this.init();
    }
    
    /**
     * Sets the inner generator to stop the generation of molecules once the first molecule has been generated. Is useful
     * for checking whether a particular head-linkers-carbons-double bonds has any feasible molecules. Needs to be called
     * before {@link #exec() }.
     * 
     * @param firstOnly 
     */
    public void setFirstResultOnly(boolean firstOnly) {
        this.generator.setFirstResultOnly(firstOnly);
    }

    /**
     * Executes the underlying generator with the specified settings.
     */
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

    /**
     * Configures the ChemInfoContainer to produce SMILES only in the ChemInfoContainer retrieved in the end.
     */
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

    /**
     * Configures the ChemInfoContainerGenerator to produce SMILES, Mass, and Molecular Formula in the ChemInfoContainer 
     * retrieved in the end.
     * 
     */
    public void configForMassAndFormula() {
        this.chemInfoContainerGenerator.setGenerateSmiles(false);
        this.chemInfoContainerGenerator.setGenerateMass(true);
        this.chemInfoContainerGenerator.setGenerateMolFormula(true);
        this.generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        this.generator.setPrintOut(false);
    }

    /**
     * Sets the total number of carbons to use (for all the fatty acids).
     * 
     * @param carbons the # of carbons to distribute.
     */
    public void setCarbons(Integer carbons) throws LNetMoleculeGeneratorException {
        this.carbons = carbons;
        this.generator.setTotalCarbons(this.carbons);
    }

    /**
     * Sets the total number of double bonds in all fatty acid chains to use.
     * 
     * @param doubleBonds the doubleBonds to set
     */
    public void setDoubleBonds(Integer doubleBonds) {
        this.doubleBonds = doubleBonds;
        this.generator.setTotalDoubleBonds(this.doubleBonds);
    }

    /**
     * Sets the underlying {@link GeneralIsomersGenerator} generator which enumerates molecules.
     * 
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
     * @deprecated use {@link SpeciesInfoContainer} instead. The formula no longer needs to be obtained from this object.
     * 
     * @return the formula
     */
    @Deprecated
    public String getFormula() {
        return formula;
    }

    /**
     * @deprecated use {@link SpeciesInfoContainer} instead. The formula no longer needs to be set in this object.
     * 
     * @param formula the formula to set
     */
    @Deprecated
    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     * @deprecated use {@link 
     * @return the mass
     */
    public Double getExactMass() {
        return exactMass;
    }

    /**
     * @deprecated use {@link SpeciesInfoContainer} instead. The exact mass no longer needs to be set in this object.
     * 
     * @param exactMass the mass to set
     */
    @Deprecated
    public void setExactMass(Double exactMass) {
        this.exactMass = exactMass;
    }

    /**
     * @deprecated use {@link SpeciesInfoContainer} instead. The natural mass no longer should be part of this generator.
     * @return the mass
     */
    @Deprecated
    public Double getNaturalMass() {
        return exactMass;
    }

    /**
     * @deprecated use {@link SpeciesInfoContainer} instead. The natural mass no longer needs to be set in this object.
     * @param exactMass the mass to set
     */
    @Deprecated
    public void setNaturalMass(Double exactMass) {
        this.exactMass = exactMass;
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
     * Sets the maximum number of carbons that can be accepted in a single fatty acid chain.
     * 
     * @param maxNumCarbons 
     */
    public void setMaxNumberOfCarbonsInSingleChain(Integer maxNumCarbons) {
        this.generator.setMaxCarbonsPerSingleChain(maxNumCarbons);
    }

    /**
     * If true, the exotic mode is on, and fatty acids of odd lengths are allowed. If this option is not set and odd
     * lengths are enforced, a runtime exception is thrown by some methods.
     * 
     * @param exotic 
     */
    public void setExoticMode(boolean exotic) {
        this.generator.setExoticModeOn(exotic);
    }
    
    /**
     * Sets the step used to increment/decrement the number of carbons on each of the fatty acid chains. It should be a
     * positive integer.
     * 
     * @param step to change number of carbons (either up or down) on each iteration.
     */
    public void setStepOfChangForNumberOfCarbonsInChains(Integer step) {
        this.generator.setStepOfChange(step);
    }

    /**
     * Sets the ChainFactoryGenerator that the underlying GeneralIsomersGenerator will use.
     * 
     * @param cfGenerator 
     */
    public void setChainFactoryGenerator(ChainFactoryGenerator cfGenerator) {
        this.generator.setChainFactoryGenerator(cfGenerator);
    }

    /**
     * Sets the links configuration to be used, in that order, with the respective fatty acid chains. The given array or
     * enumerated array should be of the same length as the amount of fatty acids.
     * 
     * @param configs to be used, for each fatty acid.
     */
    public void setLinkConfigs(SingleLinkConfiguration... configs) {
        this.generator.setLinkConfigs(configs);
    }

    /**
     * The Species, according to the LipidHome nomenclature, stands for:
     * Head + Linkers (any order) + Total # carbons in FAs + Total # double bonds in FAs
     * 
     * The {@link SpeciesInfoContainer} contains this data.
     * 
     * @return the {@link SpeciesInfoContainer} containing isomer configuration data. 
     */
    public SpeciesInfoContainer getIsomerStatistics() {
        return this.speciesStats;
    }

    /**
     * Sets the head group to be used by the underlying generator.
     * 
     * @param headGroup head to use.
     */
    public void setHead(HeadGroup headGroup) {
        this.generator.setHeadGroup(headGroup);
    }
}

