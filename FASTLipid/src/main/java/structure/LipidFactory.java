/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package structure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import structure.rule.BondDistance3nPlus2Rule;
import structure.rule.NoDoubleBondsTogetherRule;
import structure.rule.StarterDoubleBondRule;

/**
 * Given a head and chain constraints (number of double bonds and carbons), generates lipids on demand when the nextLipid
 * method is called. The lipid is represented as an ChemInfoContainer object, which holds information about the lipid that
 * is calculated while the CDK molecule is retained in memory. The properties/fields that can be calculated are set through
 * the ChemInfoContainerGenerator object.
 * 
 * @author pmoreno
 */
public class LipidFactory {

    private IAtomContainer head;
    private List<IAtom> radicalAnchors;
    private List<IAtom> originalHeadAtoms;
    private List<IBond> currentRadicalBonds;
    private List<ChainFactory> chainFactories;
    private List<IAtom> atomsToRemove = new ArrayList<IAtom>();
    private List<IBond> bondsToRemove = new ArrayList<IBond>();
    private IChemObjectBuilder builder;
    private int currentIteratingChainFactory;
    private boolean firstSet;
    private ExecutorService exec;
    private boolean threaded;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;
    
    private List<ChainInfoContainer> chainsInfo;

    public LipidFactory(boolean threaded) {
        this.builder = SilentChemObjectBuilder.getInstance();
        this.currentIteratingChainFactory = 0;
        this.firstSet = false;
        this.threaded = threaded;
        if(this.threaded)
            this.exec = Executors.newFixedThreadPool(2);
    }

    public void setChemInfoContainerGenerator(ChemInfoContainerGenerator chemInfoContainerGenerator) {
        this.chemInfoContainerGenerator = chemInfoContainerGenerator;
    }

    public void setHead(IAtomContainer head) {
        this.head = head;
        this.radicalAnchors = new ArrayList<IAtom>();
        this.currentRadicalBonds = new ArrayList<IBond>();
        this.chainFactories = new ArrayList<ChainFactory>();
        this.originalHeadAtoms = new ArrayList<IAtom>(this.head.getAtomCount());
        //System.out.println("Head:"+sg.createSMILES(head));
        for(IAtom atom : this.head.atoms()) {
            this.originalHeadAtoms.add(atom);
        }
    }

    public IAtom addRadicalAnchor(IAtom radical) {
        if (this.head.contains(radical)) {
            //this.radicals.add(radical);
            for(IBond radicalBond : this.head.getConnectedBondsList(radical)) {
                if(this.head.contains(radicalBond)) {
                    IAtom conToRad = radicalBond.getConnectedAtom(radical);
                    this.radicalAnchors.add(conToRad);
                    this.head.removeBond(radicalBond);
                    this.head.removeAtom(radical);
                    return conToRad;
                }
            }
        }
        return null;
    }

    /**
     * Use the list method instead, {@link #resetMultipleChainFactories(java.util.List) }
     * 
     * @param cfA
     * @param cfB
     * @deprecated
     */
    @Deprecated
    public void resetChainFactories(ChainFactory cfA, ChainFactory cfB) {
        this.currentIteratingChainFactory=0;
        this.chainFactories.clear();
        this.chainFactories.add(cfA);
        this.chainFactories.add(cfB);
        this.firstSet=false;
    }

    public void restoreRadicalAtom(IAtom radical, IAtom conToRadical) {
        if(this.head.contains(conToRadical)) {
            IBond radicalBond = builder.newInstance(Bond.class);
            radicalBond.setAtom(radical, 0);
            radicalBond.setAtom(conToRadical, 1);
            //IBond radicalBond = builder.newBond(radical, conToRadical);
            this.head.addBond(radicalBond);
            this.head.addAtom(radical);
        }
    }

