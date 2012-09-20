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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChainFactory;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.LipidFactory;
import uk.ac.ebi.lipidhome.fastlipid.structure.PooledChainFactory;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import structure.rule.BondDistance3nPlus2Rule;
import structure.rule.BondRule;
import structure.rule.NoDoubleBondsTogetherRule;
import structure.rule.StarterDoubleBondRule;

/**
 *
 * @author pmoreno
 */
public class IsomersGenerator {

    private String headMolFile;
    private InputStream headMolStream;
    private Integer totalCarbons;
    private Integer totalDoubleBonds;
    private Boolean threaded;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    private Integer totalGeneratedStructs;
    private Boolean printOut;
    private String molFormula;
    private Double exactMass;
    private Double naturalMass;
    private ChainFactory cfA;
    private ChainFactory cfB;
    private IAtomContainer originalMol = null;
    private Set<String> chainsConfig;
    private Integer maxCarbonsPerSingleChain;
    private Integer stepOfChange;
    protected SingleLinkConfiguration linkConfR1;
    protected SingleLinkConfiguration linkConfR2;
    private boolean exoticModeOn = false;

    public void setChemInfoContainerGenerator(ChemInfoContainerGenerator generator) {
        this.chemInfoContainerGenerator = generator;
    }

    /**
     * This method sets the linkage to be used in each of the positions. This needs to be modified to accomodate
     * monoacyls and triacyl configurations.
     *
     * TODO : change to accept mono and tri acyl configurations.
     *
     * @param confR1
     * @param confR2
     */
    public void setLinkConfigsR1R2(SingleLinkConfiguration confR1, SingleLinkConfiguration confR2) {
        this.linkConfR1 = confR1;
        this.linkConfR2 = confR2;
    }

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
        // TODO change to accept 1,2, or 3 slots.
        if (this.linkConfR1 == null) {
            this.linkConfR1 = SingleLinkConfiguration.Acyl;
        }
        if (this.linkConfR2 == null) {
            this.linkConfR2 = SingleLinkConfiguration.Acyl;
        }

        //File molFile = new File(this.headMolFile);
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = null;
        //IChemObjectReader reader = readerFactory.createReader(new FileReader(input));

