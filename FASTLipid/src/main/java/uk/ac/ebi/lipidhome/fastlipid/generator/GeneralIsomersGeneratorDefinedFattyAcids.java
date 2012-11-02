/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.generator;

import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChainFactory;
import uk.ac.ebi.lipidhome.fastlipid.structure.LipidFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondDistance3nPlus2Rule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.NoDoubleBondsTogetherRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.StarterDoubleBondRule;
import uk.ac.ebi.lipidhome.fastlipid.util.GenericAtomDetector;

/**
 *
 * @author pmoreno
 */
public class GeneralIsomersGeneratorDefinedFattyAcids extends AbstractIsomersGenerator implements IterableGenerator, Runnable {

    private final List<Integer> carbonsPerChain;
    private final List<Integer> doubleBondsPerChain;
    private Boolean threaded;
    private Integer totalGeneratedStructs;
    private Boolean printOut;
    private String molFormula;
    private Double exactMass;
    private Double naturalMass;
    private boolean iterable;
    private Thread t;
    private ChemInfoContainer FINAL = new ChemInfoContainer();


    public GeneralIsomersGeneratorDefinedFattyAcids() {
        this.carbonsPerChain = new ArrayList<Integer>();
        this.doubleBondsPerChain = new ArrayList<Integer>();
    }
    
    /**
     * @deprecated use {@link #setHeadGroup(uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup) } instead.
     * @param headMolFile
     */
    public void setHeadMolFile(String headMolFile) {
        
    }

    /**
     * Sets the carbons per chain, in the same order as provided.
     *
     * @param carbonsPerChain
     */
    public void setCarbonsPerChain(Integer... carbonsPerChain) {
        this.carbonsPerChain.clear();
        this.carbonsPerChain.addAll(Arrays.asList(carbonsPerChain));
    }

    /**
     * Sets the carbons per chain, in the same order as provided.
     *
     * @param carbonsPerChain
     */
    public void setCarbonsPerChain(List<Integer> carbonsPerChain) {
        this.carbonsPerChain.clear();
        this.carbonsPerChain.addAll(carbonsPerChain);
    }

    /**
     * Sets the double bonds per chain, in the same order as provided.
     *
     * @param totalDoubleBonds
     */
    public void setDoubleBondsPerChain(Integer... totalDoubleBonds) {
        this.doubleBondsPerChain.clear();
        this.doubleBondsPerChain.addAll(Arrays.asList(totalDoubleBonds));
    }

    /**
     * Sets the double bonds per chain, in the same order as provided.
     *
     * @param totalDoubleBonds
     */
    public void setDoubleBondsPerChain(List<Integer> totalDoubleBonds) {
        this.doubleBondsPerChain.clear();
        this.doubleBondsPerChain.addAll(totalDoubleBonds);
    }

    public void setThreaded(boolean b) {
        this.threaded = b;
    }

    public void run() {

        if (this.printOut == null) {
            this.printOut = true;
        }

        if (this.chainFactories == null) {
            this.chainFactories = new ArrayList<ChainFactory>();
        }

        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = null;
        try {
            MDLV2000Reader reader = new MDLV2000Reader(this.headGroup.getHeadMolStream());
            // We keep a copy of the original mol to avoid reading it later.
            //mol = (NNMolecule) reader.read(builder.newMolecule());
            mol = reader.read(builder.newInstance(AtomContainer.class));
        } catch (CDKException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        GenericAtomDetector detector = new GenericAtomDetector();
        List<IPseudoAtom> rAtoms = detector.detectGenericAtoms(mol);
        // Now we sort the rAtoms according to the labels ((NNPseudoAtom) at).getLabel().equals("R1")
        Collections.sort(rAtoms, new PseudoAtomListComparator());

        chainFactories.clear();
        for (int i = 0; i < rAtoms.size(); i++) {
            chainFactories.add(chainFactoryGenerator.makeChainFactory());
        }

        if (linkConfigs == null) {
            linkConfigs = new ArrayList<SingleLinkConfiguration>(this.chainFactories.size());
            for (int i = 0; i < chainFactories.size(); i++) {
                linkConfigs.add(SingleLinkConfiguration.Acyl);
            }
        }

        List<IAtom> conToRs = new ArrayList<IAtom>(chainFactories.size());

        for (int i = 0; i < chainFactories.size(); i++) {
            chainFactories.get(i).setLinkConf(linkConfigs.get(i));
        }

        int minCarbonChain = 2;

        List<BondRule> rules = Arrays.asList(new BondDistance3nPlus2Rule(),
                new NoDoubleBondsTogetherRule(),
                new StarterDoubleBondRule(2));

        if (allRAtomAreNotNull(rAtoms)) {
            int generatedStructs = 0;
            long start = System.currentTimeMillis();
            long current;

            for (int i = 0; i < chainFactories.size(); i++) {
                /**
                 * We set each chain factory to the current carbon and double bond iteration
                 */
                chainFactories.get(i).setChainIterator(carbonsPerChain.get(i), carbonsPerChain.get(i), doubleBondsPerChain.get(i), doubleBondsPerChain.get(i));
                if (this.printOut) {
                    System.out.println("Chain " + i + " : " + carbonsPerChain.get(i) + "\tDBs : " + doubleBondsPerChain.get(i));
                }
            }

            LipidFactory lipidFactory = new LipidFactory(this.threaded,this.firstResultOnly);
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

            if (cont != null) {
                cont.setLinkers(linkConfigs);
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
                if (generatedStructs % 500 == 0 && this.printOut) {
                    current = System.currentTimeMillis() - start;
                    System.out.println("StructsGen:" + generatedStructs + " Time:" + current);
                }
                if (cont.getSmiles() != null && this.printOut) {
                    System.out.println("Smiles:" + cont.getSmiles());
                }
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

    /**
     * @deprecated use {@link #setHeadGroup(uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup) } instead.
     * @param headMolStream 
     */
    public void setHeadMolStream(InputStream headMolStream) {
        
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

    @Override
    public void execute() {
        this.run();
    }
}
