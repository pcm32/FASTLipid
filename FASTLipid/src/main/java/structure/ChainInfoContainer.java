/**
 * ChainInfoContainer.java
 *
 * 2011.11.28
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
package structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @name    ChainInfoContainer
 * @date    2011.11.28
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class ChainInfoContainer {
    
    private static final Logger LOGGER = Logger.getLogger(ChainInfoContainer.class);
    
    private final List<Integer> doubleBondPositions = new ArrayList<Integer>();
    
    public List<Integer> getDoubleBondPositions() {
        return Collections.unmodifiableList(doubleBondPositions);
    }

    public void addDoubleBondPos(int counter) {
        doubleBondPositions.add(counter);
    }
}
