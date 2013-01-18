/**
 * FutureEstimatesIterator.java
 *
 * 2012.09.07
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
package uk.ac.ebi.lipidhome.fastlipid.mass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import uk.ac.ebi.lipidhome.fastlipid.util.LipidChainConfigEstimate;

/**
 * @name FutureEstimatesIterator @date 2012.09.07
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) 
 * @brief Iterates over a list of futures of LipidChainConfigEstimate.
 *
 */
public class FutureEstimatesIterator implements Iterator<LipidChainConfigEstimate> {

    private static final Logger LOGGER = Logger.getLogger(FutureEstimatesIterator.class);
    private final List<LipidChainConfigEstimate> visited;
    private final List<Future<LipidChainConfigEstimate>> estimates;
    private LipidChainConfigEstimate current;

    /**
     * Initializes the iterator with a list of Futures (concurrency) to iterate over.
     * 
     * @param estimates the list of futures to iterate over.
     */
    public FutureEstimatesIterator(List<Future<LipidChainConfigEstimate>> estimates) {
        this.visited = new ArrayList<LipidChainConfigEstimate>(estimates.size());
        this.estimates = estimates;
        setCurrent();
    }

    /**
     * 
     * @return true if there is a next Future to visit.
     */
    public boolean hasNext() {
        return current!=null;
    }

    /**
     * 
     * @return the next future estimate. 
     */
    public LipidChainConfigEstimate next() {
        LipidChainConfigEstimate toRet = current;
        setCurrent();
        return toRet;
    }

    private void setCurrent() {
        current = null;
        int failed = 0;
        while (current == null && visited.size() < estimates.size() && failed < estimates.size()) {
            for (Future<LipidChainConfigEstimate> future : estimates) {
                try {
                    if (future.isDone()) {
                        if (visited.contains(future.get())) {
                            continue;
                        } else {
                            current = future.get();
                            visited.add(current);
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    failed++;
                    continue;
                } catch (ExecutionException e) {
                    failed++;
                    continue;
                }
            }
        }
    }

    /**
     * Removes the next future.
     */
    public void remove() {
        if (hasNext()) {
            next();
        }
    }
}
