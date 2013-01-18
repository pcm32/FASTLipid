/**
 * GenericAtomDetector.java
 *
 * 2012.09.10
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
package uk.ac.ebi.lipidhome.fastlipid.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
/**
 * @name GenericAtomDetector @date 2012.09.10
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) @brief ...class description...
 *
 */
public class GenericAtomDetector {

    private static final Logger LOGGER = Logger.getLogger(GenericAtomDetector.class);

    public List<IPseudoAtom> detectGenericAtoms(IAtomContainer mol) {
        // This part above should be refactored to a head reader class.
        List<IPseudoAtom> rAtoms = new ArrayList<IPseudoAtom>();
        for (IAtom at : mol.atoms()) {
            if (at.getSymbol().equals("R")) {
                if (at instanceof IPseudoAtom) {
                    rAtoms.add((IPseudoAtom) at);
                }
            }
        }
        return rAtoms;
    }
}
