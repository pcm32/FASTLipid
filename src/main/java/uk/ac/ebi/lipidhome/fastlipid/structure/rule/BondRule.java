/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.structure.rule;

/**
 * Definition of rules for bonds.
 * 
 * @author pmoreno
 */
public abstract class BondRule {
    
    /**
     * Checks whether the given binary representation in char[] given is compliant with the rule.
     * 
     * @param binaryRep to be checked with the rule.
     * @return true if compliant. 
     */
    public abstract boolean isCompliantWithRule(char[] binaryRep);
    /**
     * Checks whether the given binary representation in boolean[] given is compliant with the rule.
     * 
     * @param counter
     * @return 
     */
    public abstract boolean isCompliantWithRule(boolean[] counter);
    
    
    /**
     * Obtains the leftmost binary representation acceptable by the rule, given the number of positions (length), number
     * of positions turned on, and a number of initial positions to the left set to zero.
     * 
     * @param positions the length of the binary representation.
     * @param on number of positions turned on in the binary representation.
     * @param initialSpace to the left that should be turned off (bits turned off to the left).
     * @return boolean[] with the leftmost representation accepted by the rule.
     */
    public abstract boolean[] leftMostValue(int positions, int on, int initialSpace);
    
    /**
     * 
     * 
     * @return  The first position (index) of the binary counter that can be used (turned on) according to the rule.
     */
    public abstract int firstUsedPosition();

    protected int shiftingDistance=1;
    
    /**
     * Provides the next (towards the end of the array) closest position to the current position so that it is 
     * compliant with the rule.
     * 
     * @param previous_one the index of the previous bit set.
     * @param current_pos the index of the current bit set that we try to modify.
     * @param next_one the index of the next bit set.
     * @param total_size total number of bits in the binary counter.
     * @return the new index for the current bit.
     */
    public abstract int nextStepTypeIGivenContext(int previous_one, int current_pos, int next_one, int total_size);

    /**
     * Returns true if the context given in terms of indexes (previous, current, next) is acceptable to the rule.
     * 
     * @param previous_one
     * @param current_pos
     * @param next_one
     * @return 
     */
    public abstract boolean acceptContext(int previous_one, int current_pos, int next_one);

    /**
     * The shifting distance is the number of positions that the current bit is moved in the binary rep, 
     * when advancing steps.
     * 
     * @return the shifting distance.
     */
    public int getShiftingDistance() {
        return shiftingDistance;
    }
}