    /**
     * Adds a new chain factory to the lipid factory object, setting its chain iterator to the specified minimal and 
     * maximal number of carbons and double bonds.
     * 
     * @param minCarbons
     * @param maxCarbons
     * @param minUnsatBonds
     * @param maxUnsatBonds 
     */
    public void addChainFactoryWithConstraints(int minCarbons, int maxCarbons, int minUnsatBonds, int maxUnsatBonds) {
        ChainFactory cf = new ChainFactory();
        if(this.threaded)
          cf = new ThreadedChainFactory();
        cf.setChainIterator(minCarbons, maxCarbons, minUnsatBonds, maxUnsatBonds);
        cf.addAlwaysRule(new BondDistance3nPlus2Rule());
        cf.addAlwaysRule(new NoDoubleBondsTogetherRule());
        cf.addAlwaysRule(new StarterDoubleBondRule(2));
        if(cf instanceof ThreadedChainFactory)
            this.exec.execute((ThreadedChainFactory)cf);
        this.chainFactories.add(cf);
    }

    /*
     * This method tries to do everything on the same molecule,
     * but doesn't guarantee to leave the head as it was originally by the end of the iterations
     */
    /*public ChemInfoContainer nextLipidFast() {
        if (!this.firstSet) {
            // first run
            for(int i=0;i<this.chainFactories.size();i++) {
                IMolecule chain = this.chainFactories.get(i).nextChain();
                if(chain!=null) {
                    //IBond chain2head = builder.newInstance(Bond.class);
                    //chain2head.setAtom(chain.getFirstAtom(), 0);
                    //chain2head.setAtom(this.radicalAnchors.get(i), 1);
                    IBond chain2head = builder.newBond(chain.getFirstAtom(), this.radicalAnchors.get(i));
                    this.currentRadicalBonds.add(chain2head);
                    this.head.add(chain);
                    this.head.addBond(chain2head);
                }
            }
            this.firstSet=true;
            return this.getCDKObjectAsChemInfoObj(head);
        } else {
            //IMolecule currentChain = chainFactories.get(this.currentIteratingChainFactory).getCurrentChain();
            char[] currentCharChain = chainFactories.get(this.currentIteratingChainFactory).getCurrentCharChain();
            //this.head.remove(currentChain);
            //this.head.removeBond(this.currentRadicalBonds.get(this.currentIteratingChainFactory));
            //IMolecule nextChain = chainFactories.get(this.currentIteratingChainFactory).nextChain();
            char[] nextCharChain = chainFactories.get(this.currentIteratingChainFactory).nextCharChain();
            if (nextCharChain != null) {
                //this.printChain(nextChain);
                //IBond bondChain2Head = builder.newBond(nextChain.getFirstAtom(), this.radicalAnchors.get(this.currentIteratingChainFactory));
                //this.currentRadicalBonds.set(this.currentIteratingChainFactory, bondChain2Head);
                //this.head.add(nextChain);
                //this.head.addBond(bondChain2Head);
                this.changeChain(currentCharChain, nextCharChain, this.radicalAnchors.get(currentIteratingChainFactory));
                return this.getCDKObjectAsChemInfoObj(head);
            } else {
                // no more chains in the current iterating chain factory (inner loop)
                char[] currentOuterLoopCharChain = chainFactories.get(this.currentIteratingChainFactory+1).getCurrentCharChain();
                //IMolecule currentOuterLoopChain = chainFactories.get(this.currentIteratingChainFactory+1).getCurrentChain();
                //this.head.remove(currentOuterLoopChain);
                //this.head.removeBond(this.currentRadicalBonds.get(this.currentIteratingChainFactory+1));
                //IMolecule outerLoopChain = chainFactories.get(this.currentIteratingChainFactory+1).nextChain();
                char[] outerLoopCharChain = chainFactories.get(this.currentIteratingChainFactory+1).nextCharChain();
                if(outerLoopCharChain==null) {
                    // we have done iterating on the 2 molecules;
                    if(chainFactories.get(currentIteratingChainFactory) instanceof ThreadedChainFactory)
                        ((ThreadedChainFactory) chainFactories.get(currentIteratingChainFactory)).stopThread();
                    if(chainFactories.get(currentIteratingChainFactory+1) instanceof ThreadedChainFactory)
                        ((ThreadedChainFactory) chainFactories.get(currentIteratingChainFactory+1)).stopThread();
                    return null;
                }
                else {
                    //this.printChain(outerLoopChain);
                    this.changeChain(currentOuterLoopCharChain, outerLoopCharChain, this.radicalAnchors.get(currentIteratingChainFactory+1));
                    //IBond; bondChain2head = builder.newBond(outerLoopChain.getFirstAtom(), this.radicalAnchors.get(currentIteratingChainFactory+1));
                    //this.currentRadicalBonds.set(currentIteratingChainFactory+1, bondChain2head);
                    //this.head.add(outerLoopChain);
                    //this.head.addBond(bondChain2head);
                    char[] prevInnerLoopCharChain = this.chainFactories.get(currentIteratingChainFactory).getCurrentCharChain();
                    this.chainFactories.get(currentIteratingChainFactory).resetChainIterator();
                    IMolecule innerLoopChain = this.chainFactories.get(currentIteratingChainFactory).nextChain();
                    char[] innerLoopCharChain = this.chainFactories.get(currentIteratingChainFactory).nextCharChain();
                    this.changeChain(prevInnerLoopCharChain, innerLoopCharChain, this.radicalAnchors.get(currentIteratingChainFactory));
                    //this.printChain(innerLoopChain);
                    //bondChain2head = builder.newBond(innerLoopChain.getFirstAtom(), this.radicalAnchors.get(currentIteratingChainFactory));
                    //this.currentRadicalBonds.set(currentIteratingChainFactory, bondChain2head);
                    //this.head.add(innerLoopChain);
                    //this.head.addBond(bondChain2head);
                    return this.getCDKObjectAsChemInfoObj(head);
                }
            }
        }
    }*/

