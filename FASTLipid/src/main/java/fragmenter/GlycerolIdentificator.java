/**
 * GlycerolIdentificator.java
 *
 * 2011.08.24
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
package fragmenter;

import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * @name    GlycerolIdentificator
 * @date    2011.08.24
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class GlycerolIdentificator {

    private static final Logger logger = Logger.getLogger(GlycerolIdentificator.class);
    private List<IAtom> carbonAtoms;
    private List<IAtom> oxygenAtoms;
    protected HashMap<AtomLabel, IAtom> label2atom;
    protected HashMap<BondLabel, IBond> label2bond;
    private IAtom secondaryCarbon;
    private IAtom primaryCarbon1;
    private IAtom primaryCarbon3;
    private IAtom secCarbOxygen;
    private IAtom primCarbOxygen1;
    private IAtom primCarbOxygen3;

    public enum AtomLabel {

        GlycerolSecondaryCarbon,
        GlycerolPrimaryCarbon,
        GlycerolSecCarbonOxygen,
        GlycerolPrimCarbonOxygen;
    }

    public enum BondLabel {

        PrimCarbon2Oxygen,
        SecCarbon2Oxygen, PrimCarbSecCarb;
    }

    public GlycerolIdentificator() throws CDKException {
        /*MDLV2000Reader reader = new MDLV2000Reader(new InputStreamReader(
        GlycerolIdentificator.class.getResourceAsStream("structures/models/"+templateName+".mol")));
        this.template = reader.read(
        NoNotificationChemObjectBuilder.getInstance().newInstance(Molecule.class));*/
    }

    /**
     * Receives the molecule where the glycerol must be tagged and the potential
     * secondary (central) carbon of it. This method should be iterated over all
     * carbon atoms of the molecule, leaving to the user the responsability to decide
     * how many of this substructure should be found. The tags are left 
     * as properties in the bonds and atoms.
     * 
     * @param mol 
     */
    public void identify(IAtomContainer mol, IAtom carbon) {

        if (carbon.getSymbol().equals("C") && mol.contains(carbon)) {
            try {
                if (canBeGlycerolCenter(mol, carbon)) {
                    int foundPrimaryCarbons = 0;
                    int foundOxygen = 0;
                    for (IAtom con2carb : mol.getConnectedAtomsList(carbon)) {
                        try {
                            if (con2carb.getSymbol().equals("C")
                                    && canBeGlycerolPrimaryCarbon(mol, con2carb, carbon)) {
                                con2carb.setProperty(AtomLabel.GlycerolPrimaryCarbon, true);
                                foundPrimaryCarbons++;
                            }
                        } catch (CDKException e) {
                            logger.warn("Found more than two primary carbons attached to \"secondary\" carbon", e);
                            foundPrimaryCarbons = 3;
                        }

                    }
                    if (foundPrimaryCarbons == 2 && this.secondaryCarbon != null
                            && this.primCarbOxygen1 != null && this.primaryCarbon3 != null) {
                        secondaryCarbon.setProperty(AtomLabel.GlycerolSecondaryCarbon, true);
                        primaryCarbon1.setProperty(AtomLabel.GlycerolPrimaryCarbon, true);
                        primaryCarbon3.setProperty(AtomLabel.GlycerolPrimaryCarbon, true);
                        secCarbOxygen.setProperty(AtomLabel.GlycerolSecCarbonOxygen, true);
                        primCarbOxygen1.setProperty(AtomLabel.GlycerolPrimCarbonOxygen, true);
                        primCarbOxygen3.setProperty(AtomLabel.GlycerolPrimCarbonOxygen, true);

                        mol.getBond(secondaryCarbon, secCarbOxygen).setProperty(BondLabel.SecCarbon2Oxygen, true);
                        mol.getBond(secondaryCarbon, primaryCarbon1).setProperty(BondLabel.PrimCarbSecCarb, true);
                        mol.getBond(secondaryCarbon, primaryCarbon3).setProperty(BondLabel.PrimCarbSecCarb, true);
                        mol.getBond(primaryCarbon1, primCarbOxygen1).setProperty(BondLabel.PrimCarbon2Oxygen, true);
                        mol.getBond(primaryCarbon3, primCarbOxygen3).setProperty(BondLabel.PrimCarbon2Oxygen, true);
                        
                        secondaryCarbon=null;
                        secCarbOxygen=null;
                        primaryCarbon1=null;
                        primaryCarbon3=null;
                        primCarbOxygen1=null;
                        primCarbOxygen3=null;
                        
                    }
                }
            } catch (CDKException ex) {
                logger.warn("Secondary carbon already assigned", ex);
            }
        }

    }

    private boolean canBeGlycerolPrimaryCarbon(IAtomContainer mol, IAtom candPrimaryCarbon, IAtom candSecondaryCarbon) throws CDKException {
        int oxygens = 0;
        int carbons = 0;
        int hydrogens = 0;
        IAtom oxygenAtom = null;
        for (IAtom conAtom : mol.getConnectedAtomsList(candPrimaryCarbon)) {
            if (conAtom.getSymbol().equals("C")) {
                carbons++;
            } else if (conAtom.getSymbol().equals("O")
                    && mol.getBond(conAtom, candPrimaryCarbon).getOrder().equals(IBond.Order.SINGLE)
                    && mol.getBond(conAtom, candSecondaryCarbon) == null) {
                oxygenAtom = conAtom;
            }
        }
        if (oxygenAtom != null && carbons == 1 && this.getNumberOfHydrogensOrMinusCharges(mol, candPrimaryCarbon) == 2) {
            /*mol.getBond(oxygenAtom, candPrimaryCarbon).setProperty(BondLabel.PrimCarbon2Oxygen, true);
            oxygenAtom.setProperty(AtomLabel.GlycerolPrimCarbonOxygen, true);
            mol.getBond(candPrimaryCarbon, candSecondaryCarbon).setProperty(BondLabel.PrimCarbSecCarb, hydrogens);*/
            if (primaryCarbon1 == null) {
                primaryCarbon1 = candPrimaryCarbon;
                primCarbOxygen1 = oxygenAtom;
            } else if (primCarbOxygen3 == null) {
                primaryCarbon3 = candPrimaryCarbon;
                primCarbOxygen3 = oxygenAtom;
            } else {
                // third primary carbon found, this shouldn't be
                throw new CDKException("third primary carbon found for what should be a secondary carbon");
            }

            return true;
        }
        return false;

    }

    private boolean canBeGlycerolCenter(IAtomContainer mol, IAtom carbonAtom) throws CDKException {
        int carbonsAttached = 0;
        int oxygensAttached = 0;
        int hydrogensAttached = 0;
        IAtom oxygenAtom = null;
        for (IAtom con : mol.getConnectedAtomsList(carbonAtom)) {
            if (con.getSymbol().equals("C")
                    && mol.getBond(carbonAtom, con).getOrder().equals(IBond.Order.SINGLE)) {
                carbonsAttached++;
            } else if (con.getSymbol().equals("O")
                    && mol.getBond(carbonAtom, con).getOrder().equals(IBond.Order.SINGLE)) {
                oxygenAtom = con;
                oxygensAttached++;
            } 
        }

        if (carbonsAttached == 2 && oxygenAtom != null && oxygensAttached == 1 && getNumberOfHydrogensOrMinusCharges(mol, carbonAtom)==2) {
            /*mol.getBond(carbonAtom, oxygenAtom).setProperty(BondLabel.SecCarbon2Oxygen, true);
            oxygenAtom.setProperty(AtomLabel.GlycerolSecCarbonOxygen, true);
            carbonAtom.setProperty(AtomLabel.GlycerolSecondaryCarbon, mol);*/
            if (secondaryCarbon == null) {
                secondaryCarbon = carbonAtom;
                secCarbOxygen = oxygenAtom;
            } else {
                throw new CDKException("Secondary carbon already assigned.");
            }
            return true;
        }
        return false;
    }

    public Integer getNumberOfHydrogensOrMinusCharges(IAtomContainer mol, IAtom atom) {
        Integer hydrogenCount = atom.getImplicitHydrogenCount();
        for (IAtom conAtom : mol.getConnectedAtomsList(atom)) {
            if (conAtom.getSymbol().equals("H")) {
                hydrogenCount++;
            }
        }
        return hydrogenCount + atom.getFormalCharge() * -1;

    }
}
