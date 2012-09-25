/**
 * MultiSetBasedIntegerListIterator.java
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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * @name MultiSetBasedIntegerListIterator @date 2012.08.17
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) @brief Handles the iteration on a list of carbon or double bond counters, as the
 * number of carbons or double bonds is either decreased or increased.
 *
 */
public class MultiSetBasedIntegerListIterator extends AbstractNCIntegerListIterator implements IntegerListIterator {

    private static final Logger LOGGER = Logger.getLogger(MultiSetBasedIntegerListIterator.class);


    /**
     * Generates an iterator that has defined number of counters which need to be traversed so that all different orders
     * of an integer partition, with restrictions on the step size and minimal boundary for all slots, are visited. 
     * The integer number partitioned is set in the {@link #initialize(java.lang.Integer, java.lang.Integer) } method.
     * 
     * @param numberOfCounters
     * @param stepSize 
     */
    public MultiSetBasedIntegerListIterator(Integer numberOfCounters, Integer stepSize) {
        super(numberOfCounters, stepSize);
    }

    void storeSignature(List<Integer> counters) {
        //List<Integer> sign = new ArrayList<Integer>(counters);
        Multiset<Integer> signatures = HashMultiset.create(counters);
        this.seenSignatures.add(signatures);
        //this.seenSignatures.add(new BagMap<Integer>(counters));
    }

    boolean signatureHasNotBeenSeen(List<Integer> newSignature) {
        // this implementation might not be very efficient.
        return !seenSignatures.contains(HashMultiset.create(newSignature));
        /*
        for (Collection<Integer> seenSignature : seenSignatures) {
            if(seenSignature.containsAll(newSignature))
                return false;
        }
        return true;*/

    }

}
