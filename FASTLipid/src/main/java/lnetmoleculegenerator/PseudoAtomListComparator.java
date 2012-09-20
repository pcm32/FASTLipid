/**
 * PseudoAtomListComparator.java
 *
 * 2012.08.17
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

package lnetmoleculegenerator;


import java.util.Comparator;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IPseudoAtom;

/**
 * @name    PseudoAtomListComparator
 * @date    2012.08.17
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   Compares two pseudoatoms supposing that their labels follow the scheme R<number>, based on the number.
 *
 */
public class PseudoAtomListComparator implements Comparator<IPseudoAtom> {

    private static final Logger LOGGER = Logger.getLogger( PseudoAtomListComparator.class );

    public PseudoAtomListComparator() {
    }

    public int compare(IPseudoAtom o1, IPseudoAtom o2) {
        try {
            Integer label1 = Integer.parseInt(o1.getLabel().substring(1));
            Integer label2 = Integer.parseInt(o2.getLabel().substring(1));
        
            return label1.compareTo(label2);
        } catch(NumberFormatException e) {
            throw new LNetMoleculeGeneratorException("Header molecule was probably submitted in the incorrect format: generic atoms need to have labels such as R1, R2, etc.:"+e.getMessage());
        }
    }


}
