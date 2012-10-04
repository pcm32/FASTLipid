/**
 * IsomerInfoContainer.java
 *
 * 2012.09.07
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


import java.util.List;
import org.apache.log4j.Logger;

/**
 * @name    IsomerInfoContainer
 * @date    2012.09.07
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   Holds results for a whole group of isomers: number of molecules, common molecular formula, common mass, etc.
 *
 */
public class IsomerInfoContainer {

    private static final Logger LOGGER = Logger.getLogger( IsomerInfoContainer.class );
    
    private Integer numOfMolsGenerated;
    private String molecularFormula;
    private Double mass;
    private Long elapsedTime;

    private HeadGroup headGroup;
    private List<SingleLinkConfiguration> linkers;
    private Integer numOfDoubleBonds;
    private Integer numOfCarbons;
    private double massDevPPM;

    /**
     * Get the value of numOfCarbons
     *
     * @return the value of numOfCarbons
     */
    public Integer getNumOfCarbons() {
        return numOfCarbons;
    }

    /**
     * Set the value of numOfCarbons
     *
     * @param numOfCarbons new value of numOfCarbons
     */
    public void setNumOfCarbons(Integer numOfCarbons) {
        this.numOfCarbons = numOfCarbons;
    }


    /**
     * Get the value of numOfDoubleBonds
     *
     * @return the value of numOfDoubleBonds
     */
    public Integer getNumOfDoubleBonds() {
        return numOfDoubleBonds;
    }

    /**
     * Set the value of numOfDoubleBonds
     *
     * @param numOfDoubleBonds new value of numOfDoubleBonds
     */
    public void setNumOfDoubleBonds(Integer numOfDoubleBonds) {
        this.numOfDoubleBonds = numOfDoubleBonds;
    }


    /**
     * Get the value of linkers
     *
     * @return the value of linkers
     */
    public List<SingleLinkConfiguration> getLinkers() {
        return linkers;
    }

    /**
     * Set the value of linkers
     *
     * @param linkers new value of linkers
     */
    public void setLinkers(List<SingleLinkConfiguration> linkers) {
        this.linkers = linkers;
    }


    /**
     * Get the value of headGroup
     *
     * @return the value of headGroup
     */
    public HeadGroup getHeadGroup() {
        return headGroup;
    }

    /**
     * Set the value of headGroup
     *
     * @param headGroup new value of headGroup
     */
    public void setHeadGroup(HeadGroup headGroup) {
        this.headGroup = headGroup;
    }

    /**
     * Get the value of elapsedTime
     *
     * @return the value of elapsedTime
     */
    public Long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Set the value of elapsedTime
     *
     * @param elapsedTime new value of elapsedTime
     */
    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }


    /**
     * Get the value of mass
     *
     * @return the value of mass
     */
    public Double getMass() {
        return mass;
    }

    /**
     * Set the value of mass
     *
     * @param mass new value of mass
     */
    public void setMass(Double mass) {
        this.mass = mass;
    }

    /**
     * Get the value of molecularFormula
     *
     * @return the value of molecularFormula
     */
    public String getMolecularFormula() {
        return molecularFormula;
    }

    /**
     * Set the value of molecularFormula
     *
     * @param molecularFormula new value of molecularFormula
     */
    public void setMolecularFormula(String molecularFormula) {
        this.molecularFormula = molecularFormula;
    }

    /**
     * Get the value of numOfMolsGenerated
     *
     * @return the value of numOfMolsGenerated
     */
    public Integer getNumOfMolsGenerated() {
        return numOfMolsGenerated;
    }

    /**
     * Set the value of numOfMolsGenerated
     *
     * @param numOfMolsGenerated new value of numOfMolsGenerated
     */
    public void setNumOfMolsGenerated(Integer numOfMolsGenerated) {
        this.numOfMolsGenerated = numOfMolsGenerated;
    }

    /**
     * Sets the mass deviation in PPM
     * 
     * @param massDevPPM 
     */
    public void setMassDeviationInPPM(double massDevPPM) {
        this.massDevPPM = massDevPPM;
    }

    /**
     * Gets the mass deviation in PPM.
     * 
     * @return the massDevPPM
     */
    public double getMassDeviationInPPM() {
        return massDevPPM;
    }



}
