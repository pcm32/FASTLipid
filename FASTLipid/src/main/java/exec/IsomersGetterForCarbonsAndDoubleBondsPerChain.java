/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package exec;

import java.io.InputStream;
import lnetmoleculegenerator.IsomersGeneratorDefinedFattyAcids;
import structure.ChemInfoContainerGenerator;

/**
 *
 * @author pmoreno
 */
public class IsomersGetterForCarbonsAndDoubleBondsPerChain {

    private Integer carbonsChainA;
    private Integer carbonsChainB;
    private Integer doubleBondsChainA;
    private Integer doubleBondsChainB;
    private String pathToHead;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    private IsomersGeneratorDefinedFattyAcids generator;
    private InputStream headMolStream;

    private Integer numOfStructs;
    private String formula;
    private Double exactMass;
    private Double naturalMass;



    public static void main(String[] args) {
        String pathToHead = args[0];
        Integer carbonsA = Integer.parseInt(args[1]);
        Integer doubleBondsA = Integer.parseInt(args[2]);
        Integer carbonsB = Integer.parseInt(args[3]);
        Integer doubleBondsB = Integer.parseInt(args[4]);

        IsomersGetterForCarbonsAndDoubleBondsPerChain igfcadbpc = new IsomersGetterForCarbonsAndDoubleBondsPerChain();
        igfcadbpc.setCarbonsChainA(carbonsA);
        igfcadbpc.setCarbonsChainB(carbonsB);
        igfcadbpc.setDoubleBondsChainA(doubleBondsA);
        igfcadbpc.setDoubleBondsChainB(doubleBondsB);
        igfcadbpc.setPathToHead(pathToHead);

        igfcadbpc.exec();

        System.out.println("Number of structures:\t"+igfcadbpc.getNumOfStructs());
        System.out.println("Formula:\t"+igfcadbpc.getFormula());
        System.out.println("Mass:\t"+igfcadbpc.getExactMass());
        
    }
    

    public IsomersGetterForCarbonsAndDoubleBondsPerChain() {
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

        setGenerator(new IsomersGeneratorDefinedFattyAcids());
        generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        generator.setThreaded(false);
        generator.setPrintOut(true);
    }

    public void exec() {
        this.generator.run();
        this.setExactMass(this.generator.getExactMass());
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
     * @param carbonsChainA the carbonsChainA to set
     */
    public void setCarbonsChainA(Integer carbonsChainA) {
        this.carbonsChainA = carbonsChainA;
        this.generator.setCarbonsChainA(this.carbonsChainA);
    }

    /**
     * @param carbonsChainB the carbonsChainB to set
     */
    public void setCarbonsChainB(Integer carbonsChainB) {
        this.carbonsChainB = carbonsChainB;
        this.generator.setCarbonsChainB(carbonsChainB);
    }

    /**
     * @param doubleBondsChainA the doubleBondsChainA to set
     */
    public void setDoubleBondsChainA(Integer doubleBondsChainA) {
        this.doubleBondsChainA = doubleBondsChainA;
        this.generator.setDoubleBondsChainA(doubleBondsChainA);
    }

    /**
     * @param doubleBondsChainB the doubleBondsChainB to set
     */
    public void setDoubleBondsChainB(Integer doubleBondsChainB) {
        this.doubleBondsChainB = doubleBondsChainB;
        this.generator.setDoubleBondsChainB(doubleBondsChainB);
    }

    /**
     * @param pathToHead the pathToHead to set
     */
    public void setPathToHead(String pathToHead) {
        this.pathToHead = pathToHead;
        this.generator.setHeadMolFile(this.pathToHead);
    }

    /**
     * @param headMolStream the headMolStream to set
     */
    public void setHeadMolStream(InputStream headMolStream) {
        this.headMolStream = headMolStream;
        this.generator.setHeadMolStream(headMolStream);
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
    public void setGenerator(IsomersGeneratorDefinedFattyAcids generator) {
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


}
