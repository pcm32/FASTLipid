/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.lnetmoleculegenerator;

import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import structure.*;
import structure.rule.BondDistance3nPlus2Rule;
import structure.rule.BondRule;
import structure.rule.NoDoubleBondsTogetherRule;
import structure.rule.StarterDoubleBondRule;
import uk.ac.ebi.lipidhome.fastlipid.util.GenericAtomDetector;

/**
 *
 * @author pmoreno
 */
public class GeneralIsomersGenerator {

    private String headMolFile;
    private InputStream headMolStream;
    private HeadGroup headGroup;
    private Integer totalCarbons;
    private Integer totalDoubleBonds;
    private Boolean threaded = false;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    private Integer totalGeneratedStructs;
    private Boolean printOut;
    private String molFormula;
    private Double exactMass;
    private Double naturalMass;
    private List<ChainFactory> chainFactories;
    private IAtomContainer originalMol = null;
    private Set<String> chainsConfig;
    private Integer maxCarbonsPerSingleChain = 0;
    private Integer stepOfChange;
    private List<SingleLinkConfiguration> linkConfigs;
    private boolean exoticModeOn = false;
    private boolean firstResultOnly = false;
    
    private ChainFactoryGenerator chainFactoryGenerator;
    

    public void setChemInfoContainerGenerator(ChemInfoContainerGenerator generator) {
        this.chemInfoContainerGenerator = generator;
    }

    /**
     * This method sets the linkage to be used in each of the positions. 
     * 
     * @param configs 
     */
    public void setLinkConfigs(SingleLinkConfiguration... configs) {
        this.linkConfigs = Arrays.asList(configs);
    }

    /**
     * Sets the head molecule file by receiving an MDL Mol file path.
     * 
     * @deprecated use {@link #setHeadGroup(structure.HeadGroup) } instead.
     * 
     * @param headMolFile the path to the MDL Mol file.
     */
    @Deprecated
    public void setHeadMolFile(String headMolFile) {
        this.headMolFile = headMolFile;
        this.originalMol = null;
    }

    public void setTotalCarbons(Integer totalCarbons) throws LNetMoleculeGeneratorException {
        if (!this.exoticModeOn && (totalCarbons % 2 != 0)) {
            throw new LNetMoleculeGeneratorException("Only pair number of carbons"
                    + " can be used if not in the exotic mode.");
        }
        this.totalCarbons = totalCarbons;
        this.chemInfoContainerGenerator.setGenerateMass(true);
        this.chemInfoContainerGenerator.setGenerateMolFormula(true);
    }

    public void setTotalDoubleBonds(Integer totalDoubleBonds) {
        this.totalDoubleBonds = totalDoubleBonds;
        this.chemInfoContainerGenerator.setGenerateMass(true);
        this.chemInfoContainerGenerator.setGenerateMolFormula(true);
    }

    public void setThreaded(boolean b) {
        this.threaded = b;
    }

