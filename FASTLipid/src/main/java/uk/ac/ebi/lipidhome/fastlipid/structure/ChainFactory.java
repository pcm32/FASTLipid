/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.structure;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounter;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterRuleBased;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pmoreno
 */
public class ChainFactory {

    private IChemObjectBuilder builder;
    private IAtomContainer currentChain;
    private char[] currentCharChain;
    private int maxCarbons;
    private int minCarbons;
    private int currentCarbons;
    private int carbonNumberIncrement;
    private int maxUnsatBonds;
    private int minUnsatBonds;
    private int currentUnsatBonds;
    private int currentShiftsCount;
    private long binaryCounter;
    private long maxBinaryCounterForBondLengthAndDoubleBondNum;
    //private boolean[] doubleBondBinaryCounter;
    //private RecursiveBinaryCounter realbinaryCounter;
    private BooleanRBCounter realbinaryCounter;
    private boolean iteratorHasNext;
    private List<BondRule> alwaysRules;
    private List<BondRule> anyRules;
    private boolean useRuleBasedBooleanCounter;
    private BooleanRBCounterStartSeeder seeder;
    SingleLinkConfiguration linkConf;
    LinkerHandler linkerHandler;

    public static ChainFactory getInstance() {
        return new ChainFactory();
    }

    public ChainFactory() {
        this.builder = SilentChemObjectBuilder.getInstance();
        this.alwaysRules = new ArrayList<BondRule>();
        this.anyRules = new ArrayList<BondRule>();
        this.linkConf = SingleLinkConfiguration.Acyl;
    }

    public boolean couldNextExist() {
        return iteratorHasNext;
    }

    public char[] getCharChain(int bondsNumber) {
        char[] chain = new char[bondsNumber];
        for (int i = 0; i < bondsNumber; i++) {
            chain[i] = '0';
        }
        return chain;
    }

    /**
     * Produces a linear chain of carbon atoms with the given number of conecting bonds (so it has bondsNumber+1 carbons).
     * This is meant to be the main structure of a fatty acid.
     * 
     * @param bondsNumber
     * @return IAtomContainer with a linear chain of carbon atoms.
     */
    public IAtomContainer getChain(int bondsNumber) {
        IAtomContainer chain = builder.newInstance(AtomContainer.class);
        IBond previousBond = null;
        for (int i = 0; i < bondsNumber; i++) {
            IBond bond;
            if (previousBond == null) {
                //IAtom c1 = builder.newAtom("C");
                IAtom c1 = builder.newInstance(Atom.class);
                c1.setSymbol("C");
                //IAtom c2 = builder.newAtom("C");
                IAtom c2 = builder.newInstance(Atom.class);
                c2.setSymbol("C");
                bond = builder.newInstance(Bond.class);
                bond.setAtom(c1, 0);
                bond.setAtom(c2, 1);
                //bond = builder.newBond(c1, c2);
            } else {
                bond = builder.newInstance(Bond.class);
                bond.setAtom(previousBond.getAtom(1), 0);
                IAtom c1 = builder.newInstance(Atom.class);
                c1.setSymbol("C");
                bond.setAtom(c1, 1);
                //bond = builder.newBond(previousBond.getAtom(1), builder.newAtom("C"));
            }
            chain.addBond(bond);
            for (IAtom atom : bond.atoms()) {
                chain.addAtom(atom);
            }
            previousBond = bond;
        }
        return chain;
    }

