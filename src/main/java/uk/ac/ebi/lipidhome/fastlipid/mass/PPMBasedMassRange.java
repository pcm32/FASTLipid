/**
 * PPMBasedMassRange.java
 *
 * 2012.09.12
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


//import org.apache.log4j.Logger;

/**
 * @name    PPMBasedMassRange
 * @date    2012.09.12
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   Calculates a mass range starting from a central mass and a ppm.
 *
 */
public class PPMBasedMassRange implements MassRange{

    //private static final Logger LOGGER = Logger.getLogger( PPMBasedMassRange.class );
    
    private Double minMass;
    private Double maxMass;
    
    public PPMBasedMassRange(Double mass, Float ppm) {
        double interval = Math.pow(10, -6)*ppm*mass;
        this.minMass = mass - interval;
        this.maxMass = mass + interval;
    }

    public Double getMinMass() {
        return minMass;
    }

    public Double getMaxMass() {
        return maxMass;
    }


}
