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
package uk.ac.ebi.lipidhome.fastlipid.structure;

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
 * @brief   Info container for fatty acids.
 *
 * InfoContainer that stores data of a single fatty acid: the number of carbons and positions of double bonds.
 */
public class ChainInfoContainer {
    
    private static final Logger LOGGER = Logger.getLogger(ChainInfoContainer.class);
    
    private final List<Integer> doubleBondPositions = new ArrayList<Integer>();
    private Integer numOfCarbons;
    
    public ChainInfoContainer(Integer numOfCarbons) {
        this.numOfCarbons = numOfCarbons;
    }
    
    public List<Integer> getDoubleBondPositions() {
        return Collections.unmodifiableList(doubleBondPositions);
    }

    public void addDoubleBondPos(int positionOfDoubleBondInFA) {
        doubleBondPositions.add(positionOfDoubleBondInFA);
    }

    public Integer getNumCarbons() {
        return numOfCarbons;
    }
}
