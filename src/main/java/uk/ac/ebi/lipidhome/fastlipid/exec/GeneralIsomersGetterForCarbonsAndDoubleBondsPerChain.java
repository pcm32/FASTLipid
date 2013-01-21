/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.exec;

import java.util.Arrays;
import java.util.List;
import uk.ac.ebi.lipidhome.fastlipid.generator.GeneralIsomersGeneratorDefinedFattyAcids;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;

/**
 * Enumerates lipids for a particular configuration of {@link HeadGroup}, {@link SingleLinkConfiguration} linkers, 
 * number of carbons per fatty acid chain, and number of double bonds per fatty acid chain.
 *
 * @author pmoreno
 */
public class GeneralIsomersGetterForCarbonsAndDoubleBondsPerChain {

    private List<Integer> carbonsPerChain;
    private List<Integer> doubleBondsPerChain;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    private GeneralIsomersGeneratorDefinedFattyAcids generator;
    private HeadGroup hg;

    private Integer numOfStructs;
    private String formula;
    private Double exactMass;
    private Double naturalMass;


/*
    public static void main(String[] args) {
        HeadGroup hg = HeadGroup.valueOf(args[0]);
        Integer carbonsA = Integer.parseInt(args[1]);
        Integer doubleBondsA = Integer.parseInt(args[2]);
        Integer carbonsB = Integer.parseInt(args[3]);
        Integer doubleBondsB = Integer.parseInt(args[4]);

        GeneralIsomersGetterForCarbonsAndDoubleBondsPerChain igfcadbpc = new GeneralIsomersGetterForCarbonsAndDoubleBondsPerChain();
        igfcadbpc.setCarbonsPerChains(carbonsA, carbonsB);
        igfcadbpc.setDoubleBondsPerChains(doubleBondsA, doubleBondsB);
        
        igfcadbpc.setHeadGroup(hg);

        igfcadbpc.exec();

        System.out.println("Number of structures:\t"+igfcadbpc.getNumOfStructs());
        System.out.println("Formula:\t"+igfcadbpc.getFormula());
        System.out.println("Mass:\t"+igfcadbpc.getExactMass());
        
    }
  */  

    /**
     * Default constructor. Sets the {@link ChemInfoContainerGenerator} to produce InChIs, Molecular Formula, Mass, and
     * use cached objects.
     */
    public GeneralIsomersGetterForCarbonsAndDoubleBondsPerChain() {
        this.init();
    }

    private void init() {
        setChemInfoContainerGenerator(new ChemInfoContainerGenerator());
        chemInfoContainerGenerator.setGenerateInChi(true);
        chemInfoContainerGenerator.setGenerateInChiKey(false);
        chemInfoContainerGenerator.setGenerateInChIAux(false);
        chemInfoContainerGenerator.setGenerateMolFormula(true);
        chemInfoContainerGenerator.setGenerateMass(true);
        chemInfoContainerGenerator.setGenerateSmiles(false);
        chemInfoContainerGenerator.setUseCachedObjects(true);

        setGenerator(new GeneralIsomersGeneratorDefinedFattyAcids());
        generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        generator.setThreaded(false);
        generator.setPrintOut(true);
    }

    /**
     * Once configurations have been set, this method should be called to run the generation on the same thread.
     * 
     */
    public void exec() {
        this.generator.run();
        this.setExactMass(this.generator.getExactMass());
        this.setFormula(this.generator.getMolFormula());
        this.setNumOfStructs(this.generator.getTotalGeneratedStructs());
    }

    /**
     * Configures the ChemInfoContainer to produce SMILES only in the ChemInfoContainer retrieved in the end.
     * 
     * TODO this method should be part of an abstract class, like AbstractIsomerGetter
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
     * Configures the ChemInfoContainerGenerator to produce Mass, and Molecular Formula in the ChemInfoContainer 
     * retrieved in the end.
     * 
     * TODO this method should be part of an abstract class, like AbstractIsomerGetter
     */
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
        this.doubleBondsPerChain.clear();
        this.doubleBondsPerChain.addAll(Arrays.asList(doubleBonds));
        this.generator.setDoubleBondsPerChain(doubleBonds);
    }

    /**
     * @param chemInfoContainerGenerator the chemInfoContainerGenerator to set
     */
    public void setChemInfoContainerGenerator(ChemInfoContainerGenerator chemInfoContainerGenerator) {
        this.chemInfoContainerGenerator = chemInfoContainerGenerator;
    }

    /**
     * @param generator the generator to set
     */
    public void setGenerator(GeneralIsomersGeneratorDefinedFattyAcids generator) {
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
     * Returns the mass based on the most common (most abundant in nature) isotopes of the atoms in the lipid.
     * 
     * @return the exact mass
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
     * Returns the mass based on the weighted average of isotope masses of the atoms in the lipid.
     * 
     * @return the exact mass
     */
    public Double getNaturalMass() {
        return naturalMass;
    }

    /**
     * @param naturalMass the mass to set
     */
    public void setNaturalMass(Double naturalMass) {
        this.naturalMass = naturalMass;
    }

    /**
     * Sets the head group to be used.
     * @param hg 
     */
    private void setHeadGroup(HeadGroup hg) {
        this.hg = hg;
        this.generator.setHeadGroup(hg);
    }

    


}
