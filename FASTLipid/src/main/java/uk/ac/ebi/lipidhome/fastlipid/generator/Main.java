/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.LipidFactory;

/**
 * @TODO get rid of this.
 * This is an old main and should be deleted
 *
 * @author pmoreno
 */
@Deprecated
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String molFilePath = args[0];
        int threadedInt = Integer.parseInt(args[1]);
        String AminC_maxC_minDB_maxDB = args[2]; //2:16:0:10 separated by :
        String BminC_maxC_minDB_maxDB;
        if(args.length>2)
            BminC_maxC_minDB_maxDB = args[3];
        else
            BminC_maxC_minDB_maxDB = AminC_maxC_minDB_maxDB;
        String[] tokens = AminC_maxC_minDB_maxDB.split(":");
        int AminC = Integer.parseInt(tokens[0]);
        int AmaxC = Integer.parseInt(tokens[1]);
        int AminDB = Integer.parseInt(tokens[2]);
        int AmaxDB = Integer.parseInt(tokens[3]);
        tokens = BminC_maxC_minDB_maxDB.split(":");
        int BminC = Integer.parseInt(tokens[0]);
        int BmaxC = Integer.parseInt(tokens[1]);
        int BminDB = Integer.parseInt(tokens[2]);
        int BmaxDB = Integer.parseInt(tokens[3]);

        File molFile = new File(molFilePath);
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = null;
        //IChemObjectReader reader = readerFactory.createReader(new FileReader(input));
        try {
            MDLV2000Reader reader = new MDLV2000Reader(new FileReader(molFile));//(MDLV2000Reader) readerFactory.createReader(new FileReader(input));
            //mol = (NNMolecule) reader.read(builder.newMolecule());
            mol = reader.read(builder.newInstance(AtomContainer.class));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("Could not find file for " + molFilePath);
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

//        ArrayList<IBond> bonds = new ArrayList<IBond>();
//        for(int i=0;i<3;i++) {
//            if(i==0)
//                bonds.add(builder.newBond(builder.newAtom("C"), builder.newAtom("C")));
//            else
//                bonds.add(builder.newBond(bonds.get(i-1).getAtom(1),builder.newAtom("C")));
//            testMolR1.addBond(bonds.get(i));
//            for(IAtom atom : bonds.get(i).atoms())
//                testMolR1.addAtom(atom);
//        }

        if (r1 != null && r2 != null) {
            LipidFactory lipidFactory = new LipidFactory(threadedInt==1,false);
            lipidFactory.setHead(mol);
            lipidFactory.addRadicalAnchor(r1);
            lipidFactory.addRadicalAnchor(r2);
            //lipidFactory.addChainFactoryWithConstraints(6, 14, 2, 6);
            lipidFactory.addChainFactoryWithConstraints(AminC,AmaxC,AminDB,AmaxDB);
            //lipidFactory.addChainFactoryWithConstraints(6, 12, 2, 6);
            lipidFactory.addChainFactoryWithConstraints(BminC,BmaxC,BminDB,BmaxDB);

            ChemInfoContainer cont = lipidFactory.nextLipid();
            int generatedStructs = 1;
            long start = System.currentTimeMillis();
            while (cont != null) {
                generatedStructs++;
                //printChain(mol);
                if (generatedStructs % 500 == 0) {
                //    long current = System.currentTimeMillis() - start;
                    System.out.println("StructsGen:" + generatedStructs);
                }
                //System.out.println("Mol number:"+counter++);
                //System.out.println("Smiles:" + cont.getSmiles());
                //System.out.println("Mass:"+cont.getMass()+"\n\n");
                cont = lipidFactory.nextLipid();
            }
            long current = System.currentTimeMillis() - start;
            System.out.println("Time:" + current + "\tStructsGen:" + generatedStructs);
            System.exit(0);
            lipidFactory.killThreads();
        }

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
}
