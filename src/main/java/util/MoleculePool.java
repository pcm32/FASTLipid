/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;

/**
 *
 * @author pmoreno
 */
public class MoleculePool extends ObjectPool<IMolecule> {

    private IChemObjectBuilder builder;

    public MoleculePool(IChemObjectBuilder builder) {
        super();
        this.builder = builder;
    }

    @Override
    protected IMolecule create() {
        return builder.newInstance(Molecule.class);
        //return builder.newMolecule();
    }


    @Override
    public void expire(IMolecule o) {
        o=null;
    }

}
