/**
 * MolAnomalyChecker.java
 *
 * 2013.01.17
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
import java.util.List;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * @name    MolAnomalyChecker
 * @date    2013.01.17
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class MolAnomalyChecker {

    private static final Logger LOGGER = Logger.getLogger( MolAnomalyChecker.class );


    /**
     * Checks whether the atom container mol has bonds which have atoms that do not belong to the atom container.
     * Returns the list of all those bonds.
     * 
     * @param mol
     * @return list of bonds which contain foreign atoms.
     */
    public List<IBond> checkForeignBondsAnomaly(IAtomContainer mol) {
        List<IBond> anom = new ArrayList<IBond>();
        for (IBond iBond : mol.bonds()) {
            for (IAtom iAtom : iBond.atoms()) {
                if (!mol.contains(iAtom)) {
                    anom.add(iBond);
                    break;
                }
            }
        }
        return anom;
    }

    /**
     * Checks whether the atom container has bonds which points to atoms which are not part of the atom container. 
     * Returns the list of all those foreign atoms.
     * 
     * @param mol
     * @return list of all foreign atoms
     */
    public List<IAtom> checkForeignAtomAnomaly(IAtomContainer mol) {
        List<IAtom> foreignAtoms = new ArrayList<IAtom>();
        for (IBond iBond : mol.bonds()) {
            for (IAtom iAtom : iBond.atoms()) {
                if (!mol.contains(iAtom) && !foreignAtoms.contains(iAtom)) {
                    foreignAtoms.add(iAtom);
                }
            }
        }
        return foreignAtoms;
    }
}
