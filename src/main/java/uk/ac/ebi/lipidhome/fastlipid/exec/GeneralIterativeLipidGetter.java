/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import uk.ac.ebi.lipidhome.fastlipid.generator.ChainFactoryGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.GeneralIsomersGeneratorDefinedFattyAcids;
import uk.ac.ebi.lipidhome.fastlipid.generator.LNetMoleculeGeneratorException;
import uk.ac.ebi.lipidhome.fastlipid.structure.*;

/**
 * An iterative generator of phospholipids with defined carbons and double bonds per chain.
 *
 * @author pmoreno
 */
public class GeneralIterativeLipidGetter {

    private List<Integer> carbonsPerChain;
    private List<Integer> doubleBondsPerChain;
    private HeadGroup hg;

    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    private GeneralIsomersGeneratorDefinedFattyAcids generator;
    private Integer numOfStructs;
    private String formula;
    private Double mass;
    private Set<String> chainConfigs;
    private Boolean generateChainInfoContainer;
    

    public GeneralIterativeLipidGetter(ChainFactoryGenerator cfg) {
        this.init();
        this.generator.setChainFactoryGenerator(cfg);
    }
    
    /**
     * Starts an iterative phospho lipid getter from a subspecie definition (that is, a definition of header, linkers,
     * and number of carbons and double bonds per fatty acid). The ChainFactoryGenerator provides the logic rules to generate
     * the fatty acids.
     * 
     * @param subSpecies
     * @param cfg 
     */
    public GeneralIterativeLipidGetter(SubSpecies subSpecies, ChainFactoryGenerator cfg) {
        this.init();
        List<FattyAcidSpecies> fas = subSpecies.getFattyAcids();
        Integer[] carbons = new Integer[fas.size()];
        Integer[] dbBonds = new Integer[fas.size()];
        for (int i = 0; i < carbons.length; i++) {
            carbons[i] = fas.get(i).getCarbonCount();
            dbBonds[i] = fas.get(i).getDoubleBondsCount();
        }
        
        this.setCarbonsPerChainP(carbons);
        this.setDoubleBondsPerChainsP(dbBonds);
        this.setHeadGroup(subSpecies.getHeadGroup());
        this.setLinkConfigs(subSpecies.getLinkages().toArray(new SingleLinkConfiguration[subSpecies.getLinkages().size()]));
        this.generator.setChainFactoryGenerator(cfg);
    }

    private void init() {
        this.carbonsPerChain = new ArrayList<Integer>();
        this.doubleBondsPerChain = new ArrayList<Integer>();
        chemInfoContainerGenerator = new ChemInfoContainerGenerator();
        chemInfoContainerGenerator.setGenerateInChi(true);
        chemInfoContainerGenerator.setGenerateInChiKey(false);
        chemInfoContainerGenerator.setGenerateInChIAux(false);
        chemInfoContainerGenerator.setGenerateMolFormula(true);
        chemInfoContainerGenerator.setGenerateMass(true);
        chemInfoContainerGenerator.setGenerateSmiles(false);
        chemInfoContainerGenerator.setUseCachedObjects(true);
        chemInfoContainerGenerator.setGenerateChainInfoContainers(false);
        
        generateChainInfoContainer = false;

        setGenerator(new GeneralIsomersGeneratorDefinedFattyAcids());
        generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        generator.setThreaded(false);
        generator.setPrintOut(false);
    }
    
    /**
     * Sets the exotic mode on, which allows to use odd numbers of carbons. This should be invoked before setting the
     * number of carbons.
     * 
     * @param exotic true if the generator should be set to exotic mode. 
     */
    public void setExoticModeOn(boolean exotic) {
        this.generator.setExoticModeOn(true);
    }
    
    /**
     * Sets the number of carbons that should be added/removed to a fatty acid each time its size is modified.
     * 
     * @param carbonStepSize the number of carbons to add or remove at each size changing iteration. 
     *
    public void setCarbonStepSize(Integer carbonStepSize) {
        this.generator.setStepOfChange(carbonStepSize);
    }*/

    public void run() {
        chemInfoContainerGenerator.setGenerateChainInfoContainers(generateChainInfoContainer);
        this.generator.setIterableMode(true);
        this.generator.executeInSeparateThread();
    }

    public ChemInfoContainer nextChemInfoContainer() throws LNetMoleculeGeneratorException {
        return this.generator.getNext();
    }

