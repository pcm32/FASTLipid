/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.util;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.AtomContainer;

/**
 *
 * @author pmoreno
 */
public class MoleculePool extends ObjectPool<IAtomContainer> {

    private IChemObjectBuilder builder;

    public MoleculePool(IChemObjectBuilder builder) {
        super();
        this.builder = builder;
    }

    @Override
    protected IAtomContainer create() {
        return builder.newInstance(AtomContainer.class);
        //return builder.newMolecule();
    }

    @Override
    public void expire(IAtomContainer o) {
        o=null;
    }
}