    public void setChainIterator(int minCarbonAtoms, int maxCarbonAtoms, int minUnsatBonds, int maxUnsatBonds) {
        this.minCarbons = minCarbonAtoms;
        this.maxCarbons = maxCarbonAtoms;
        this.minUnsatBonds = minUnsatBonds;
        this.maxUnsatBonds = maxUnsatBonds;
        this.carbonNumberIncrement = 2;

        this.currentCarbons = this.minCarbons;
        this.currentUnsatBonds = this.minUnsatBonds;
        this.binaryCounter = 0;
        this.currentShiftsCount = 0;
        this.iteratorHasNext = true;
        //this.realbinaryCounter = new RecursiveBinaryCounter(this.currentCarbons-1, this.currentUnsatBonds);
        //this.realbinaryCounter = new BooleanRBCounter(this.currentCarbons-1, this.currentUnsatBonds);
        this.realbinaryCounter = this.getNewBooleanRBCounter(this.currentCarbons - 1, this.currentUnsatBonds);
        if (this.realbinaryCounter.getCounter() == null) {
            this.iteratorHasNext = false;
            // TODO not very elegant.
            //System.out.println("We are in config in which the counter is null:");
            //System.out.println("Bonds:" + (this.currentCarbons - 1));
            //System.out.println("Double:" + this.currentUnsatBonds);
        }
        this.calculateMaxBinaryCounterForBondNumberAndDoubleBonds();
    }

    public IAtomContainer nextChain() {
        if (!this.iteratorHasNext) {
            return null;
        }
        IAtomContainer tmp = this.getChain(this.currentCarbons - 1);
        boolean molProd = false;
        while (!molProd) {
            if (this.currentCarbons > this.maxCarbons) {
                return null;
            }
            if (this.currentUnsatBonds > 0) {
                boolean binaryCounterGotToTheTop = false;
                while (Long.bitCount(this.binaryCounter) != this.currentUnsatBonds 
                        || !this.complyWithRules(this.realbinaryCounter.getCounter())) {
                    if (this.realbinaryCounter.hasNext()) {
                        this.binaryCounter = this.realbinaryCounter.nextBinaryAsLong();
                    } else {
                        binaryCounterGotToTheTop = true;
                        break;
                    }
                }

                // This char[] holds the positions for the double bonds.
                // This is legacy of the initial versions, probably the
                // String operations here are causing a burden, although it
                // doesn't look like from the profiler.
                // This is currently very fragile, as we could be modifiying the bond of the linker carbon. This currently
                // works fine only because the linker's bond is added at the end of the chain building process.
                char[] doubleBondPos = new StringBuffer(Long.toBinaryString(binaryCounter)).reverse().toString().toCharArray();
                int bondCounter = 0;
                if (doubleBondPos.length <= tmp.getBondCount() && !binaryCounterGotToTheTop) {
                    for (IBond b : tmp.bonds()) {
                        if (bondCounter == doubleBondPos.length) {
                            break;
                        }
                        if (doubleBondPos[bondCounter] == '1') {
                            b.setOrder(IBond.Order.DOUBLE);
                        } else if (doubleBondPos[bondCounter] == '0') {
                            b.setOrder(IBond.Order.SINGLE);
                        }
                        bondCounter++;
                    }

                    this.binaryCounter++;
                    //this.binaryCounter = this.realbinaryCounter.nextBinaryAsLong();
                    molProd = true;
                    if (this.binaryCounter >= this.maxBinaryCounterForBondLengthAndDoubleBondNum) {
                        // We have reached all the combinations for the current state of
                        // bond length + double bond numbers. We move on to the next number
                        // of double bonds
                        //System.out.println("Augmenting double bonds:" + this.currentUnsatBonds);
                        this.currentUnsatBonds++;
                        //System.out.println("New double bonds:" + this.currentUnsatBonds);
                        //this.binaryCounter = 0;
                        //this.realbinaryCounter = new RecursiveBinaryCounter(this.currentCarbons-1, this.currentUnsatBonds);
                        //this.realbinaryCounter = new BooleanRBCounter(this.currentCarbons-1, this.currentUnsatBonds);
                        this.realbinaryCounter = this.getNewBooleanRBCounter(this.currentCarbons - 1, this.currentUnsatBonds);
                    }
                    if (this.currentUnsatBonds > this.maxUnsatBonds) {
                        // We have reached the total number ot double bonds for this carbon
                        // length. We move on to the next number of carbons.
                        // this.currentUnsatBonds = this.minUnsatBonds;
                        // this.currentCarbons += this.carbonNumberIncrement;
                        this.increaseCarbonAtomsSetMinUnsatsResetBinCounter();
                    }
                } else {
                    // If we are here is because the binary counter got to a place
                    // where more bonds are being considered than possible within
                    //System.out.println("Augmenting double bonds:" + this.currentUnsatBonds);
                    this.currentUnsatBonds++;
                    //System.out.println("New double bonds:" + this.currentUnsatBonds);
                    //this.binaryCounter = 0;
                    //this.realbinaryCounter = new RecursiveBinaryCounter(this.currentCarbons-1, this.currentUnsatBonds);
                    // Maybe it would be a good idea to reset the counter here, instead of creating a new one.
                    //this.realbinaryCounter = new BooleanRBCounter(this.currentCarbons-1, this.currentUnsatBonds);
                    this.realbinaryCounter = this.getNewBooleanRBCounter(this.currentCarbons - 1, this.currentUnsatBonds);
                    if (this.currentUnsatBonds > this.maxUnsatBonds) {
                        // the current amount of bonds. We need to increase carbon atoms,
                        // set single bonds to min and binary counter = 0
                        //System.out.println("Changing due to binary counter being bigger than carbon atoms");
                        //System.out.println("Current carbon:" + this.currentCarbons);
                        //System.out.println("Current unsat:" + this.currentUnsatBonds);
                        //System.out.println("Current bin counter:" + this.binaryCounter);
                        molProd = false;
                        this.increaseCarbonAtomsSetMinUnsatsResetBinCounter();
                        tmp = this.getChain(this.currentCarbons - 1);
                        //System.out.println("New carbon:" + this.currentCarbons);
                        //System.out.println("New unsat:" + this.currentUnsatBonds);
                    }
                }
            } else {
                // This is the easy case with no double bonds.
                molProd = true;
                if (this.maxUnsatBonds > this.currentUnsatBonds) {
                    this.currentUnsatBonds++;
                    //this.realbinaryCounter = new RecursiveBinaryCounter(this.currentCarbons-1, this.currentUnsatBonds);
                    //this.realbinaryCounter = new BooleanRBCounter(this.currentCarbons-1, this.currentUnsatBonds);
                    // We move this out of the if
                } else {
                    this.currentCarbons += this.carbonNumberIncrement;
                    //this.realbinaryCounter = new RecursiveBinaryCounter(this.currentCarbons-1, this.currentUnsatBonds);
                    //this.realbinaryCounter = new BooleanRBCounter(this.currentCarbons-1, this.currentUnsatBonds);
                }
                this.realbinaryCounter = this.getNewBooleanRBCounter(this.currentCarbons - 1, this.currentUnsatBonds);
            }

            if (this.currentCarbons > this.maxCarbons) {
                this.iteratorHasNext = false;
            } else {
                this.calculateMaxBinaryCounterForBondNumberAndDoubleBonds();
            }
        }
        this.currentChain = tmp;
        return tmp;
    }

