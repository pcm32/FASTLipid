/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.counter;

/**
 * Extension of {@link BooleanRBCounter} which uses the rules given to traverse the space, in a branch & bound manner
 * (by neglecting intermediate results that do not observe the rules). Only feasible solutions are visited.
 * 
 * @author pmoreno
 */
public class BooleanRBCounterRuleBased extends BooleanRBCounter {

    private int shiftBuffer = 0;
    private int shiftedAmount = 0;
    private boolean firstRun = false;
    private final BooleanRBCounterStartSeeder seeder;

    public BooleanRBCounterRuleBased(Integer binaryPositions, Integer bitCount, BooleanRBCounterStartSeeder seeder) {
        super(binaryPositions, bitCount);
        this.seeder = seeder;
        this.firstRun = true;          
    }

    @Override
    protected void flipBit(int i) {
        super.flipBit(i);
    }

    @Override
    protected boolean testBit(int i) {
        return super.testBit(i);
    }

    @Override
    protected void setBit(int i) {
        super.setBit(i);
    }

    private int getPreviousOne(int pos_i) {
        int previous_one = -1;
        for (int i = pos_i - 1; i >= 0; i--) {
            if (this.testBit(i)) {
                previous_one = i;
                break;
            }
        }
        return previous_one;
    }

    private int getNextOne(int pos_i) {
        int next_one = -1;
        for (int i = pos_i + 1; i < super.getCounter().length; i++) {
            if (this.testBit(i)) {
                next_one = i;
                break;
            }
        }
        return next_one;
    }

    private void shiftCounterOneStep() {
        boolean[] counter = super.getCounter();
        for (int i = counter.length - 1; i > 0; i--) {
            counter[i] = counter[i - 1];
        }
    }

    private void unShiftCounterOneStep() {
        boolean[] counter = super.getCounter();
        for (int i = 0; i < counter.length - 1; i++) {
            counter[i] = counter[i + 1];
        }
        counter[counter.length - 1] = false;
    }

    @Override
    public Long nextBinaryAsLong() {
        
        if (this.getCounter() == null)
            throw new NullPointerException("Counter has not been set, use '.setCounter()'");            
        
        Long res = super.longValue();
        // Find leftmost 1
        if (this.firstRun) {
            int counterShiftableSpace = counterShiftableSpace();
            if (counterShiftableSpace > 0) {
                this.shiftBuffer = Math.min(counterShiftableSpace, seeder.maxMovableDistance() - 1);
                this.firstRun = false;
            }
        }
        if (this.shiftBuffer > 0) {
            this.shiftCounterOneStep();
            this.shiftBuffer--;
            this.shiftedAmount++;
            return res;
        } else if (this.shiftBuffer == 0 && this.shiftedAmount > 0) {
            while (this.shiftedAmount > 0) {
                this.unShiftCounterOneStep();
                this.shiftedAmount--;
            }
        }

        int leftMost_pos = -1;
        for (int i = super.getCounter().length - 1; i >= 0; i--) //for(int i=0;i<this.counter.length;i++)
        {
            if (this.getCounter()[i]) {
                leftMost_pos = i;
                break;
            }
        }
        if (leftMost_pos == -1) {
            return null;
        }
        this.next = this.callMovementRecursion(leftMost_pos);
        int counterShiftableSpace = counterShiftableSpace();
        if (counterShiftableSpace > 0) {
            this.shiftBuffer = Math.min(counterShiftableSpace, seeder.maxMovableDistance() - 1);
        }
        return res;
    }

    private int counterShiftableSpace() {
        int biggerIndex = -1;
        for (int i = this.getCounter().length - 1; i > 0; i--) {
            if (super.getCounter()[i]) {
                biggerIndex = i;
                break;
            }
        }
        if (biggerIndex > 0 && biggerIndex < this.getCounter().length - 1) {
            return this.getCounter().length - 1 - biggerIndex;
        }
        return 0;
    }

    // Try to move bit in pos_i to pos_i+distance (to the left one)
    // distance is determined by rules
    @Override
    protected boolean stepType1(int pos_i) {
        //char[] binary = this.getBinaryRep();
        int previous_one = getPreviousOne(pos_i);
        int next_one = getNextOne(pos_i);

        int nextPos = seeder.nextStepTypeIGivenContext(previous_one, pos_i, next_one, super.getCounter().length);
        if (nextPos < 0) // this will mean that no suitable position could be found.
        {
            return false;
        }
        if (nextPos < super.getCounter().length && !testBit(nextPos)) {
            //counter = counter.setBit(pos_i+1);
            this.setBit(nextPos);
            //counter = counter.flipBit(pos_i);
            this.flipBit(pos_i);
            return true;
        }
        return false;
    }

    // Move position pos_i to righmost possible (boundary and no bit set)
    // this righmost possible is jointly determined with rules
    @Override
    protected boolean stepTyp2(int pos_i) {
        int previous_one = getPreviousOne(pos_i);
        int next_one = getNextOne(pos_i);

        boolean moved = false;
        // here we need to move to the beginning of the array as long
        // as the context is accepted.
        // When we hit a point where conditions are not accepted, we return the
        // last position obtained that was compliant.
        int previously_accepted = -1;
        int initial_position = pos_i;
        int minimalPos = seeder.getInitialPosition(super.getCounter().length, super.bitCount);
        while (pos_i - 1 >= minimalPos && !super.getCounter()[pos_i - 1]) {
            //setBit(pos_i-1);
            //flipBit(pos_i);
            if (seeder.acceptContext(previous_one, pos_i - 1, next_one)) {
                previously_accepted = pos_i - 1;
                moved = true;
            }
            pos_i--;
        }
        if (moved) {
            setBit(previously_accepted);
            flipBit(initial_position);
        }
        return moved;
    }
}