    public ChemInfoContainer nextLipid() {
        if (!this.firstSet) {
            initializeChainInfoContainers();
            for(int i=0;i<this.chainFactories.size();i++) {
                IMolecule chain = this.chainFactories.get(i).nextChain();
                if(chain!=null) {
                    addChainInfoContainerFromChainFactory(this.chainFactories.get(i));
                    IBond chain2head = builder.newInstance(Bond.class);
                    chain2head.setAtom(chain.getFirstAtom(), 0);
                    chain2head.setAtom(this.radicalAnchors.get(i), 1);
                    chain2head.setOrder(IBond.Order.SINGLE);
                    //IBond chain2head = builder.newBond(chain.getFirstAtom(), this.radicalAnchors.get(i));
                    this.currentRadicalBonds.add(chain2head);
                    this.head.add(chain);
                    this.head.addBond(chain2head);
                } else {
                    // one of the chain factories is not producing any results
                    // iterate over the previous ones and remove what ever was inserted into head.
                    for(int j=0;j<i;j++) {
                        IMolecule currentChain = chainFactories.get(j).getCurrentChain();
                        if(currentChain!=null) {
                            this.head.remove(currentChain);
                            this.head.removeBond(this.currentRadicalBonds.get(j));
                        }
                    }
                    clearChainInfoContainers();
                    return null;
                }
            }
            this.firstSet=true;
            ChemInfoContainer cont = this.getCDKObjectAsChemInfoObj(head);
            if(cont!=null)
                cont.setChainsInfo(chainsInfo);
            return cont;
        } else {
            IMolecule currentChain = chainFactories.get(this.currentIteratingChainFactory).getCurrentChain();
            this.head.remove(currentChain);
            this.head.removeBond(this.currentRadicalBonds.get(this.currentIteratingChainFactory));
            IMolecule nextChain = chainFactories.get(this.currentIteratingChainFactory).nextChain();
            if (nextChain != null) {
                replaceChainInfoContainerIndexFromFactory(this.currentIteratingChainFactory,chainFactories.get(currentIteratingChainFactory));
                IBond bondChain2Head = builder.newInstance(Bond.class);
                bondChain2Head.setAtom(nextChain.getFirstAtom(), 0);
                bondChain2Head.setAtom(this.radicalAnchors.get(this.currentIteratingChainFactory), 1);
                bondChain2Head.setOrder(IBond.Order.SINGLE);
                //IBond bondChain2Head = builder.newBond(nextChain.getFirstAtom(), this.radicalAnchors.get(this.currentIteratingChainFactory));
                this.currentRadicalBonds.set(this.currentIteratingChainFactory, bondChain2Head);
                this.head.add(nextChain);
                this.head.addBond(bondChain2Head);
                ChemInfoContainer cont = this.getCDKObjectAsChemInfoObj(head);
                cont.setChainsInfo(chainsInfo);
                return cont;
            } else {
                
                Integer indexOfMaxFactoryToUse=null;
                Boolean allChainFactoriesDone=true;
                for(int i=currentIteratingChainFactory+1;i<chainFactories.size();i++) {
                    IMolecule currentoOuterLoopChain = chainFactories.get(i).getCurrentChain();
                    this.head.remove(currentoOuterLoopChain);
                    this.head.removeBond(this.currentRadicalBonds.get(i));
                    IMolecule outerLoopChain = chainFactories.get(i).nextChain();
                    if(outerLoopChain!=null) {
                        indexOfMaxFactoryToUse=i;
                        allChainFactoriesDone=false;
                        break;
                    }
                }
                
                if(allChainFactoriesDone) {
                    for (ChainFactory chainFactory : chainFactories) {
                        chainFactory.cleanUp();
                        if(chainFactory instanceof ThreadedChainFactory)
                            ((ThreadedChainFactory) chainFactory).stopThread();
                    }
                    return null;
                }
                else {
                    /**
                     * We replace the chain and chainInfo corresponding to the outer loop
                     * 
                     * Add the current chain from the following chain factory.
                     */
                    addChainToHead(indexOfMaxFactoryToUse, true);
                    
                    for(int i=indexOfMaxFactoryToUse-1;i>=currentIteratingChainFactory;i--) {
                        this.chainFactories.get(i).resetChainIterator();
                        addChainToHead(i, false);
                    }
                    /**
                     * Add the next chain from the current chain factory, which was just resetted to start all over.
                     */
                    
                    ChemInfoContainer cont = this.getCDKObjectAsChemInfoObj(head);
                    if(cont!=null && chemInfoContainerGenerator.getGenerateChainInfoContainers())
                        cont.setChainsInfo(chainsInfo);
                    return cont;
                }
            }
        }
    }