    public void configForSmilesOutput() {
        this.chemInfoContainerGenerator.setGenerateSmiles(true);
        this.chemInfoContainerGenerator.setGenerateMass(true);
        this.chemInfoContainerGenerator.setGenerateMolFormula(false);
        this.chemInfoContainerGenerator.setGenerateInChi(false);
        this.chemInfoContainerGenerator.setGenerateInChIAux(false);
        this.chemInfoContainerGenerator.setGenerateInChiKey(false);
        this.chemInfoContainerGenerator.setGenerateChainInfoContainers(true);
        this.generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        this.generator.setPrintOut(false);
    }

    public void configForMassAndFormula() {
        this.chemInfoContainerGenerator.setGenerateSmiles(false);
        this.chemInfoContainerGenerator.setGenerateMass(true);
        this.chemInfoContainerGenerator.setGenerateMolFormula(true);
        this.generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        this.generator.setPrintOut(false);
    }

    /**
     * Sets the number of carbons for each chain, as in the order provided. Note that no check is done to enforce
     * that this number of chains makes sense with the header. TODO: check this.
     * 
     * @param carbonsChainA the carbonsChainA to set
     */
    public void setCarbonsPerChains(Integer... carbonsChains) {
        setCarbonsPerChainP(carbonsChains);
    }

    /**
     * Private method to avoid calling an overridable method in the constructor. 
     * @param carbonsChains 
     */
    private void setCarbonsPerChainP(Integer... carbonsChains) {
        this.carbonsPerChain.clear();
        this.carbonsPerChain.addAll(Arrays.asList(carbonsChains));
        this.generator.setCarbonsPerChain(carbonsChains);
    }

    /**
     * Sets the number of double bonds for each chain, as in the order provided. Note that no check is done to enforce
     * that this number of chains makes sense with the header. TODO: check this.
     * 
     * @param carbonsChainA the carbonsChainA to set
     */
    public void setDoubleBondsPerChains(Integer... doubleBonds) {
        setDoubleBondsPerChainsP(doubleBonds);
    }

    /**
     * Private method to avoid calling {@link  #setDoubleBondsPerChains(java.lang.Integer[])} from one of the constructors.
     * 
     * @param doubleBonds 
     */
    private void setDoubleBondsPerChainsP(Integer... doubleBonds) {
        this.doubleBondsPerChain.clear();
        this.doubleBondsPerChain.addAll(Arrays.asList(doubleBonds));
        this.generator.setDoubleBondsPerChain(doubleBonds);
    }
    

    /**
     * @param chemInfoContainerGenerator the chemInfoContainerGenerator to set
     */
    public void setChemInfoContainerGenerator(ChemInfoContainerGenerator chemInfoContainerGenerator) {
        this.chemInfoContainerGenerator = chemInfoContainerGenerator;
        this.generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
    }

    /**
     * @param generator the generator to set
     */
    public void setGenerator(GeneralIsomersGeneratorDefinedFattyAcids generator) {
        this.generator = generator;
    }
    
    public void setGenerateChainInfoContainer(boolean generateChainInfoContainer) {
        this.generateChainInfoContainer = generateChainInfoContainer;
    }

    public int getNumOfTotalStructures() {
        return this.generator.getTotalGeneratedStructs();
    }
    
    /**
     * Retrieves the exact mass for the generated lipids of defined carbons and double bonds. This should only be executed
     * once the first ChemInfoContainer has been retrieved with {@link #nextChemInfoContainer() }. The exact mass is calculated
     * using the most abundant isotope for each atom. Is the desired mass to use in mass spectrometry.
     * 
     * @return the exact mass. 
     */
    public Double getExactMass() {
        return this.generator.getExactMass();
    }
    
    /**
     * Sets the head group to be used.
     * @param hg 
     */
    public void setHeadGroup(HeadGroup hg) {
        this.hg = hg;
        this.generator.setHeadGroup(hg);
    }

    /**
     * Sets the configuration for the linkers (part that binds head group to fatty acids), in the order specified.
     * 
     * @param linkers 
     */
    public void setLinkConfigs(SingleLinkConfiguration... linkers) {
        this.generator.setLinkConfigs(linkers);
    }

    /**
     * Retrieves the natural mass for the generated lipids of defined carbons and double bonds. This should only be executed
     * once the first ChemInfoContainer has been retrieved with {@link #nextChemInfoContainer() }. The natural mass is
     * calculated from the weighted averages based abundances of each of the isotopes for each of the atoms. Is what would
     * be obtained out of measuring a solid of this material in a balance (and then dividing by the mols of molecules).
     * 
     * @return the natural mass. 
     */
    public Double getNaturalMass() {
        return this.generator.getNaturalMass();
    }

}
