/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.counter;

/**
 *
 * @author pmoreno
 */
public interface BinaryCounter {

    /**
     * Returns a String representation of the internal boolean counter, in the form of a binary number written as a string.
     *
     * @return
     */
    String binaryAsString();

    /**
     * Returns the counter in the form of a boolean array.
     *
     * @return boolean array for the counter in the current position.
     */
    boolean[] getCounter();

    boolean hasNext();

    /**
     * Advances the internal counter and returns the next value as a Long.
     * @return
     */
    Long nextBinaryAsLong();

    /**
     * @param counter the counter to set
     */
    void setCounter(boolean[] counter);
    
}
