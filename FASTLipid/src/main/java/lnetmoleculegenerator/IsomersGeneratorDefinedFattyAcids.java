/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lnetmoleculegenerator;

import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
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

/**
 *
 * @author pmoreno
 */
public class IsomersGeneratorDefinedFattyAcids implements IterableGenerator, Runnable {

    private String headMolFile;
    private InputStream headMolStream;
    private Integer carbonsChainA;
    private Integer doubleBondsChainA;
    private Integer carbonsChainB;
    private Integer doubleBondsChainB;
    private Boolean threaded;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    private Integer totalGeneratedStructs;
    private Boolean printOut;
    private String molFormula;
    private Double exactMass;
    private Double naturalMass;
    private boolean iterable;
    private Thread t;
    private ChemInfoContainer FINAL = new ChemInfoContainer();
    private SingleLinkConfiguration linkConfR1;
    private SingleLinkConfiguration linkConfR2;

    public void setChemInfoContainerGenerator(ChemInfoContainerGenerator generator) {
        this.chemInfoContainerGenerator = generator;
    }

    public void setHeadMolFile(String headMolFile) {
        this.headMolFile = headMolFile;
    }

    public void setCarbonsChainA(Integer totalCarbons) {
        this.carbonsChainA = totalCarbons;
    }

    public void setDoubleBondsChainA(Integer totalDoubleBonds) {
        this.doubleBondsChainA = totalDoubleBonds;
    }

    /**
     * @param carbonsChain2 the carbonsChain2 to set
     */
    public void setCarbonsChainB(Integer carbonsChain2) {
        this.carbonsChainB = carbonsChain2;
    }

    /**
     * @param doubleBondsChain2 the doubleBondsChain2 to set
     */
    public void setDoubleBondsChainB(Integer doubleBondsChain2) {
        this.doubleBondsChainB = doubleBondsChain2;
    }

    public void setThreaded(boolean b) {
        this.threaded = b;
    }

