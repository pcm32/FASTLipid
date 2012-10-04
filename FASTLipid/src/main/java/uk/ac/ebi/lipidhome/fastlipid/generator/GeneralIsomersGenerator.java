/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.generator;

import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.IsomerInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChainFactory;
import uk.ac.ebi.lipidhome.fastlipid.structure.LipidFactory;
import java.util.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import uk.ac.ebi.lipidhome.fastlipid.util.GenericAtomDetector;

/**
 *
 * @author pmoreno
 */
public class GeneralIsomersGenerator extends AbstractIsomersGenerator {
    private Integer totalCarbons;
    private Integer totalDoubleBonds;
    private Boolean threaded = false;
    private Integer totalGeneratedStructs;
    private Boolean printOut;
    private String molFormula;
    private Double exactMass;
    private Double naturalMass;
    private IAtomContainer originalMol = null;
    private Set<String> chainsConfig;

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
                MDLV2000Reader reader = new MDLV2000Reader(this.headGroup.getHeadMolStream());
                // We keep a copy of the original mol to avoid reading it later.
                originalMol = reader.read(builder.newInstance(AtomContainer.class));
                mol = (IAtomContainer) originalMol.clone();
            } catch (CloneNotSupportedException e) {
                System.out.println("Cloning not supported");
                System.exit(1);
            } catch (CDKException e) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e);
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
            //IntegerListIterator carbonIterator = new ListBasedIntegerListIterator(rAtoms.size(), stepOfChange);
            IntegerListIterator carbonIterator = new AccAscConstrainedBasedIntegerListIterator(rAtoms.size(), stepOfChange);
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
                //IntegerListIterator dbIterator = new ListBasedIntegerListIterator(rAtoms.size(), 1);
                IntegerListIterator dbIterator = new AccAscConstrainedBasedIntegerListIterator(rAtoms.size(), 1);
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
     * @return the stepOfChange
     */
    public Integer getStepOfChange() {
        return stepOfChange;
    }

    private boolean fattyAcidsWithMoreCarbonsThanAllowed(List<Integer> carbonDisp) {
        for (Integer chainLength : carbonDisp) {
            if(chainLength > this.maxCarbonsPerSingleChain)
                return true;
        }
        return false;
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
}
