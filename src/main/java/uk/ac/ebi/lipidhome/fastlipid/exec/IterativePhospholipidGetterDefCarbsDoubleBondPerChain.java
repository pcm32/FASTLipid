/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import java.io.InputStream;
import java.util.Set;
import uk.ac.ebi.lipidhome.fastlipid.generator.IsomersGeneratorDefinedFattyAcids;
import uk.ac.ebi.lipidhome.fastlipid.generator.LNetMoleculeGeneratorException;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChainInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;

/**
 * An iterative generator of phospholipids with defined carbons and double bonds per chain.
 *
 * @author pmoreno
 */
public class IterativePhospholipidGetterDefCarbsDoubleBondPerChain {

    private Integer carbonsChainA;
    private Integer carbonsChainB;
    private Integer doubleBondsChainA;
    private Integer doubleBondsChainB;
    private String pathToHead;
    private InputStream headMolStream;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    private IsomersGeneratorDefinedFattyAcids generator;
    private Integer numOfStructs;
    private String formula;
    private Double mass;
    private Set<String> chainConfigs;
    private Boolean generateChainInfoContainer;
    

    public IterativePhospholipidGetterDefCarbsDoubleBondPerChain() {
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
        chemInfoContainerGenerator.setGenerateChainInfoContainers(false);
        
        generateChainInfoContainer = false;

        setGenerator(new IsomersGeneratorDefinedFattyAcids());
        generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        generator.setThreaded(false);
        generator.setPrintOut(false);
    }

    public void run() {
        chemInfoContainerGenerator.setGenerateChainInfoContainers(generateChainInfoContainer);
        this.generator.setIterableMode(true);
        this.generator.executeInSeparateThread();
        //this.setNaturalMass(this.generator.getNaturalMass());
        //this.setFormula(this.generator.getMolFormula());
        //this.setNumOfStructs(this.generator.getTotalGeneratedStructs());
    }

    public ChemInfoContainer nextChemInfoContainer() throws LNetMoleculeGeneratorException {
        return this.generator.getNext();
    }

    public void configForSmilesOutput() {
        this.chemInfoContainerGenerator.setGenerateSmiles(true);
        this.chemInfoContainerGenerator.setGenerateMass(false);
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
    
    public void setGenerateChainInfoContainer(boolean generateChainInfoContainer) {
        this.generateChainInfoContainer = generateChainInfoContainer;
    }

    public int getNumOfTotalStructures() {
        return this.generator.getTotalGeneratedStructs();
    }

    public static void main(String[] args) throws LNetMoleculeGeneratorException {
        int carbonsA=12;
        int carbonsB=14;
        int dbA=3;
        int dbB=2;

        IterativePhospholipidGetterDefCarbsDoubleBondPerChain ipgdcdbpc = new IterativePhospholipidGetterDefCarbsDoubleBondPerChain();
        ipgdcdbpc.setCarbonsChainA(carbonsA);
        ipgdcdbpc.setCarbonsChainB(carbonsB);
        ipgdcdbpc.setDoubleBondsChainA(dbA);
        ipgdcdbpc.setDoubleBondsChainB(dbB);
        ipgdcdbpc.setHeadMolStream(IterativePhospholipidGetterDefCarbsDoubleBondPerChain.class.getResourceAsStream("/structures/models/PC.mol"));
        //ipgdcdbpc.setPathToHead("/Users/pmoreno/NetBeansProjects/LNetMoleculeGenerator/src/structures/models/PC.mol");
        ipgdcdbpc.configForSmilesOutput();
        ipgdcdbpc.setGenerateChainInfoContainer(true);
        ipgdcdbpc.setLinkConfigsR1R2(SingleLinkConfiguration.Acyl, SingleLinkConfiguration.Acyl);
        ipgdcdbpc.run();

        ChemInfoContainer res = ipgdcdbpc.nextChemInfoContainer();

        while(res!=null) {
            System.out.println("Smiles:"+res.getSmiles());
            int count=1;
            for (ChainInfoContainer chainInfo : res.getChainsInfo()) {
                System.out.println("Chain "+count+": Double bonds in pos:");
                count++;
                for (Integer integer : chainInfo.getDoubleBondPositions()) {
                    System.out.print(integer+" ");
                }
                System.out.println();
            }
            System.out.println("Exact mass:"+res.getExactMass());
            System.out.println("Natural mass:"+res.getNaturalMass());
            res = ipgdcdbpc.nextChemInfoContainer();
        }

        System.out.println("Total:"+ipgdcdbpc.getNumOfTotalStructures());



    }

    public void setLinkConfigsR1R2(SingleLinkConfiguration confR1, SingleLinkConfiguration confR2) {
        this.generator.setLinkConfigsR1R2(confR1, confR2);
    }

}
