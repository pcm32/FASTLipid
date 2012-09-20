/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lnetmoleculegenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import structure.ChainFactory;
import structure.ChemInfoContainer;
import structure.ChemInfoContainerGenerator;
import structure.LipidFactory;
import structure.PooledChainFactory;
import structure.rule.BondDistance3nPlus2Rule;
import structure.rule.NoDoubleBondsTogetherRule;
import structure.rule.StarterDoubleBondRule;

/**
 *
 * @author pmoreno
 */
public class IsomersGeneratorRangeCDB {

    private String headMolFile;
    private Integer maxCarbons;
    private Integer minCarbons;
    private Integer maxDoubleBonds;
    private Integer minDoubleBonds;
    private Boolean threaded;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    private Integer totalGeneratedStructs;
    private Boolean printOut;
    private String molFormula;
    private Double exactMass;
    private Double naturalMass;

    public void setChemInfoContainerGenerator(ChemInfoContainerGenerator generator) {
        this.chemInfoContainerGenerator = generator;
    }

    public void setHeadMolFile(String headMolFile) {
        this.headMolFile = headMolFile;
    }

    public void setMaxCarbons(Integer maxCarbons) {
        this.maxCarbons = maxCarbons;
    }

    public void setMinCarbons(Integer minCarbons) {
        this.minCarbons = minCarbons;
    }



    public void setMaxDoubleBonds(Integer maxDoubleBonds) {
        this.maxDoubleBonds = maxDoubleBonds;
    }

    public void setMinDoubleBonds(Integer minDoubleBonds) {
        this.minDoubleBonds = minDoubleBonds;
    }

    public void setThreaded(boolean b) {
        this.threaded = b;
    }

