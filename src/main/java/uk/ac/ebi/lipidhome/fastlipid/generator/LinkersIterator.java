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
package uk.ac.ebi.lipidhome.fastlipid.generator;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.permutations.PermutationWithRepetitionGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;

/**
 * @name SuccesiveIntegerListIterator @date 2012.08.17
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) @brief Handles the iteration on a list of possible linkers, were the list of possible
 * linkers can be either bigger or smaller than the number of slots for fatty acids.
 *
 */
public class LinkersIterator {

    private static final Logger LOGGER = Logger.getLogger(LinkersIterator.class);
    private List<SingleLinkConfiguration> possibleLinkers;
    private HeadGroup hg;
    private Boolean hasNext = false;
    private Boolean moveTokensBetweenSlot = false;
    private Iterator<ICombinatoricsVector<SingleLinkConfiguration>> swapIterator;


    /**
     * Generates an iterator that has defined set of possible linkers which need to be traversed so that all different orders
     * and combinations of these linkers are visited.
     * 
     * @param numberOfCounters
     * @param stepSize 
     */
    public LinkersIterator(List<SingleLinkConfiguration> possibleLinkers, HeadGroup hg) {
        this.possibleLinkers = possibleLinkers;
        this.hg = hg;
        resetSwappingIterator();
    }

    public List<SingleLinkConfiguration> next() {
        return swapIterator.next().getVector();
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
            return swapIterator.hasNext();
    }

    private void resetSwappingIterator() {
        /*
         * We have found a new signature, we need to generate/initialize permutations.
         */

        // create an initial combinatorics vector
        ICombinatoricsVector<SingleLinkConfiguration> initialVector = Factory.createVector(possibleLinkers);
        // create a permutation with repetition generator, second parameter is a number of slots
        Generator<SingleLinkConfiguration> gen = new PermutationWithRepetitionGenerator<SingleLinkConfiguration>(initialVector , this.hg.getNumOfSlots());

        // create an iterator
        swapIterator = gen.iterator();
    }

}