    /**
     * Adds the chain from the chainFactory specified by the index to the head. It will either add the current chain in
     * the factory, or the next chain, according to the boolean value "current". This method does not handle the removal
     * of whatever was chained to the head in that position (index).
     * 
     * @param chainIndex
     * @param current
     * @throws IllegalArgumentException 
     */
    private void addChainToHead(Integer chainIndex, Boolean current) throws IllegalArgumentException {
        IBond bondChain2head;
        replaceChainInfoContainerIndexFromFactory(chainIndex, chainFactories.get(chainIndex));
        IMolecule chain;
        if(current)
            chain = chainFactories.get(chainIndex).getCurrentChain();
        else
            chain = chainFactories.get(chainIndex).nextChain();
        //this.printChain(innerLoopChain);
        bondChain2head = builder.newInstance(Bond.class);
        bondChain2head.setAtom(chain.getFirstAtom(), 0);
        bondChain2head.setAtom(this.radicalAnchors.get(chainIndex), 1);
        bondChain2head.setOrder(IBond.Order.SINGLE);
        //bondChain2head = builder.newBond(innerLoopChain.getFirstAtom(), this.radicalAnchors.get(currentIteratingChainFactory));
        this.currentRadicalBonds.set(chainIndex, bondChain2head);
        this.head.add(chain);
        this.head.addBond(bondChain2head);
    }

    private void replaceChainInfoContainerIndexFromFactory(Integer index, ChainFactory fact) {
        //this.printChain(nextChain);
        if(chemInfoContainerGenerator.getGenerateChainInfoContainers())
            chainsInfo.set(index, fact.getCurrentChainAsInfoContainer());
    }

    private void clearChainInfoContainers() {
        if(chemInfoContainerGenerator.getGenerateChainInfoContainers())
            chainsInfo.clear();
    }

