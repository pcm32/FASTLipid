/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package counter;

/**
 * Counter based on a boolean array. 
 *
 * @author pmoreno
 */
public class BooleanRBCounter {

    private boolean[] counter;
    protected Integer bitCount;
    protected boolean next;

    /**
     * Initializes a new counter with a defined number of binary positions (size of the boolean[]) and the number of 
     * positions that can be true (these are set at the beginning of the array).
     * 
     * @param binaryPositions
     * @param bitCount 
     */
    public BooleanRBCounter(Integer binaryPositions, Integer bitCount) {
        this.counter = new boolean[binaryPositions];
        for (int i = 0; i < this.counter.length; i++) {
            this.counter[i] = false;
        }
        this.bitCount = bitCount;
        if (binaryPositions > bitCount) {
            this.next = true;
        } else {
            this.next = false;
        }        

    }
    
    /**
     * Returns the counter in the form of a boolean array.
     * 
     * @return boolean array for the counter in the current position. 
     */
    public boolean[] getBooleanArrayCounter() {
        return this.counter;
    }

    /**
     * Changes the value of position i of the boolean array.
     * 
     * @param i 
     */
    protected void flipBit(int i) {
        this.counter[i] = !this.counter[i];
    }

    /**
     * Returns the boolean value in position i
     * 
     * @param i
     * @return 
     */
    protected boolean testBit(int i) {
        return this.counter[i];
    }

    /**
     * Sets the position i of the underlying boolean array to true.
     * 
     * @param i 
     */
    protected void setBit(int i) {
        this.counter[i] = true;
    }

    /**
     * Try to move bit in pos_i to pos_i+1 (to the left one)
     * 
     * @param pos_i
     * @return 
     */
    protected boolean stepType1(int pos_i) {
        if (pos_i + 1 < counter.length && !testBit(pos_i + 1)) {
            this.setBit(pos_i + 1);
            this.flipBit(pos_i);
            return true;
        }
        return false;
    }

    /**
     * Move position pos_i to right-most possible (boundary and no bit set).
     * 
     * @param pos_i
     * @return 
     */
    protected boolean stepTyp2(int pos_i) {
        // TODO improve implementation so that not all positions are flipped, but only the initial and final one, if possible.
        boolean moved = false;
        while (pos_i - 1 >= 0 && !counter[pos_i - 1]) {
            setBit(pos_i - 1);
            flipBit(pos_i);
            pos_i--;
            moved = true;
        }
        return moved;
    }

    /**
     * Returns a String representation of the internal boolean counter, in the form of a binary number written as a string.
     * 
     * @return 
     */
    public String binaryAsString() {
        String count = "";
        // TODO in outer classes this string is reversed, it could well be written in reverse mode here instead of
        // reversing later.
        for (int i = 0; i < this.counter.length; i++) {
            if (this.counter[i]) {
                count += "1";
            } else {
                count += "0";
            }
        }
        return count;
    }

    /**
     * Advances the internal counter and returns the next value as a Long.
     * @return 
     */
    public Long nextBinaryAsLong() {
        Long res = longValue();
        // Find leftmost 1
        int leftMost_pos = -1;
        for (int i = this.counter.length - 1; i >= 0; i--) 
        {
            if (this.counter[i]) {
                leftMost_pos = i;
                break;
            }
        }
        if (leftMost_pos == -1) {
            return null;
        }
        this.next = this.callMovementRecursion(leftMost_pos);
        return res;
    }

    /**
     * Main recursion method, producing a single movement. 
     * 
     * @param pos_i
     * @return 
     */
    protected boolean callMovementRecursion(int pos_i) {
        // TODO This method should have default privacy, not protected.
        if (this.stepType1(pos_i)) {
            return true;
        } else {
            int nextBitSetRight = this.nextBitSetToTheRight(pos_i);
            if (nextBitSetRight < 0) {
                // We have nothing more to move
                return false;
            } else {
                if (!this.callMovementRecursion(nextBitSetRight)) {
                    return false;
                }
                this.stepTyp2(pos_i);
                return true;
            }
        }
    }

    protected int nextBitSetToTheRight(int pos_i) {
        for (int i = pos_i - 1; i > -1; i--) {
            if (this.counter[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Produces the current counter position as a Long value. Unsigned Java long should do until 2^64 - 1, so our limit 
     * is a counter with less than 64 positions.
     * 
     * @return Long representation of current counter position. 
     */
    protected Long longValue() {
        Long val = new Long(0);
        for (int i = 0; i < this.counter.length; i++) {
            if (this.counter[i]) 
            {
                val += (long) Math.pow(2.0, i);
            }
        }
        return val;
    }

    public boolean hasNext() {
        return this.next;
    }

    /**
     * @return the counter
     */
    public boolean[] getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    public void setCounter(boolean[] counter) {
        this.counter = counter;
    }
}
