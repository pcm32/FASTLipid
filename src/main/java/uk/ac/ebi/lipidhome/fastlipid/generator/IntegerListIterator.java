/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.generator;

import java.util.List;

/**
 *
 * @author pmoreno
 */
public interface IntegerListIterator {

    /**
     * The iteration has two parts. The first part changes the amount of tokens from one counter to the next by the
     * specified step size, conserving always the number of tokens. Every time this operation takes place, the signature
     * set is generated, and compared to the existing signatures. If the signature does not exist, then the current
     * config is iterated on its possible orders using the combinatoricsLib. The combinatoricsLib generates some undesired
     * repetitions, which are filtered out.
     *
     * This method computes the next element to be given, so calling it twice without a call to next() will lose one generated
     * element.
     *
     * @return true if there is a next.
     */
    boolean hasNext();

    void initialize(Integer maxToShare, Integer minPerSlot);

    List<Integer> next();
    
}
