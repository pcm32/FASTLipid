/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package structure.rule;

/**
 *
 * @author pmoreno
 */
public abstract class BondRule {
    public abstract boolean isCompliantWithRule(char[] binaryRep);
    public abstract boolean isCompliantWithRule(boolean[] counter);
    public abstract boolean[] leftMostValue(int positions, int on, int initialSpace);
    public abstract int firstUsedPosition();

    protected int shiftingDistance=1;

    /*
     * This method should provide the next (towards the end of the array)
     * closest position to the current position so that it is compliant with
     * the rule.
     */
    public abstract int nextStepTypeIGivenContext(int previous_one, int current_pos, int next_one, int total_size);

    public abstract boolean acceptContext(int previous_one, int current_pos, int next_one);

    public int getShiftingDistance() {
        return shiftingDistance;
    }
}
