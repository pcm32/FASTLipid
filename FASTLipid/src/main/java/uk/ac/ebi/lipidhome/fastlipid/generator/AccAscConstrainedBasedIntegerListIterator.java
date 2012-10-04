/**
 * AccAscConstrainedBasedIntegerListIterator.java
 *
 * 2012.10.03
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
import org.apache.log4j.Logger;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.paukov.combinatorics.permutations.PermutationGenerator;
import uk.ac.ebi.lipidhome.fastlipid.composition.AccAscCompositionConstrained;

/**
 * @name    AccAscConstrainedBasedIntegerListIterator
 * @date    2012.10.03
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   Based on the Accelerated ascendent constrained composition generator.
 *
 */
public class AccAscConstrainedBasedIntegerListIterator implements IntegerListIterator {

    private static final Logger LOGGER = Logger.getLogger( AccAscConstrainedBasedIntegerListIterator.class );
    
    private AccAscCompositionConstrained compositionCons;
    private Integer numberOfCounters;
    private Integer stepSize;
    Iterator<ICombinatoricsVector<Integer>> swapIterator;
    
    private boolean hasNext;
    private List<Integer> iteratedCounters;
    private List<Integer> counters;
    /**
     * Generates an iterator that has defined number of counters which need to be traversed so that all different orders
     * of an integer partition, with restrictions on the step size and minimal boundary for all slots, are visited. 
     * The integer number partitioned is set in the {@link #initialize(java.lang.Integer, java.lang.Integer) } method.
     * 
     * @param numberOfCounters
     * @param stepSize 
     */
    public AccAscConstrainedBasedIntegerListIterator(Integer numberOfCounters, Integer stepSize) {
        this.numberOfCounters = numberOfCounters;
        this.stepSize = stepSize;
        this.hasNext = true;
    }

    public boolean hasNext() {
        if (hasNext && (swapIterator == null || !swapIterator.hasNext())) {
            /**
             * Try to increase and decrease while seeing if the boundaries are respected (min per slot). This only
             * happens once the combinatorics iterator does not have any hasNext.
             * 
             * We need to address the case where the composition can have zeros, like when it is needed to enumerate double
             * bonds: [0,0,2,0], [0,0,1,1]
             */
            //this.counters = new ArrayList<Integer>(numberOfCounters);
            hasNext = false;
            if(compositionCons.hasNext()) {
                counters = compositionCons.next();
                hasNext = true;
            }
            if (hasNext) {
                /**
                 * A proper signature has been found.
                 */
                resetSwappingIterator(counters);
                this.iteratedCounters = swapIterator.next().getVector();
            }
            return hasNext;
        } else if (hasNext && swapIterator != null && swapIterator.hasNext()) {
            this.iteratedCounters = swapIterator.next().getVector();
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
        if(maxToShare == 0 && minPerSlot == 0) {
            this.counters = new ArrayList<Integer>(numberOfCounters);
            for (int i = 0; i < numberOfCounters; i++) {
                this.counters.add(0);
            }
            hasNext = true;
            resetSwappingIterator(counters);
            this.compositionCons = new AccAscCompositionConstrained(maxToShare, numberOfCounters, minPerSlot, stepSize);
            return;
        }
        if (minPerSlot * numberOfCounters > maxToShare || maxToShare == 0 ) { // 
            hasNext = false;
            return;
        }
        this.compositionCons = new AccAscCompositionConstrained(maxToShare, numberOfCounters, minPerSlot, stepSize);
        if(compositionCons.hasNext()) {
            this.counters = compositionCons.next();
        }
        resetSwappingIterator(counters);
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
    }


}
