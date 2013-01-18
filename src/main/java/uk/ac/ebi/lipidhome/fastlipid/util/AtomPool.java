/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.util;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * A pool of CDK IAtoms 
 * 
 * @author pmoreno
 */
public class AtomPool extends ObjectPool<IAtom>{

    private IChemObjectBuilder builder;

    /**
     * Initializes the pool with a defined ChemObjectBuilder.
     * 
     * @param builder the builder to build atoms in this pool.
     */
    public AtomPool(IChemObjectBuilder builder) {
        super();
        this.builder = builder;
    }

    @Override
    protected IAtom create() {
        return builder.newInstance(Atom.class);
    }

    @Override
    public void expire(IAtom o) {
        o=null;
    }

}
