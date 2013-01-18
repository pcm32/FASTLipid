/**
 * MassDeviationCalculator.java
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

package uk.ac.ebi.lipidhome.fastlipid.mass;


import org.apache.log4j.Logger;

/**
 * @name    MassDeviationCalculator
 * @date    2012.10.03
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   Calculates the deviation between two masses in PPM, supposing that the first given one (queried) is the base.
 *
 */
public class MassDeviationCalculator {

    private static final Logger LOGGER = Logger.getLogger( MassDeviationCalculator.class );

    private double queriedMass;
    
    /**
     * Initializes the object with the queried mass, which is the base for the PPM deviation calculation.
     * 
     * @param queriedMass 
     */
    public MassDeviationCalculator(Double queriedMass) {
        this.queriedMass = queriedMass;
    }

    /**
     * Given a second mass, this calculates the deviation from the original queriedMass given in the constructor, in PPM
     * based on the queried mass.
     * 
     * @param mass
     * @return deviation in PPM
     */
    public double calculateDeviationInPPM(Double mass) {
        double diff = Math.abs(queriedMass - mass);
        return diff/(Math.pow(10, -6)*queriedMass);
    }


}
