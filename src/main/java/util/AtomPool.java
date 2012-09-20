/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 *
 * @author pmoreno
 */
public class AtomPool extends ObjectPool<IAtom>{

    private IChemObjectBuilder builder;

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