    public IAtomContainer getCurrentChain() {
        return this.currentChain;
    }

    /**
     * Calculates on demand a ChainInfoContainer that represents the current chain as given by this ChainFactory. This 
     * method trusts that the internal current chain has not been modified past the getChain() invocation.
     *
     * @return ChainInfoContainer containing the information of the current chain.
     */
    public ChainInfoContainer getCurrentChainAsInfoContainer() {
        ChainInfoContainer container = new ChainInfoContainer(this.currentChain.getAtomCount());
        int counter = 1;
        for (IBond bond : this.currentChain.bonds()) {
            if (bond.getOrder().equals(IBond.Order.DOUBLE) && counter < this.currentChain.getBondCount()) {
                container.addDoubleBondPos(counter);
            }
            counter++;
        }
        return container;
    }

    protected void setCurrentChain(IAtomContainer chain) {
        this.currentChain = chain;
    }

    public char[] getCurrentCharChain() {
        return this.currentCharChain;
    }

    public BooleanRBCounterStartSeeder getSeeder() {
        return seeder;
    }

    public void setSeeder(BooleanRBCounterStartSeeder seeder) {
        this.seeder = seeder;
    }

    private void calculateMaxBinaryCounterForBondNumberAndDoubleBonds() {
        int doubleBonds = this.currentUnsatBonds;
        int singleBonds = (this.currentCarbons - 1) - doubleBonds;

        if (!this.realbinaryCounter.hasNext() || this.realbinaryCounter.getCounter() == null) {
            this.maxBinaryCounterForBondLengthAndDoubleBondNum = 0;
        } else {

            this.maxBinaryCounterForBondLengthAndDoubleBondNum = 0;
            for (int i = 0; i < singleBonds + doubleBonds; i++) {
                if (i >= singleBonds) {
                    this.maxBinaryCounterForBondLengthAndDoubleBondNum += Math.pow(2, i);
                }
            }
        }
    }
    