        // This part should be refactored to a head reader class.
        if (originalMol == null) {
            try {
                MDLV2000Reader reader = null;
                if (this.headMolStream != null) {
                    reader = new MDLV2000Reader(this.headMolStream);
                } else {
                    reader = new MDLV2000Reader(new FileReader(this.headMolFile));//(MDLV2000Reader) readerFactory.createReader(new FileReader(input));
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

        IAtom r1 = null;
        IAtom r2 = null;
        for (IAtom at : mol.atoms()) {
            if (at.getSymbol().equals("R")) {
                if (at instanceof IPseudoAtom && ((IPseudoAtom) at).getLabel().equals("R1")) {
                    r1 = at;
                } else if (at instanceof IPseudoAtom && ((IPseudoAtom) at).getLabel().equals("R2")) {
                    r2 = at;
                }
            }
        }

        // This part above should be refactored to a head reader class.

        int minCarbonChain = 2; // 1 bond.
        int carbonsChainA = this.totalCarbons - minCarbonChain;
        int carbonsChainB = minCarbonChain;

        IAtom conToR1;
        IAtom conToR2;


        List<BondRule> rules = Arrays.asList(new BondDistance3nPlus2Rule(), new NoDoubleBondsTogetherRule(), new StarterDoubleBondRule(2));

        if (cfA == null) {
            cfA = new PooledChainFactory();
            cfA.setUseRuleBasedBooleanCounter(false);
            for (BondRule bondRule : rules) {
                cfA.addAlwaysRule(bondRule);
            }
            cfA.setSeeder(new BooleanRBCounterStartSeeder(rules));
        }
        if (cfB == null) {
            cfB = new PooledChainFactory();
            cfB.setUseRuleBasedBooleanCounter(false);
            for (BondRule bondRule : rules) {
                cfB.addAlwaysRule(bondRule);
            }
            cfB.setSeeder(new BooleanRBCounterStartSeeder(rules));
        }


        cfA.setLinkConf(this.linkConfR1);
        cfB.setLinkConf(this.linkConfR2);

        //ChainFactory cfA = new ChainFactory();
        //ChainFactory cfB = new ChainFactory();



        if (r1 != null && r2 != null) {
            int generatedStructs = 0;
            long start = System.currentTimeMillis();
            long current;
            while (carbonsChainB <= this.totalCarbons - minCarbonChain) {
                int doubleBondsA = this.totalDoubleBonds;
                int doubleBondsB = 0;

                if (this.maxCarbonsPerSingleChain != null) {
                    if (this.maxCarbonsPerSingleChain < carbonsChainA) {
                        carbonsChainA -= this.stepOfChange;
                        carbonsChainB += this.stepOfChange;
                        continue;
                    }
                    if (this.maxCarbonsPerSingleChain < carbonsChainB) {
                        break;
                    }
                }

                while (doubleBondsB <= this.totalDoubleBonds) {
                    cfB.setChainIterator(carbonsChainB, carbonsChainB, doubleBondsB, doubleBondsB);
                    cfA.setChainIterator(carbonsChainA, carbonsChainA, doubleBondsA, doubleBondsA);
                    if (this.printOut) {
                        System.out.println("Chain A:" + carbonsChainA + "\tDoubleBonds A:" + doubleBondsA);
                        System.out.println("Chain B:" + carbonsChainB + "\tDoubleBonds B:" + doubleBondsB);
                    }
                    LipidFactory lipidFactory = new LipidFactory(this.threaded);
                    lipidFactory.setChemInfoContainerGenerator(chemInfoContainerGenerator);
                    lipidFactory.setHead(mol);
                    // The fact that the linkage is an alkyl or an acyl it should
                    // be signalled here, and handled accordingly in the chain
                    // factories. The default should be to have the =O attached,
                    // and removed if the alkyl case is specifed.
                    conToR1 = lipidFactory.addRadicalAnchor(r1);
                    conToR2 = lipidFactory.addRadicalAnchor(r2);
                    lipidFactory.resetChainFactories(cfA, cfB);


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
                            chainsConfig.add(carbonsChainA + ":" + doubleBondsA + "_" + carbonsChainB + ":" + doubleBondsB);
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
                        //printChain(mol);
                        if (generatedStructs % 500 == 0 && this.printOut) {
                            current = System.currentTimeMillis() - start;
                            System.out.println("StructsGen:" + generatedStructs + " Time:" + current);
                        }
                        //System.out.println("Mol number:"+counter++);
                        if (cont.getSmiles() != null && this.printOut) {
                            System.out.println("Smiles:" + cont.getSmiles());
                        }
                        if (cont.getMolecularFormula() != null && this.printOut) {
                            System.out.println("MolFor: " + cont.getMolecularFormula());
                        }
                        //System.out.println("Mass:"+cont.getNaturalMass()+"\n\n");
                        if (printOut) {
                        }
                        // FIXED: In the exotic case, for 7_1, after 5:1_2:0 is generated, there is a cont that is not null
                        // but that is badly initialized. Solve this problem, which is giving a wrong count.
                        cont = lipidFactory.nextLipid();
                    }

                    if (this.printOut) {
                        current = System.currentTimeMillis() - start;
                        System.out.println("StructsGen:" + generatedStructs + " Time:" + current);
                    }

                    lipidFactory.restoreRadicalAtom(r1, conToR1);
                    lipidFactory.restoreRadicalAtom(r2, conToR2);
                    lipidFactory.killThreads();
                    doubleBondsA--;
                    doubleBondsB++;
                }
                carbonsChainA -= this.stepOfChange;
                carbonsChainB += this.stepOfChange;
            }
            if (this.printOut) {
                current = System.currentTimeMillis() - start;
                System.out.println("Total Time:" + current + "\tStructsGen:" + generatedStructs);
            }
            this.totalGeneratedStructs = generatedStructs;

        }
    }

    public static void main(String args[]) throws LNetMoleculeGeneratorException {
        String molFilePath = args[0];                   // First argument: headMolFile
        int threadedInt = Integer.parseInt(args[1]);    // Second argument: threaded
        String[] tokens = args[2].split(":");           // Third argument: carbons:doubleBonds
        int carbons = Integer.parseInt(tokens[0]);
        int doubleBonds = Integer.parseInt(tokens[1]);


        ChemInfoContainerGenerator chemInfoContainerGenerator = new ChemInfoContainerGenerator();
        chemInfoContainerGenerator.setUseCachedObjects(Boolean.TRUE);
        chemInfoContainerGenerator.setGenerateInChi(true);
        chemInfoContainerGenerator.setGenerateInChiKey(true);
        chemInfoContainerGenerator.setGenerateInChIAux(true);
        chemInfoContainerGenerator.setGenerateSmiles(true);
        chemInfoContainerGenerator.setGenerateMolFormula(Boolean.TRUE);

        IsomersGenerator generator = new IsomersGenerator();
        generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        generator.setThreaded(false);
        generator.setHeadMolFile(molFilePath);
        generator.setTotalCarbons(carbons);
        generator.setTotalDoubleBonds(doubleBonds);
        generator.setPrintOut(Boolean.TRUE);

        generator.execute();


    }

    /**
     * @return the totalGeneratedStructs
     */
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
     * @return the molFormula
     */
    public String getMolFormula() {
        return molFormula;
    }

    /**
     * @return the mass
     */
    public Double getMass() {
        return exactMass;
    }

    /**
     * @param cfA the cfA to set
     */
    public void setCfA(ChainFactory cfA) {
        this.cfA = cfA;
    }

    /**
     * @param cfB the cfB to set
     */
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
     * @param headMolStream the headMolStream to set
     */
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
}
