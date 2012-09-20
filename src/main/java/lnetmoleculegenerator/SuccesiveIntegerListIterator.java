/**
 * SuccesiveIntegerListIterator.java
 *
 * 2012.08.17
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with CheMet. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package lnetmoleculegenerator;

import java.util.*;
import org.apache.log4j.Logger;
import org.paukov.combinatorics.CombinatoricsVector;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.permutations.PermutationGenerator;

/**
 * @name SuccesiveIntegerListIterator @date 2012.08.17
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) @brief Handles the iteration on a list of carbon or double bond counters, as the
 * number of carbons or double bonds is either decreased or increased.
 *
 */
public class SuccesiveIntegerListIterator {

    private static final Logger LOGGER = Logger.getLogger(SuccesiveIntegerListIterator.class);
    private List<Integer> counters;
    private List<Integer> iteratedCounters;
    private Set<List<Integer>> seenSignatures;
    private Set<List<Integer>> seenOrders;
    private Integer stepSize;
    private Integer minPerSlot;
    private Integer indexToDecrease; // signals where the decrease should be made
    private Integer indexToIncrease; // signals where the increase should be made
    private Boolean hasNext = false;
    private Boolean moveTokensBetweenSlot = false;
    private Iterator<CombinatoricsVector<Integer>> swapIterator;


    /**
     * Generates an iterator that has defined number of counters which need to be traversed so that all different orders
     * of an integer partition, with restrictions on the step size and minimal boundary for all slots, are visited. 
     * The integer number partitioned is set in the {@link #initialize(java.lang.Integer, java.lang.Integer) } method.
     * 
     * @param numberOfCounters
     * @param stepSize 
     */
    public SuccesiveIntegerListIterator(Integer numberOfCounters, Integer stepSize) {
        counters = new ArrayList<Integer>(numberOfCounters);
        iteratedCounters = new ArrayList<Integer>(numberOfCounters);
        this.stepSize = stepSize;
        this.seenSignatures = new HashSet<List<Integer>>();
        this.seenOrders = new HashSet<List<Integer>>();
        for (int i = 0; i < numberOfCounters; i++) {
            counters.add(0);
        }
        hasNext = true;
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

    public List<Integer> next() {
        return Collections.unmodifiableList(iteratedCounters);
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
            if(this.counters.size()==1) {
                hasNext = false;
                return true;
            }
            return hasNext;
        } else {
            this.iteratedCounters = null;
            return false;
        }
    }

    private void storeSignature(List<Integer> counters) {
        List<Integer> sign = new ArrayList<Integer>(counters);
        this.seenSignatures.add(sign);
    }

    private boolean signatureHasNotBeenSeen(List<Integer> newSignature) {
        // this implementation might not be very efficient.
        /*
         * ((AbstractList)this.counters).retainAll(counters); for (List<Integer> signature : seenSignatures) {
         * if(signature.containsAll(counters)) return false; } return true;
         */
        /*for (List<Integer> signature : seenSignatures) {
            Iterator<Integer> countIt = newSignature.iterator();
            boolean found = true;
            while (countIt.hasNext()) {
                if (!signature.contains(countIt.next())) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return false;
            }
        }
        return true;*/
        for (List<Integer> seenSignature : seenSignatures) {
            if(seenSignature.containsAll(newSignature))
                return false;
        }
        return true;

    }

    private void shiftIndices() {
        if (indexToIncrease + 1 < counters.size()) {
            indexToDecrease++;
            indexToIncrease++;
        } else {
            indexToDecrease = 0;
            indexToIncrease = 1;
        }
    }

    private void resetSwappingIterator(List<Integer> counters) {
        /*
         * We have found a new signature, we need to generate/initialize permutations.
         */
        // create an array of the initial items (words "one" and "two")
        List<Integer> array = new ArrayList<Integer>(counters);

        // create an initial combinatorics vector
        CombinatoricsVector<Integer> initialVector = new CombinatoricsVector<Integer>(array);
        // create a permutation with repetition generator, second parameter is a number of slots
        Generator<Integer> gen = new PermutationGenerator<Integer>(initialVector);

        // create an iterator
        swapIterator = gen.createIterator();
        this.seenOrders.clear();
    }

    private boolean leftIndexesAreAllAtMin(Integer indexToDecrease) {
        for (int i = 0; i < indexToDecrease; i++) {
            if (counters.get(i) > minPerSlot) {
                return false;
            }
        }
        return true;
    }

    private boolean signatureOrderHasNotBeenSeen(List<Integer> iteratedCounters) {
        return !this.seenOrders.contains(iteratedCounters);
    }

    private void storeSignatureOrder(List<Integer> iteratedCounters) {
        this.seenOrders.add(new ArrayList<Integer>(iteratedCounters));
    }

}
