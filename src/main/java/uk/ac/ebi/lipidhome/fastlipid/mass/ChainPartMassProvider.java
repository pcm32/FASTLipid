/**
 * ChainPartMassProvider.java
 *
 * 2012.09.17
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
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * @name    ChainPartMassProvider
 * @date    2012.09.17
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class ChainPartMassProvider {

    private static final Logger LOGGER = Logger.getLogger( ChainPartMassProvider.class );
    
    private static ChainPartMassProvider instance;
    
    private double fullySatAtomicUnitWeight;
    private double fullyUnSatAtomicUnitWeight;
    private double endPartFullySatWeight;

    /**
     * Gets the exact mass of the end part (last C) of a fatty acid when it is fully saturated. In this case, -CH3
     *
     * @return the exact mass of -CH3
     */
    public double getEndPartFullySatExactMass() {
        return endPartFullySatWeight;
    }


    /**
     * Gets the unique instance of the ChainPartMassProvider.
     * @return 
     */
    public static ChainPartMassProvider getInstance() {
        if(instance==null)
            instance = new ChainPartMassProvider();
        return instance;
    }
    
    private ChainPartMassProvider() {
        fullySatAtomicUnitWeight = MolecularFormulaManipulator.getTotalExactMass(MolecularFormulaManipulator.getMolecularFormula("CH2", SilentChemObjectBuilder.getInstance()));
        fullyUnSatAtomicUnitWeight = MolecularFormulaManipulator.getTotalExactMass(MolecularFormulaManipulator.getMolecularFormula("C", SilentChemObjectBuilder.getInstance()));
        endPartFullySatWeight = MolecularFormulaManipulator.getTotalExactMass(MolecularFormulaManipulator.getMolecularFormula("CH3", SilentChemObjectBuilder.getInstance()));
    }

    /**
     * Gets the exact mass of a single part (-CH2-) of a fatty acid when it is fully saturated. In this case, -CH2-.
     *
     * @return the exact mass of -CH2-
     */
    public double getFullySatAtomicUnitWeight() {
        return fullySatAtomicUnitWeight;
    }

    /**
     * Gets the exact mass of a single part (=C=) of a fatty acid when it is fully unsaturated.
     *
     * @return the exact mass of =C=
     */
    public double getFullyUnSatAtomicUnitWeight() {
        return fullyUnSatAtomicUnitWeight;
    }

}