    public void run() {

        if (this.printOut == null) {
            this.printOut = true;
        }

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
        try {
            MDLV2000Reader reader = null;
            if (this.headMolStream != null) {
                reader = new MDLV2000Reader(this.headMolStream);
            } else {
                reader = new MDLV2000Reader(new FileReader(this.headMolFile));//(MDLV2000Reader) readerFactory.createReader(new FileReader(input));
            }
            // We keep a copy of the original mol to avoid reading it later.
            //mol = (NNMolecule) reader.read(builder.newMolecule());
            mol = reader.read(builder.newInstance(AtomContainer.class));
            if (this.headMolStream != null) {
                this.headMolStream.close();
            }


            //mol = (NNMolecule) reader.read(builder.newInstance(Molecule.class));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("Could not find file for " + this.headMolFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CDKException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        IAtom carbonPrevR1 = null;
        IAtom carbonPrevR2 = null;
        IAtom r1 = null;
        IAtom r2 = null;
        for (IAtom at : mol.atoms()) {
            if (at.getSymbol().equals("R")) {
                if (at instanceof IPseudoAtom && ((IPseudoAtom) at).getLabel().equals("R1")) {
                    r1 = at;
                    for (IAtom con : mol.getConnectedAtomsList(at)) {
                        carbonPrevR1 = con;
                    }
                } else if (at instanceof IPseudoAtom && ((IPseudoAtom) at).getLabel().equals("R2")) {
                    r2 = at;
                    for (IAtom con : mol.getConnectedAtomsList(at)) {
                        carbonPrevR2 = con;
                    }
                }
            }
        }

        int minCarbonChain = 2; // 1 bond.
        int carbonsChainA = this.carbonsChainA;
        int carbonsChainB = this.carbonsChainB;

        IAtom conToR1;
        IAtom conToR2;
        
        List<BondRule> rules = Arrays.asList(new BondDistance3nPlus2Rule(),
                                             new NoDoubleBondsTogetherRule(),
                                             new StarterDoubleBondRule(2));

        ChainFactory cfA = new PooledChainFactory();
        ChainFactory cfB = new PooledChainFactory();
        
        //ChainFactory cfA = new ChainFactory();
        //ChainFactory cfB = new ChainFactory();

        cfA.setSeeder(new BooleanRBCounterStartSeeder(rules));
        cfB.setSeeder(new BooleanRBCounterStartSeeder(rules));

        
        // We can have this configurates outside... although this runs only once.
        for (BondRule bondRule : rules) {
            cfA.addAlwaysRule(bondRule);
            cfB.addAlwaysRule(bondRule);
        }
        
        cfA.setUseRuleBasedBooleanCounter(true);
        cfB.setUseRuleBasedBooleanCounter(true);

        cfA.setLinkConf(this.linkConfR1);
        cfB.setLinkConf(this.linkConfR2);

        /*BooleanRBCounterStartSeeder.addRule(new BondDistance3nPlus2Rule());
        BooleanRBCounterStartSeeder.addRule(new NoDoubleBondsTogetherRule());
        BooleanRBCounterStartSeeder.addRule(new StarterDoubleBondRule(2));*/

        if (r1 != null && r2 != null) {
            int generatedStructs = 0;
            long start = System.currentTimeMillis();
            long current;

            int doubleBondsA = this.doubleBondsChainA; // Why do we do this??
            int doubleBondsB = this.doubleBondsChainB;

            cfB.setChainIterator(carbonsChainB, carbonsChainB, doubleBondsB, doubleBondsB);
            cfA.setChainIterator(carbonsChainA, carbonsChainA, doubleBondsA, doubleBondsA);
            if (this.printOut) {
                System.out.println("Chain A:" + carbonsChainA + "\tDoubleBonds A:" + doubleBondsA);
                System.out.println("Chain B:" + carbonsChainB + "\tDoubleBonds B:" + doubleBondsB);
            }
            LipidFactory lipidFactory = new LipidFactory(this.threaded);
            lipidFactory.setChemInfoContainerGenerator(chemInfoContainerGenerator);
            lipidFactory.setHead(mol);
            conToR1 = lipidFactory.addRadicalAnchor(r1);
            conToR2 = lipidFactory.addRadicalAnchor(r2);
            lipidFactory.resetChainFactories(cfA, cfB);

            ChemInfoContainer cont = lipidFactory.nextLipid();

            if (cont != null) {
                if (this.isInIterableMode()) {
                    queue.add(cont);
                }
                if (chemInfoContainerGenerator.getGenerateMass()) {
                    this.exactMass = cont.getExactMass();
                    this.naturalMass = cont.getNaturalMass();
                    chemInfoContainerGenerator.setGenerateMass(false);
                }
                if (chemInfoContainerGenerator.getGenerateMolFormula()) {
                    this.molFormula = cont.getMolecularFormula();
                    chemInfoContainerGenerator.setGenerateMolFormula(false);
                }
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
                //System.out.println("Mass:"+cont.getNaturalMass()+"\n\n");
                cont = lipidFactory.nextLipid();
                if (this.isInIterableMode() && cont != null) {
                    try {
                        queue.put(cont);
                    } catch (InterruptedException ex) {
                        continue;
                    }
                }
            }

            if (this.isInIterableMode()) {
                try {
                    queue.put(this.FINAL);
                } catch (InterruptedException ex) {
                }
            }


            if (this.printOut) {
                current = System.currentTimeMillis() - start;
                System.out.println("StructsGen:" + generatedStructs + " Time:" + current);
            }

            //lipidFactory.restoreRadicalAtom(r1, conToR1);
            //lipidFactory.restoreRadicalAtom(r2, conToR2);
            lipidFactory.killThreads();

            if (this.printOut) {
                current = System.currentTimeMillis() - start;
                System.out.println("Total Time:" + current + "\tStructsGen:" + generatedStructs);
            }
            this.totalGeneratedStructs = generatedStructs;
        }
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
    public Double getExactMass() {
        return exactMass;
    }
    
    /**
     * @return the mass
     */
    public Double getNaturalMass() {
        return naturalMass;
    }

    public void setHeadMolStream(InputStream headMolStream) {
        this.headMolStream = headMolStream;
    }

    public void setIterableMode(boolean isIterable) {
        // if the thread hasn't started or it has finished, then we can change the iterable mode.
        if (t == null) {
            t = new Thread(this);
        }
        if (this.t.getState().equals(Thread.State.NEW) || this.t.getState().equals(Thread.State.TERMINATED)) {
            this.iterable = isIterable;
        }
    }

    public ChemInfoContainer getNext() throws LNetMoleculeGeneratorException {
        ChemInfoContainer res = null;
        if (this.isInIterableMode()) {
            try {
                res = queue.take();
            } catch (InterruptedException ex) {
            }

        }
        if (res.equals(this.FINAL)) {
            return null;
        }
        return res;
    }

    public boolean isInIterableMode() {
        return this.iterable;
    }

    public void executeInSeparateThread() {
        if (this.isInIterableMode()) {
            t.start();
        }
    }

    public void setLinkConfigsR1R2(SingleLinkConfiguration confR1, SingleLinkConfiguration confR2) {
        this.linkConfR1 = confR1;
        this.linkConfR2 = confR2;
    }
}
