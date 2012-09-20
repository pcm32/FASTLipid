/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.structure;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 *
 * @author pmoreno
 */
public class StaticSmilesGenerator {

    private static SmilesGenerator sg = new SmilesGenerator();

    public static String getSmiles(IMolecule mol) {
        return sg.createSMILES(mol);
    }
}
