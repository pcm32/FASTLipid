/**
 * FattyAcidSpecies.java
 *
 * 2012.08.20
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
package uk.ac.ebi.lipidhome.fastlipid.structure;


/**
 * @name FattyAcidSpecies @date 2012.08.20
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) @brief ...class description...
 *
 */
public class FattyAcidSpecies {

    private Integer carbonCount;
    private Integer doubleBondsCount;
    private String name;
    
    public FattyAcidSpecies(Integer carbons, Integer doubleBonds) {
        this.carbonCount = carbons;
        this.doubleBondsCount = doubleBonds;
        this.name = carbonCount+":"+doubleBondsCount;
    }

    @Override
    public String toString() {
        return carbonCount+":"+doubleBondsCount;
    }
    
    

    /**
     * @return the carbonCount
     */
    public Integer getCarbonCount() {
        return carbonCount;
    }

    /**
     * @return the doubleBondsCount
     */
    public Integer getDoubleBondsCount() {
        return doubleBondsCount;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    
}