    /**
     * Frees resources once the chain factory won't be used any more.
     */
    public void cleanUp() {
    }

    private BooleanRBCounter getNewBooleanRBCounter(int carbons_minus_one, int unsat_bonds) {
        BooleanRBCounter counter = useRuleBasedBooleanCounter
                ? new BooleanRBCounterRuleBased(carbons_minus_one, unsat_bonds, seeder)
                : new BooleanRBCounter(carbons_minus_one, unsat_bonds);
        
        // Sets the initial position of the counter.
        counter.setCounter(seeder.getStartingSeedFor(carbons_minus_one, unsat_bonds));
        return counter;
    }

    private boolean complyWithRules(boolean[] counter) {
        for (BondRule rule : alwaysRules) {
            if (!rule.isCompliantWithRule(counter)) {
                return false;
            }
        }
        if (anyRules.isEmpty()) {
            return true;
        }
        for (BondRule rule : anyRules) {
            if (rule.isCompliantWithRule(counter)) {
                return true;
            }
        }
        return false;
    }

    private boolean complyWithRules(long binCounter) {
        char[] binRep = new StringBuffer(Long.toBinaryString(binCounter)).reverse().toString().toCharArray();
        for (BondRule rule : alwaysRules) {
            if (!rule.isCompliantWithRule(binRep)) {
                return false;
            }
        }
        if (anyRules.isEmpty()) {
            return true;
        }
        for (BondRule rule : anyRules) {
            if (rule.isCompliantWithRule(binRep)) {
                return true;
            }
        }

        return false;
    }

    public void addAlwaysRule(BondRule rule) {
        this.alwaysRules.add(rule);
    }

    public void addAnyRule(BondRule rule) {
        this.anyRules.add(rule);
    }

    private void increaseCarbonAtomsSetMinUnsatsResetBinCounter() {
        this.currentCarbons += this.carbonNumberIncrement;
        this.currentUnsatBonds = this.minUnsatBonds;
        //this.binaryCounter = 0;
        //this.realbinaryCounter = new RecursiveBinaryCounter(this.currentCarbons-1, this.currentUnsatBonds);
        //this.realbinaryCounter = new BooleanRBCounter(this.currentCarbons-1, this.currentUnsatBonds);
        this.realbinaryCounter = this.getNewBooleanRBCounter(this.currentCarbons - 1, this.currentUnsatBonds);
        this.calculateMaxBinaryCounterForBondNumberAndDoubleBonds();
    }

    public void resetChainIterator() {
        setChainIterator(this.minCarbons, this.maxCarbons, this.minUnsatBonds, this.maxUnsatBonds);
    }

    /**
     * @param useRuleBasedBooleanCounter the useRuleBasedBooleanCounter to set
     */
    public void setUseRuleBasedBooleanCounter(boolean useRuleBasedBooleanCounter) {
        this.useRuleBasedBooleanCounter = useRuleBasedBooleanCounter;
    }

    /**
     * @return the linkConf
     */
    public SingleLinkConfiguration getLinkConf() {
        return linkConf;
    }

    /**
     * @param linkConf the linkConf to set
     */
    public void setLinkConf(SingleLinkConfiguration linkConf) {
        this.linkConf = linkConf;
    }
}