    private void addChainInfoContainerFromChainFactory(ChainFactory fact) {
        if(chemInfoContainerGenerator.getGenerateChainInfoContainers())
            chainsInfo.add(fact.getCurrentChainAsInfoContainer());
    }

    private void initializeChainInfoContainers() {
        // first run
        // if(chemInfoContainerGenerator.getGenerateChainInfoContainers())
            chainsInfo = new ArrayList<ChainInfoContainer>(chainFactories.size());
    }

    private ChemInfoContainer getCDKObjectAsChemInfoObj(IAtomContainer mol) {
        return this.chemInfoContainerGenerator.generateChemInfoContainer(mol);
    }

    /*
     * Steps into this radical anchor and modifies the chain lying
     * here in terms of double bonds and number of carbons.
     */
    private void changeChain(char[] oldChain, char[] newChain, IAtom anchor) {
        IAtom chain1stCarbon = null;
        for(IAtom conAtom : this.head.getConnectedAtomsList(anchor)) {
            if(!this.originalHeadAtoms.contains(conAtom)) {
                chain1stCarbon = conAtom;
                break;
            }
        }
        IAtom previous = anchor;
        int bondCounter=0;
        //int atomCounter=0;
        IAtom current = chain1stCarbon;
        //this.atomsToRemove.clear();
        //this.bondsToRemove.clear();
        while(current!=null) {
            if(!previous.equals(anchor)) {
                if(oldChain.length > bondCounter && newChain.length > bondCounter) {
                    if(oldChain[bondCounter] != newChain[bondCounter]) {
                        if(newChain[bondCounter] == '1')
                            this.head.getBond(previous, current).setOrder(IBond.Order.DOUBLE);
                        else
                            this.head.getBond(previous, current).setOrder(IBond.Order.SINGLE);
                    }
                }
                else if(newChain.length <= bondCounter) {
                    // Here we mark the atom and bond for removal
                    this.bondsToRemove.add(this.head.getBond(previous, current));
                    this.atomsToRemove.add(current);
                }
                bondCounter++;
            }

            IAtom next = this.nextCarbonInChain(current, previous);
            previous = current;
            current = next;
        }
        if(this.atomsToRemove.size()>0) {
            for(IAtom atomToRem : this.atomsToRemove)
                this.head.removeAtom(anchor);
            for(IBond bondToRem : this.bondsToRemove)
                this.head.removeBond(bondToRem);
            this.atomsToRemove.clear();
            this.bondsToRemove.clear();
        }
        while(newChain.length > bondCounter) {
            //IAtom carbonAtom = builder.newAtom("C");
            IAtom carbonAtom = builder.newInstance(Atom.class);
            carbonAtom.setSymbol("C");
            IBond newBond = builder.newInstance(Bond.class);
            newBond.setAtom(previous, 0);
            newBond.setAtom(carbonAtom, 1);
            //IBond newBond = builder.newBond(previous, carbonAtom);
            if(newChain[bondCounter]=='1')
                newBond.setOrder(IBond.Order.DOUBLE);
            this.head.addAtom(carbonAtom);
            this.head.addBond(newBond);
            bondCounter++;
        }
    }

    private IAtom nextCarbonInChain(IAtom current, IAtom previous) {
        for(IAtom conAtom : this.head.getConnectedAtomsList(current))
            if(!conAtom.equals(previous))
                return conAtom;
        return null;
    }

    private void printChain(IMolecule mol) {
        String res = "C";
        for(IBond bond : mol.bonds()) {
            if(bond.getOrder().equals(IBond.Order.SINGLE))
                res+="-C";
            else
                res+="=C";
        }
        System.out.println(res);
    }

    public void killThreads() {
        for(ChainFactory cf : this.chainFactories) {
            if(cf instanceof ThreadedChainFactory)
                ((ThreadedChainFactory)cf).stopThread();
        }
    }

    public void resetMultipleChainFactories(List<ChainFactory> chainFactories) {
        this.currentIteratingChainFactory=0;
        this.chainFactories.clear();
        this.chainFactories.addAll(chainFactories);
        this.firstSet=false;
    }
}
