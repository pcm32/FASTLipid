/**
 * AbstractPooledLinker.java
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

package uk.ac.ebi.lipidhome.fastlipid.structure;


import java.util.ArrayList;
import java.util.List;
import uk.ac.ebi.lipidhome.fastlipid.generator.LNetMoleculeGeneratorException;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * @name    AbstractPooledLinker
 * @date    2012.09.14
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public abstract class AbstractPooledLinker {

    public AbstractPooledLinker() {
    }

    /**
     * For Acyl linker the mol has to have a =O attached to the first carbon.
     * @param mol
     * @param carbon1
     * @return
     */
    boolean checkForAcyl(IMolecule mol, IAtom carbon1) {
        for (IBond bond : mol.getConnectedBondsList(carbon1)) {
            if (bond.getOrder().equals(IBond.Order.DOUBLE)) {
                IAtom suspOxygen = bond.getConnectedAtom(carbon1);
                return suspOxygen.getSymbol().equals("O");
            }
        }
        return false;
    }

    /**
     * For alkyl linkers, just a CH2 group needs to be checked.
     * @param mol
     * @param carbon1
     * @return
     */
    boolean checkForAlkyl(IMolecule mol, IAtom carbon1) {
        int numberOfSingleBondsToCs = 0;
        int numberOfBonds = 0;
        for (IBond bond : mol.getConnectedBondsList(carbon1)) {
            numberOfBonds++;
            if (bond.getOrder().equals(IBond.Order.SINGLE) && bond.getConnectedAtom(carbon1).getSymbol().equals("C")) {
                numberOfSingleBondsToCs++;
            } else if (bond.getOrder().equals(IBond.Order.SINGLE) && bond.getConnectedAtom(carbon1).getSymbol().equals("H")) {
                numberOfBonds--;
            }
        }
        return numberOfBonds == numberOfSingleBondsToCs && numberOfSingleBondsToCs == 1;
    }

    public boolean checkLinker(IMolecule mol, SingleLinkConfiguration linker) {
        IAtom carbon1 = mol.getFirstAtom();
        if (linker.equals(SingleLinkConfiguration.Acyl)) {
            return checkForAcyl(mol, carbon1);
        } else if (linker.equals(SingleLinkConfiguration.Alkyl)) {
            return checkForAlkyl(mol, carbon1);
        }
        throw new LNetMoleculeGeneratorException("Linker checker needs to be defined in the LinkerHandler classes for " + linker.name());
    }

    abstract IAtom getAtom();

    abstract IBond getBond();

    abstract void removeAtom(IAtom atom, IMolecule mol);

    abstract void removeBond(IBond bond, IMolecule mol);

    /**
     * C=O for first carbon.
     * @param mol
     * @param carbon1
     */
    void setAcyl(IMolecule mol, IAtom carbon1) {
        setAlkyl(mol, carbon1);
        IBond bondOx = getBond();
        IAtom ox = getAtom();
        ox.setSymbol("O");
        bondOx.setAtom(carbon1, 0);
        bondOx.setAtom(ox, 1);
        bondOx.setOrder(IBond.Order.DOUBLE);
        mol.addBond(bondOx);
    }

    /**
     * First carbon should only be single bonded to another carbon.
     * @param mol
     * @param carbon1
     */
    void setAlkyl(IMolecule mol, IAtom carbon1) {
        List<IBond> toRemoveBond = new ArrayList<IBond>();
        List<IAtom> toRemoveAtom = new ArrayList<IAtom>();
        int bonds2C = 0;
        for (IBond bond : mol.getConnectedBondsList(carbon1)) {
            if (!bond.getOrder().equals(IBond.Order.SINGLE) || !bond.getConnectedAtom(carbon1).getSymbol().equals("C")) {
                toRemoveBond.add(bond);
                toRemoveAtom.add(bond.getConnectedAtom(carbon1));
            } else {
                bonds2C++;
            }
        }
        if (bonds2C > 1) {
            throw new LNetMoleculeGeneratorException("More than one single bond to a C, I don't know what to do here (Alkyl setter)");
        }
        for (IBond bond : toRemoveBond) {
            removeBond(bond, mol);
        }
        for (IAtom atom : toRemoveAtom) {
            removeAtom(atom, mol);
        }
    }

    public void setLinker(IMolecule mol, SingleLinkConfiguration linker) {
        IAtom carbon1 = mol.getFirstAtom();
        if (linker.equals(SingleLinkConfiguration.Acyl)) {
            setAcyl(mol, carbon1);
        } else if (linker.equals(SingleLinkConfiguration.Alkyl)) {
            setAlkyl(mol, carbon1);
        }
        throw new LNetMoleculeGeneratorException("Linker setter needs to be defined in the LinkerHandler classes for " + linker.name());
    }


}
