/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package structure;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import util.AtomPool;
import util.BondPool;
import util.MoleculePool;
import util.PoolProvider;

/**
 *
 * @author pmoreno
 */
public class PooledChainFactory extends ChainFactory {

    private MoleculePool molPool;
    private AtomPool atomPool;
    private BondPool bondPool;
    private IChemObjectBuilder builder;
    private Boolean usingPoolProvider = false;

    public PooledChainFactory() {
        super();
        this.builder = SilentChemObjectBuilder.getInstance();
        molPool = new MoleculePool(builder);
        atomPool = new AtomPool(builder);
        bondPool = new BondPool(builder);
        this.linkerHandler = new PooledLinkerHandler(atomPool, bondPool);
    }

    public PooledChainFactory(PoolProvider provider) {
        super();
        this.builder = SilentChemObjectBuilder.getInstance();
        molPool = provider.getMoleculePool();
        atomPool = provider.getAtomPool();
        bondPool = provider.getBondPool();
        usingPoolProvider = true;
    }

    public IMolecule refurbishCurrentChain() {
        IMolecule currentChain = super.getCurrentChain();
        for (IBond bond : currentChain.bonds()) {
            if(bond.getOrder().equals(IBond.Order.DOUBLE) && bond.getAtom(0).getSymbol().equals("C") && bond.getAtom(1).getSymbol().equals("C")) {
                bond.setOrder(IBond.Order.SINGLE);
            }
        }
        if(!linkerHandler.checkLinker(currentChain,this.linkConf)) {
            linkerHandler.setLinker(currentChain,this.linkConf);
        }
        return currentChain;
    }
    
    @Override
    public IMolecule getChain(int bondsNumber) {
        IMolecule currentChain = super.getCurrentChain();
        if (currentChain != null) {
            // tries to avoid creating additional mols, bonds, etc.
            if(getChainBondSize(currentChain)==bondsNumber)
                return refurbishCurrentChain();
            for (IAtom atom : currentChain.atoms()) {
                atomPool.checkIn(atom);
            }
            for (IBond bond : currentChain.bonds()) {
                bond.setOrder(IBond.Order.SINGLE);
                bondPool.checkIn(bond);
            }
            currentChain.removeAllElements();
            molPool.checkIn(currentChain);
        }
        IMolecule chain = molPool.checkOut();
        //chain.removeAllElements();
        IBond previousBond = null;
        String element = "C";
        String oxygenElement = "O";
        IBond oxygenDoubleBond = null;
        IAtom oxygen = null;
        for (int i = 0; i < bondsNumber; i++) {
            IBond bond;
            if (previousBond == null) {
                bond = bondPool.checkOut();
                IAtom carbon1 = atomPool.checkOut();

                carbon1.setSymbol(element);
                IAtom carbon2 = atomPool.checkOut();
                carbon2.setSymbol(element);
                bond.setAtom(carbon1, 0);
                bond.setAtom(carbon2, 1);
                bond.setOrder(IBond.Order.SINGLE);
                if (this.linkConf.equals(SingleLinkConfiguration.Acyl)) {
                    oxygenDoubleBond = bondPool.checkOut();
                    oxygen = atomPool.checkOut();
                    oxygen.setSymbol(oxygenElement);
                    oxygenDoubleBond.setAtom(carbon1, 0);
                    oxygenDoubleBond.setAtom(oxygen, 1);
                    oxygenDoubleBond.setOrder(IBond.Order.DOUBLE);
                }
            } else {
                IAtom atom = atomPool.checkOut();
                atom.setSymbol(element);
                bond = bondPool.checkOut();
                bond.setAtom(previousBond.getAtom(1), 0);
                bond.setAtom(atom, 1);
                bond.setOrder(IBond.Order.SINGLE);
                //bond = builder.newBond(previousBond.getAtom(1), atom);
            }
            chain.addBond(bond);
            for (IAtom atom : bond.atoms()) {
                chain.addAtom(atom);
            }
            previousBond = bond;
        }
        if (this.linkConf.equals(SingleLinkConfiguration.Acyl)) {
            chain.addBond(oxygenDoubleBond);
            chain.addAtom(oxygen);
        }
        super.setCurrentChain(chain);
        return chain;
    }

    @Override
    public void cleanUp() {
        if (!usingPoolProvider) {
            this.atomPool.clearPool();
            this.bondPool.clearPool();
            this.molPool.clearPool();
        }
    }

    private int getChainBondSize(IMolecule currentChain) {
        int carbon2carbonBonds = 0;
        for (IBond bond : currentChain.bonds()) {
            if(bond.getAtom(0).getSymbol().equals("C") && bond.getAtom(1).getSymbol().equals("C"))
                carbon2carbonBonds++;
        }
        return carbon2carbonBonds;
    }
}
