/**
 * AbstractIntegerListIterator.java
 *
 * 2012.09.21
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.lipidhome.fastlipid.generator;


import java.util.*;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.paukov.combinatorics.permutations.PermutationGenerator;

/**
 * @name    AbstractIntegerListIterator
 * @date    2012.09.21
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public abstract class AbstractIntegerListIterator {

    List<Integer> counters;
    Boolean hasNext = false;
    Integer indexToDecrease; // signals where the decrease should be made
    Integer indexToIncrease; // signals where the increase should be made
    List<Integer> iteratedCounters;
    Integer minPerSlot;
    Boolean moveTokensBetweenSlot = false;
    Set<List<Integer>> seenOrders;
    Set<Collection<Integer>> seenSignatures;
    Integer stepSize;
    Iterator<ICombinatoricsVector<Integer>> swapIterator;
    
    public AbstractIntegerListIterator(Integer numberOfCounters, Integer stepSize) {
        counters = new ArrayList<Integer>(numberOfCounters);
        iteratedCounters = new ArrayList<Integer>(numberOfCounters);
        this.stepSize = stepSize;
        this.seenSignatures = new HashSet<Collection<Integer>>();
        this.seenOrders = new HashSet<List<Integer>>();
        for (int i = 0; i < numberOfCounters; i++) {
            counters.add(0);
        }
        hasNext = true;
    }

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
    public boolean hasNext() {
        if (hasNext && (swapIterator == null || !swapIterator.hasNext())) {
            /**
             * Try to increase and decrease while seeing if the boundaries are respected (min per slot). This only
             * happens once the combinatorics iterator does not have any hasNext.
             */
            moveTokensBetweenSlot = true;
            while (moveTokensBetweenSlot) {
                if (counters.get(indexToDecrease) - stepSize >= this.minPerSlot) {
                    counters.set(indexToDecrease, counters.get(indexToDecrease) - stepSize);
                    counters.set(indexToIncrease, counters.get(indexToIncrease) + stepSize);
                    if (signatureHasNotBeenSeen(counters)) {
                        storeSignature(counters);
                        resetSwappingIterator(counters);
                        if (swapIterator.hasNext()) {
                            moveTokensBetweenSlot = false;
                        }
                        shiftIndices();
                    } else {
                        /**
                         * Signature is known, we skip it.
                         */
                        shiftIndices();
                    }
                } else {
                    /**
                     * The exit condition is given by checking that all positions to the left of the index to decrease are
                     * at the minimum and that the indexToIncrease is at the maximum position.
                     */
                    if (indexToIncrease == counters.size() - 1 && leftIndexesAreAllAtMin(indexToDecrease)) {
                        /*
                         * There is no hasNext and we get out of all iterations.
                         */
                        hasNext = false;
                        iteratedCounters = null;
                        return hasNext;
                    }
                    shiftIndices();
                }
            }
            if (hasNext) {
                /**
                 * A proper signature has been found.
                 */
                this.iteratedCounters = swapIterator.next().getVector();
                while (!signatureOrderHasNotBeenSeen(iteratedCounters) && swapIterator.hasNext()) {
                    iteratedCounters = swapIterator.next().getVector();
                }
                if (!signatureOrderHasNotBeenSeen(iteratedCounters) && !swapIterator.hasNext()) {
                    return this.hasNext();
                }
                if (signatureOrderHasNotBeenSeen(iteratedCounters)) {
                    storeSignatureOrder(iteratedCounters);
                }
            }
            return hasNext;
        } else if (hasNext && swapIterator != null && swapIterator.hasNext()) {
            this.iteratedCounters = swapIterator.next().getVector();
            while (!signatureOrderHasNotBeenSeen(iteratedCounters) && swapIterator.hasNext()) {
                iteratedCounters = swapIterator.next().getVector();
            }
            if (!signatureOrderHasNotBeenSeen(iteratedCounters) && !swapIterator.hasNext()) {
                return this.hasNext();
            }
            if (signatureOrderHasNotBeenSeen(iteratedCounters)) {
                storeSignatureOrder(iteratedCounters);
            }
            if (this.counters.size() == 1) {
                hasNext = false;
                return true;
            }
            return hasNext;
        } else {
            this.iteratedCounters = null;
            return false;
        }
    }

    public void initialize(Integer maxToShare, Integer minPerSlot) {
        if (counters.isEmpty()) {
            return;
        }
        this.minPerSlot = minPerSlot;
        if (minPerSlot * counters.size() > maxToShare) {
            hasNext = false;
            return;
        }
        Integer remain = maxToShare - (minPerSlot * (counters.size() - 1));
        counters.set(0, remain);
        for (int i = 1; i < counters.size(); i++) {
            counters.set(i, minPerSlot);
        }
        indexToDecrease = 0;
        indexToIncrease = 1;
        //        if (indexToIncrease >= counters.size()) {
        //            hasNext = false;
        //        }
        /**
         * We copy the state of the counter for the first version of the iterated counter
         */
        for (Integer counter : counters) {
            iteratedCounters.add(counter);
        }
        this.seenSignatures.clear();
        storeSignature(counters);
        resetSwappingIterator(counters);
    }


    boolean leftIndexesAreAllAtMin(Integer indexToDecrease) {
        for (int i = 0; i < indexToDecrease; i++) {
            if (counters.get(i) > minPerSlot) {
                return false;
            }
        }
        return true;
    }

    public List<Integer> next() {
        return Collections.unmodifiableList(iteratedCounters);
    }

    void resetSwappingIterator(List<Integer> counters) {
        /*
         * We have found a new signature, we need to generate/initialize permutations.
         */
        // create an array of the initial items (words "one" and "two")
        List<Integer> array = new ArrayList<Integer>(counters);
        // create an initial combinatorics vector
        ICombinatoricsVector<Integer> initialVector = Factory.createVector(array);
        // create a permutation with repetition generator, second parameter is a number of slots
        Generator<Integer> gen = new PermutationGenerator<Integer>(initialVector);
        // create an iterator
        swapIterator = gen.iterator();
        this.seenOrders.clear();
    }

    void shiftIndices() {
        if (indexToIncrease + 1 < counters.size()) {
            indexToDecrease++;
            indexToIncrease++;
        } else {
            indexToDecrease = 0;
            indexToIncrease = 1;
        }
    }

    abstract boolean signatureHasNotBeenSeen(List<Integer> newSignature);

    boolean signatureOrderHasNotBeenSeen(List<Integer> iteratedCounters) {
        return !this.seenOrders.contains(iteratedCounters);
    }

    abstract void storeSignature(List<Integer> counters);

    void storeSignatureOrder(List<Integer> iteratedCounters) {
        this.seenOrders.add(new ArrayList<Integer>(iteratedCounters));
    }


}