    public void execute() {

        if (this.printOut == null) {
            this.printOut = true;
        }

        File molFile = new File(this.headMolFile);
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = null;
        //IChemObjectReader reader = readerFactory.createReader(new FileReader(input));
        try {
            MDLV2000Reader reader = new MDLV2000Reader(new FileReader(molFile));//(MDLV2000Reader) readerFactory.createReader(new FileReader(input));
            //mol = (NNMolecule) reader.read(builder.newMolecule());
            mol = reader.read(builder.newInstance(AtomContainer.class));
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
        int carbonsChainA = this.maxCarbons - minCarbonChain;
        int carbonsChainB = minCarbonChain;

        IAtom conToR1;
        IAtom conToR2;

        ChainFactory cfA = new PooledChainFactory();
        ChainFactory cfB = new PooledChainFactory();
        //ChainFactory cfA = new ChainFactory();
        //ChainFactory cfB = new ChainFactory();

        cfA.addAlwaysRule(new BondDistance3nPlus2Rule());
        cfA.addAlwaysRule(new NoDoubleBondsTogetherRule());
        cfA.addAlwaysRule(new StarterDoubleBondRule(2));

        cfB.addAlwaysRule(new BondDistance3nPlus2Rule());
        cfB.addAlwaysRule(new NoDoubleBondsTogetherRule());
        cfB.addAlwaysRule(new StarterDoubleBondRule(2));

        if (r1 != null && r2 != null) {
            int generatedStructs = 0;
            long start = System.currentTimeMillis();
            long current;
            while (carbonsChainB <= this.maxCarbons - minCarbonChain) {
                int doubleBondsA = this.maxDoubleBonds;
                int doubleBondsB = 0;

                while (doubleBondsB <= this.maxDoubleBonds) {
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
                carbonsChainA -= 2;
                carbonsChainB += 2;
            }
            if (this.printOut) {
                current = System.currentTimeMillis() - start;
                System.out.println("Total Time:" + current + "\tStructsGen:" + generatedStructs);
            }
            this.totalGeneratedStructs = generatedStructs;

        }
    }

    public static void main(String args[]) {
        String molFilePath = args[0];                   // First argument: headMolFile
        int threadedInt = Integer.parseInt(args[1]);    // Second argument: threaded
        String[] tokens = args[2].split(":");           // Third argument: carbons:doubleBonds
        int carbons = Integer.parseInt(tokens[0]);
        int doubleBonds = Integer.parseInt(tokens[1]);

        ChemInfoContainerGenerator chemInfoContainerGenerator = new ChemInfoContainerGenerator();
        chemInfoContainerGenerator.setGenerateInChi(true);
        chemInfoContainerGenerator.setGenerateInChiKey(true);
        chemInfoContainerGenerator.setGenerateInChIAux(true);
        chemInfoContainerGenerator.setGenerateMolFormula(Boolean.TRUE);

        IsomersGeneratorRangeCDB generator = new IsomersGeneratorRangeCDB();
        generator.setChemInfoContainerGenerator(chemInfoContainerGenerator);
        generator.setThreaded(false);
        generator.setHeadMolFile(molFilePath);
        generator.setMaxCarbons(carbons);
        generator.setMaxDoubleBonds(doubleBonds);

        generator.execute();

//        File molFile = new File(molFilePath);
//        SilentChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
//        NNMolecule mol = null;
//        //IChemObjectReader reader = readerFactory.createReader(new FileReader(input));
//        try {
//            MDLV2000Reader reader = new MDLV2000Reader(new FileReader(molFile));//(MDLV2000Reader) readerFactory.createReader(new FileReader(input));
//            mol = (NNMolecule) reader.read(builder.newMolecule());
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            System.out.println("Could not find file for " + molFilePath);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (CDKException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        IAtom carbonPrevR1 = null;
//        IAtom carbonPrevR2 = null;
//        IAtom r1 = null;
//        IAtom r2 = null;
//        for (IAtom at : mol.atoms()) {
//            if (at.getSymbol().equals("R")) {
//                if (at instanceof NNPseudoAtom && ((NNPseudoAtom) at).getLabel().equals("R1")) {
//                    r1 = at;
//                    for (IAtom con : mol.getConnectedAtomsList(at)) {
//                        carbonPrevR1 = con;
//                    }
//                } else if (at instanceof NNPseudoAtom && ((NNPseudoAtom) at).getLabel().equals("R2")) {
//                    r2 = at;
//                    for (IAtom con : mol.getConnectedAtomsList(at)) {
//                        carbonPrevR2 = con;
//                    }
//                }
//            }
//        }
//
//        int minCarbonChain = 2; // 1 bond.
//        int carbonsChainA = carbons - minCarbonChain;
//        int carbonsChainB = minCarbonChain;
//
//        IAtom conToR1;
//        IAtom conToR2;
//
//        ChainFactory cfA = new PooledChainFactory();
//        ChainFactory cfB = new PooledChainFactory();
//        //ChainFactory cfA = new ChainFactory();
//        //ChainFactory cfB = new ChainFactory();
//
//        cfA.addAlwaysRule(new BondDistance3nPlus2Rule());
//        cfA.addAlwaysRule(new NoDoubleBondsTogetherRule());
//        cfA.addAlwaysRule(new StarterDoubleBondRule(2));
//
//        cfB.addAlwaysRule(new BondDistance3nPlus2Rule());
//        cfB.addAlwaysRule(new NoDoubleBondsTogetherRule());
//        cfB.addAlwaysRule(new StarterDoubleBondRule(2));
//
//        if (r1 != null && r2 != null) {
//            int generatedStructs = 1;
//            long start = System.currentTimeMillis();
//            long current;
//            while (carbonsChainB <= carbons - minCarbonChain) {
//                int doubleBondsA = doubleBonds;
//                int doubleBondsB = 0;
//
//                while (doubleBondsB <= doubleBonds) {
//                    cfB.setChainIterator(carbonsChainB, carbonsChainB, doubleBondsB, doubleBondsB);
//                    cfA.setChainIterator(carbonsChainA, carbonsChainA, doubleBondsA, doubleBondsA);
//                    System.out.println("Chain A:"+carbonsChainA+"\tDoubleBonds A:"+doubleBondsA);
//                    System.out.println("Chain B:"+carbonsChainB+"\tDoubleBonds B:"+doubleBondsB);
//                    LipidFactory lipidFactory = new LipidFactory(threadedInt == 1);
//                    lipidFactory.setHead(mol);
//                    conToR1 = lipidFactory.addRadicalAnchor(r1);
//                    conToR2 = lipidFactory.addRadicalAnchor(r2);
//                    lipidFactory.resetChainFactories(cfA, cfB);
//
//                    ChemInfoContainer cont = lipidFactory.nextLipid();
//
//                    while (cont != null) {
//                        generatedStructs++;
//                        //printChain(mol);
//                        if (generatedStructs % 500 == 0) {
//                            current = System.currentTimeMillis() - start;
//                            System.out.println("StructsGen:" + generatedStructs+" Time:"+current);
//                        }
//                        //System.out.println("Mol number:"+counter++);
//                        if(cont.getSmiles()!=null)
//                            System.out.println("Smiles:" + cont.getSmiles());
//                        //System.out.println("Mass:"+cont.getNaturalMass()+"\n\n");
//                        cont = lipidFactory.nextLipid();
//                    }
//
//                    current = System.currentTimeMillis() - start;
//                    System.out.println("StructsGen:" + generatedStructs+" Time:"+current);
//
//                    lipidFactory.restoreRadicalAtom(r1, conToR1);
//                    lipidFactory.restoreRadicalAtom(r2, conToR2);
//                    lipidFactory.killThreads();
//                    doubleBondsA--;
//                    doubleBondsB++;
//                }
//                carbonsChainA-=2;
//                carbonsChainB+=2;
//            }
//            current = System.currentTimeMillis() - start;
//            System.out.println("Total Time:" + current + "\tStructsGen:" + generatedStructs);
//        }
//        System.exit(0);

//        if(r1!=null && r2!=null) {
//            System.out.println("Current atom count:"+mol.getAtomCount());
//            mol.removeAtom(r1);
//            IBond bondR1 = builder.newBond(carbonPrevR1, testMolR1.getAtom(0));
//            mol.addBond(bondR1);
//            mol.add(testMolR1);
//            System.out.println("Current atom count, after first addition:"+mol.getAtomCount());
//            mol.removeAtom(r2);
//        }
//
//        if(carbonPrevR1!=null && carbonPrevR2!=null) {
//            System.out.println("Working up to here!");
//        }
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
}
