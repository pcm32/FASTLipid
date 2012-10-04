/**
 * AccAscCompositionConstrained.java
 *
 * 2012.10.03
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
package uk.ac.ebi.lipidhome.fastlipid.composition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @name AccAscCompositionConstrained @date 2012.10.03
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) @brief Sub-class of {@link AccAscComposition} which tailors the composition results
 * to fixed width arrays (discards shorter or longer results). Only when minimum number per slot is zero, then shorter
 * results are accepted, appending zeros at the end to complete to the positions required (defined in the number of
 * slots). This also checks, when the minimum per slot is >0, that all positions of the solution comply with this (else
 * discarded). This class also checks that the step size between any consecutive solutions (only when they are of the
 * same width) complies with the step size set.
 *
 */
public class AccAscCompositionConstrained extends AccAscComposition {

    private static final Logger LOGGER = Logger.getLogger(AccAscCompositionConstrained.class);
    private int numOfSlots;
    private int minPerSlot;
    private int stepSize;
    private List<Integer> previous;

    /**
     * Sub-class of {@link AccAscComposition} which tailors the composition results to fixed width arrays (discards
     * shorter or longer results). Only when minimum number per slot is zero, then shorter results are accepted,
     * appending zeros at the end to complete to the positions required (defined in the number of slots). This also
     * checks, when the minimum per slot is >0, that all positions of the solution comply with this (else discarded).
     * This class also checks that the step size between any consecutive solutions (only when they are of the same
     * width) complies with the step size set. 
     * 
     * Constructor which sets the number to generate the composition for, the fixed width that the results can have,
     * the minimum number that each position can take, and the step size between different solutions.
     * 
     * This constrained composition maker does not handle the 0 case producing [0,....,0] of the defined length, as it is
     * not a real composition. That needs to be solved externally (important for double bonds arrangements).
     * 
     *
     * @param numToCompose
     * @param numOfSlots is the width of the solution. Or how many fatty acid chains a header could have attached.
     * @param minPerSlot
     * @param stepSize
     */
    public AccAscCompositionConstrained(int numToCompose, int numOfSlots, int minPerSlot, int stepSize) {
        super(numToCompose);
        this.numOfSlots = numOfSlots;
        this.minPerSlot = minPerSlot;
        this.stepSize = stepSize;
    }

    @Override
    boolean checkedYield(int[] result, int indexFrom, int indexTo) {
        if (minPerSlot > 0) {
            /**
             * We accept only arrays of the desired length if there is minimum per slot > 0
             */
            if (indexTo - indexFrom == numOfSlots) {
                boolean allEqOrAboveMin = true;
                /*
                 * Check whether all slots are above the minimum set.
                 */
                for (int i = indexFrom; i < indexTo; i++) {
                    if (result[i] < minPerSlot) {
                        allEqOrAboveMin = false;
                        break;
                    }
                }
                /*
                 * If all of them are above, then yield.
                 */
                return allEqOrAboveMin && changeIsStepSizeComplaint(result, indexFrom, indexTo);
            }
        } else {
            /*
             * if the minimum per slot is zero, then we accept portions that are of smaller sizes (in which case the
             * copy of arrays in doYield will fill with zeros) or of the same size.
             */
            if (indexTo - indexFrom <= numOfSlots) {
                //indexTo = indexTo - indexFrom < numOfSlots ? indexFrom + numOfSlots : indexTo;
                //doYield(result, indexFrom, indexTo);
                /**
                 * This aims to cover cases like [1,0,0,0]
                 */
                return changeIsStepSizeComplaint(result, indexFrom, indexTo);
            }
        }
        return false;
    }

    @Override
    List<Integer> getArrayPart(int[] a, int indexFrom, int indexTo) {
        List<Integer> toRet = new ArrayList<Integer>(numOfSlots);
        if (minPerSlot > 0) {
            for (int i : Arrays.copyOfRange(a, indexFrom, indexTo)) {
                toRet.add(i);
            }
            previous = toRet;
        } else {
            for (int i : Arrays.copyOfRange(a, indexFrom, indexTo)) {
                toRet.add(i);
            }
            for (int i = indexTo; i < indexFrom + numOfSlots; i++) {
                // for any difference between the numOfSlots and indexTo, we add zeros for cases like [1,0,0], [2,2,0]
                toRet.add(0);
            }
            previous = toRet;
        }
        return toRet;
    }

    private boolean changeIsStepSizeComplaint(int[] result, int indexFrom, int indexTo) {
        if (stepSize == 1) {
            return true;
        }
        if (previous == null) {
            return true;
        }
        if (previous.size() != indexTo - indexFrom) {
            return true;
        }
        int previousCounter = 0;
        for (int i = indexFrom; i < indexTo; i++) {
            int change = result[i] - previous.get(previousCounter);
            if (change != 0 && change * change != stepSize * stepSize) {
                return false;
            }
            previousCounter++;
        }
        return true;
    }
}
