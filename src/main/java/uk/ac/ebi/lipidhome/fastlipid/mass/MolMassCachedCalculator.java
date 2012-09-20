/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.mass;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;

/**
 *
 * @author pmoreno
 */
public class MolMassCachedCalculator {

    public static double calcNaturalMass(IAtomContainer mol) {
        double mass = 0.0;
        for (IAtom atom : mol.atoms()) {
            mass += AtomNaturalMassCacheStaticDec.getCacheInstance().getNaturalMassForSymbol(atom.getSymbol());
        }
        return mass;
    }
    
    public static double calcNaturalMassGenericMol(IAtomContainer mol) {
        double mass = 0.0;
        for (IAtom atom : mol.atoms()) {
            if(atom instanceof IPseudoAtom)
                continue;
            mass += AtomNaturalMassCacheStaticDec.getCacheInstance().getNaturalMassForSymbol(atom.getSymbol());
        }
        return mass;
    }

    public static double calcExactMass(IAtomContainer mol) {
        double mass = 0.0;
        for (IAtom atom : mol.atoms()) {
            mass += AtomExactMassCacheStaticDec.getCacheInstance().getExactMassForSymbol(atom.getSymbol());
        }
        return mass;
    }
    
    public static double calcExactMassGenericMol(IAtomContainer mol) {
        double mass = 0.0;
        for (IAtom atom : mol.atoms()) {
            if(atom instanceof IPseudoAtom)
                continue;
            mass += AtomExactMassCacheStaticDec.getCacheInstance().getExactMassForSymbol(atom.getSymbol());
        }
        return mass;
    }
}