    public void execute() {

        if (this.printOut == null) {
            this.printOut = true;
        }
        if (this.stepOfChange == null) {
            this.stepOfChange = 2;
        }
        if (this.chainFactories == null) {
            this.chainFactories = new ArrayList<ChainFactory>();
        }
        
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = null;
        
        // TODO This part should be refactored to a head reader class.
        if (originalMol == null) {
            try {
                MDLV2000Reader reader = null;
                if (this.headMolFile == null) {
                    reader = new MDLV2000Reader(this.headGroup.getHeadMolStream());
                } else {
                    reader = new MDLV2000Reader(new FileReader(this.headMolFile));
                }
                // We keep a copy of the original mol to avoid reading it later.
                //originalMol = (NNMolecule) reader.read(builder.newMolecule());
                originalMol = reader.read(builder.newInstance(AtomContainer.class));
                mol = (IAtomContainer) originalMol.clone();
                
                if (this.headMolStream != null) {
                    this.headMolStream.close();
                }
                //mol = (NNMolecule) reader.read(builder.newInstance(Molecule.class));
            } catch (CloneNotSupportedException e) {
                System.out.println("Cloning not supported");
                System.exit(1);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                System.out.println("Could not find file for " + this.headMolFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Problems here.");
            } catch (CDKException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                mol = (IAtomContainer) originalMol.clone();
            } catch (CloneNotSupportedException ex) {
                System.out.println("Cloning not supported");
                System.exit(1);
            }
        }
        GenericAtomDetector detector = new GenericAtomDetector();
        List<IPseudoAtom> rAtoms = detector.detectGenericAtoms(mol);
        // Now we sort the rAtoms according to the labels ((NNPseudoAtom) at).getLabel().equals("R1")
        Collections.sort(rAtoms, new PseudoAtomListComparator());
        
        chainFactories.clear();
        for (int i = 0; i < rAtoms.size(); i++) {
            chainFactories.add(chainFactoryGenerator.makeChainFactory());
        }
        
        if(linkConfigs==null) {
            linkConfigs = new ArrayList<SingleLinkConfiguration>(this.chainFactories.size());
            for (int i = 0; i < chainFactories.size(); i++) {
                linkConfigs.add(SingleLinkConfiguration.Acyl);
            }
        }
        

        int minCarbonChain = 2; // 1 bond.
        
        List<IAtom> conToRs = new ArrayList<IAtom>(chainFactories.size());
        
        for (int i = 0; i < chainFactories.size(); i++) {
            chainFactories.get(i).setLinkConf(linkConfigs.get(i));
        }



        if (allRAtomAreNotNull(rAtoms)) {
            int generatedStructs = 0;
            long start = System.currentTimeMillis();
            long current;
            
            /**
             * Create and initialize the carbon number iterator.
             */
            SuccesiveIntegerListIterator carbonIterator = new SuccesiveIntegerListIterator(rAtoms.size(), stepOfChange);
            carbonIterator.initialize(this.totalCarbons, minCarbonChain);
            
            numberOfCarbonsLoop:
            while (carbonIterator.hasNext()) {
                
                List<Integer> carbonDisp = carbonIterator.next();
                if(this.maxCarbonsPerSingleChain>0 && fattyAcidsWithMoreCarbonsThanAllowed(carbonDisp))
                    continue;
                /**
                 * Create and initalize the double bond number iterator. Maybe have a variable for the double bond
                 * step size.
                 */
                SuccesiveIntegerListIterator dbIterator = new SuccesiveIntegerListIterator(rAtoms.size(), 1);
                dbIterator.initialize(this.totalDoubleBonds, 0);
                

                while(dbIterator.hasNext()) {
                    List<Integer> dbDisp = dbIterator.next();
                    if(incompatibleDoubleBondsWithCarbons(dbDisp,carbonDisp))
                        continue;
                    for (int i = 0; i < chainFactories.size(); i++) {
                        /**
                         * We set each chain factory to the current carbon and double bond iteration
                         */
                        chainFactories.get(i).setChainIterator(carbonDisp.get(i), carbonDisp.get(i), dbDisp.get(i), dbDisp.get(i));
                        if(this.printOut) {
                            System.out.println("Chain "+i+" : " + carbonDisp.get(i) + "\tDBs : "+dbDisp.get(i));
                        }
                    }

                    LipidFactory lipidFactory = new LipidFactory(this.threaded);
                    lipidFactory.setChemInfoContainerGenerator(chemInfoContainerGenerator);
                    lipidFactory.setHead(mol);
                    // The fact that the linkage is an alkyl or an acyl it should
                    // be signalled here, and handled accordingly in the chain
                    // factories. The default should be to have the =O attached,
                    // and removed if the alkyl case is specifed.
                    conToRs.clear();
                    for (int i = 0; i < chainFactories.size(); i++) {
                        conToRs.add(lipidFactory.addRadicalAnchor(rAtoms.get(i)));   
                    }
                    
                    lipidFactory.resetMultipleChainFactories(chainFactories);


                    ChemInfoContainer cont = lipidFactory.nextLipid();
                    // FIXED: again we see a weird cont object with 3:0_4:1, not null but badly formed.
                    if (this.printOut) {
                        chemInfoContainerGenerator.setGenerateMolFormula(true);
                        chemInfoContainerGenerator.setGenerateMass(true);
                    }
                    if (cont != null) {
                        if (chemInfoContainerGenerator.getGenerateMass()) {
                            this.exactMass = cont.getExactMass();
                            this.naturalMass = cont.getNaturalMass();
                            chemInfoContainerGenerator.setGenerateMass(false);
                        }
                        if (chemInfoContainerGenerator.getGenerateMolFormula()) {
                            this.molFormula = cont.getMolecularFormula();
                            chemInfoContainerGenerator.setGenerateMolFormula(false);
                        }
                        if (chainsConfig != null) {
                            chainsConfig.add(makeChainConfigStr(carbonDisp,dbDisp));
                        }
                    } else {
                        if (chemInfoContainerGenerator.getGenerateMass()) {
                            this.exactMass = null;
                        }
                        if (chemInfoContainerGenerator.getGenerateMolFormula()) {
                            this.molFormula = null;
                        }
                    }

                    if (this.printOut) {
                        chemInfoContainerGenerator.setGenerateMolFormula(true);
                        chemInfoContainerGenerator.setGenerateMass(true);
                    }

                    while (cont != null) {
                        generatedStructs++;
                        if(firstResultOnly)
                            break numberOfCarbonsLoop;
                        // FIXED: In the exotic case, for 7_1, after 5:1_2:0 is generated, there is a cont that is not null
                        // but that is badly initialized. Solve this problem, which is giving a wrong count.
                        cont = lipidFactory.nextLipid();
                    }

                    if (this.printOut) {
                        current = System.currentTimeMillis() - start;
                        System.out.println("StructsGen:" + generatedStructs + " Time:" + current);
                    }

                    for (int i = 0; i < conToRs.size(); i++) {
                        lipidFactory.restoreRadicalAtom(rAtoms.get(i), conToRs.get(i));
                    }
                    lipidFactory.killThreads();
                }
            }
            if (this.printOut) {
                current = System.currentTimeMillis() - start;
                System.out.println("Total Time:" + current + "\tStructsGen:" + generatedStructs);
            }
            this.totalGeneratedStructs = generatedStructs;

        }
    }
    /**
     * @deprecated use {@link #getIsomerInfoContainer() } instead.
     * @return the totalGeneratedStructs
     */
    @Deprecated
    public Integer getTotalGeneratedStructs() {
        return totalGeneratedStructs;
    }

    /**
     * @param printOut the printOut to set
     */
    public void setPrintOut(Boolean printOut) {
        this.printOut = printOut;
    }

    /**
     * @deprecated use {@link #getIsomerInfoContainer() } instead.
     * @return the molFormula
     */
    @Deprecated
    public String getMolFormula() {
        return molFormula;
    }

    /**
     * @deprecated use {@link #getIsomerInfoContainer() } instead.
     * @return the mass
     */
    @Deprecated
    public Double getMass() {
        return exactMass;
    }

    /**
     * @param cfA the cfA to set
     *
    public void setCfA(ChainFactory cfA) {
        this.cfA = cfA;
    }

    /**
     * @param cfB the cfB to set
     *
    public void setCfB(ChainFactory cfB) {
        this.cfB = cfB;
    }

    /**
     * @return the chainsConfig
     */
    public Set<String> getChainsConfig() {
        return chainsConfig;
    }

    /**
     * @param chainsConfig the chainsConfig to set
     */
    public void setChainsConfigContainer(Set<String> chainsConfig) {
        this.chainsConfig = chainsConfig;
    }

    /**
     * @deprecated use setHeadGroup() instead.
     * 
     * @param headMolStream the headMolStream to set
     */
    @Deprecated
    public void setHeadMolStream(InputStream headMolStream) {
        this.headMolStream = headMolStream;
    }

    /**
     * @param maxCarbonsPerSingleChain the maxCarbonsPerSingleChain to set
     */
    public void setMaxCarbonsPerSingleChain(Integer maxCarbonsPerSingleChain) {
        this.maxCarbonsPerSingleChain = maxCarbonsPerSingleChain;
    }

    /**
     * @return the stepOfChange
     */
    public Integer getStepOfChange() {
        return stepOfChange;
    }

    /**
     * @param stepOfChange the stepOfChange to set
     */
    public void setStepOfChange(Integer stepOfChange) throws LNetMoleculeGeneratorException {
        if (!exoticModeOn && stepOfChange % 2 != 0) {
            throw new LNetMoleculeGeneratorException("Steps of change can only be even if the exotic mode is not on");
        }
        this.stepOfChange = stepOfChange;
    }

    /**
     * @return the weirdModeOn
     */
    public boolean isExoticModeOn() {
        return exoticModeOn;
    }

    /**
     * @param exoticModeOn the weirdModeOn to set
     */
    public void setExoticModeOn(boolean exoticModeOn) {
        this.exoticModeOn = exoticModeOn;
    }

    private boolean allRAtomAreNotNull(List<IPseudoAtom> rAtoms) {
        for (IPseudoAtom atom : rAtoms) {
            if(atom==null)
                return false;
        }
        return true;
    }

    private boolean incompatibleDoubleBondsWithCarbons(List<Integer> doubleBondsDisp, List<Integer> carbonDisp) {
        for (int i = 0; i < doubleBondsDisp.size(); i++) {
            if(doubleBondsDisp.get(i) >= carbonDisp.get(i))
                return true;
        }
        return false;
    }

    private String makeChainConfigStr(List<Integer> carbonDisp, List<Integer> dbDisp) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < carbonDisp.size(); i++) {
            builder.append(carbonDisp.get(i)).append(":").append(dbDisp.get(i));
            if(i<carbonDisp.size()-1)
                builder.append("_");
        }
        return builder.toString();
    }

    /**
     * @param chainFactoryGenerator the chainFactoryGenerator to set
     */
    public void setChainFactoryGenerator(ChainFactoryGenerator chainFactoryGenerator) {
        this.chainFactoryGenerator = chainFactoryGenerator;
    }

    private boolean fattyAcidsWithMoreCarbonsThanAllowed(List<Integer> carbonDisp) {
        for (Integer chainLength : carbonDisp) {
            if(chainLength > this.maxCarbonsPerSingleChain)
                return true;
        }
        return false;
    }
    
    /**
     * Get the value of headGroup
     *
     * @return the value of headGroup
     */
    public HeadGroup getHeadGroup() {
        return headGroup;
    }

    /**
     * Set the value of headGroup
     *
     * @param headGroup new value of headGroup
     */
    public void setHeadGroup(HeadGroup headGroup) {
        this.headGroup = headGroup;
    }

    public IsomerInfoContainer getIsomerInfoContainer() {
        IsomerInfoContainer cont = new IsomerInfoContainer();
        cont.setHeadGroup(headGroup);
        cont.setLinkers(linkConfigs);
        cont.setMass(exactMass);
        cont.setMolecularFormula(molFormula);
        cont.setNumOfCarbons(totalCarbons);
        cont.setNumOfDoubleBonds(totalDoubleBonds);
        cont.setNumOfMolsGenerated(totalGeneratedStructs);
        
        return cont;
    }

    /**
     * @param firstResultOnly the firstResultOnly to set
     */
    public void setFirstResultOnly(boolean firstResultOnly) {
        this.firstResultOnly = firstResultOnly;
    }
}
