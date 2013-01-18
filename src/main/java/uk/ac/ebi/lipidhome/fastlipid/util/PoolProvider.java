/**
 * PoolProvider.java
 *
 * 2012.09.13
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


import org.apache.log4j.Logger;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * @name    PoolProvider
 * @date    2012.09.13
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   Singleton class to provide uniform access to pools. Should not be used for multithreading purposes.
 *
 */
public class PoolProvider {

    private static final Logger LOGGER = Logger.getLogger( PoolProvider.class );
    
    private static PoolProvider instance;
    
    AtomPool atomPool;
    MoleculePool molPool;
    BondPool bondPool;
    
    /**
     * Initializes an atom pool, a mol pool, and a bond pool.
     */
    private PoolProvider() {
        this.atomPool = new AtomPool(SilentChemObjectBuilder.getInstance());
        this.molPool = new MoleculePool(SilentChemObjectBuilder.getInstance());
        this.bondPool = new BondPool(SilentChemObjectBuilder.getInstance());
    }
    
    /**
     * 
     * @return the atom pool.
     */
    public AtomPool getAtomPool() {
        return this.atomPool;
    }
    
    /**
     * 
     * @return the bond pool
     */
    public BondPool getBondPool() {
        return this.bondPool;
    }
    
    /**
     * 
     * @return the molecule pool.
     */
    public MoleculePool getMoleculePool() {
        return this.molPool;
    }

    /**
     * Singleton access.
     * 
     * @return the pool provider instance.
     */
    public static PoolProvider getInstance() {
        if(instance==null) {
            instance = new PoolProvider();
        }
        return instance;
    }
}
