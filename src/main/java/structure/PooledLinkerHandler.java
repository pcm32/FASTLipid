/**
 * PooledLinkerHandler.java
 *
 * 2012.09.14
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

package structure;


import java.util.ArrayList;
import java.util.List;
import uk.ac.ebi.lipidhome.fastlipid.lnetmoleculegenerator.LNetMoleculeGeneratorException;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import uk.ac.ebi.lipidhome.fastlipid.util.AtomPool;
import uk.ac.ebi.lipidhome.fastlipid.util.BondPool;

/**
 * @name    PooledLinkerHandler
 * @date    2012.09.14
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class PooledLinkerHandler extends AbstractPooledLinker implements LinkerHandler{

    private static final Logger LOGGER = Logger.getLogger( PooledLinkerHandler.class );
    
    private AtomPool atomPool;
    private BondPool bondPool;
    
    public PooledLinkerHandler(AtomPool atomPool, BondPool bondPool) {
        this.atomPool = atomPool;
        this.bondPool = bondPool;
    }

    void removeBond(IBond bond, IMolecule mol) {
        mol.removeBond(bond);
        this.bondPool.checkIn(bond);
    }

    void removeAtom(IAtom atom, IMolecule mol) {
        mol.removeAtom(atom);
        this.atomPool.checkIn(atom);
    }

    IBond getBond() {
        return this.bondPool.checkOut();
    }

    IAtom getAtom() {
        return this.atomPool.checkOut();
    }


}
