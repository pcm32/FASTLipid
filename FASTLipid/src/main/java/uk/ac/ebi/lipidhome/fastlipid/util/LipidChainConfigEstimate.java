/**
 * LipidChainConfigEstimate.java
 *
 * 2012.09.03
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

package uk.ac.ebi.lipidhome.fastlipid.util;


import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;

/**
 * @name    LipidChainConfigEstimate
 * @date    2012.09.03
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   This object includes the results of the estimation process (of possible configurations), so that a head group and
 *          possible linkers fit within the mass range given. The result requires to have min and max number of carbons, 
 *          and for each of these values, a min and max number of double bonds.
 *
 */
public class LipidChainConfigEstimate {

    private static final Logger LOGGER = Logger.getLogger( LipidChainConfigEstimate.class );


    private Integer maxCarbons;
    private Integer minCarbons;
    private Map<Integer,Integer> carbonsToMaxDoubleBonds;
    private Map<Integer,Integer> carbonsToMinDoubleBonds;
    private HeadGroup hg;
    private Double minMass;
    private Double maxMass;

    /**
     * Get the value of minMass
     *
     * @return the value of minMass
     */
    public Double getMinMass() {
        return minMass;
    }

    /**
     * Set the value of minMass
     *
     * @param minMass new value of minMass
     */
    public void setMinMass(Double minMass) {
        this.minMass = minMass;
    }


    /**
     * Get the value of maxMass
     *
     * @return the value of maxMass
     */
    public Double getMaxMass() {
        return maxMass;
    }

    /**
     * Set the value of maxMass
     *
     * @param maxMass new value of maxMass
     */
    public void setMaxMass(Double maxMass) {
        this.maxMass = maxMass;
    }


    public LipidChainConfigEstimate(HeadGroup hg) {
        this.carbonsToMaxDoubleBonds = new HashMap<Integer, Integer>();
        this.carbonsToMinDoubleBonds = new HashMap<Integer, Integer>();
        this.hg = hg;
    }
    
    /**
     * @return the maxCarbons
     */
    public Integer getMaxCarbons() {
        return maxCarbons;
    }

    /**
     * @param maxCarbons the maxCarbons to set
     */
    public void setMaxCarbons(Integer maxCarbons, Integer minDoubleBonds, Integer maxDoubleBonds) {
        this.maxCarbons = maxCarbons;
        this.carbonsToMaxDoubleBonds.put(maxCarbons, maxDoubleBonds);
        this.carbonsToMinDoubleBonds.put(maxCarbons, minDoubleBonds);
    }

    /**
     * @return the minCarbons
     */
    public Integer getMinCarbons() {
        return minCarbons;
    }

    /**
     * @param minCarbons the minCarbons to set
     */
    public void setMinCarbons(Integer minCarbons, Integer minDoubleBonds, Integer maxDoubleBonds) {
        this.minCarbons = minCarbons;
        this.carbonsToMaxDoubleBonds.put(minCarbons, maxDoubleBonds);
        this.carbonsToMinDoubleBonds.put(minCarbons, minDoubleBonds);
    }
    
    public Integer getMaxDoubleBonds(Integer carbons) {
        return carbonsToMaxDoubleBonds.get(carbons);
    }

    public Integer getMinDoubleBonds(Integer carbons) {
        return carbonsToMinDoubleBonds.get(carbons);
    }

    public HeadGroup getHeadGroup() {
        return this.hg;
    }
}
