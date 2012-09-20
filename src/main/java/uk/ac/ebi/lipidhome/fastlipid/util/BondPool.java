/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.util;

import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 *
 * @author pmoreno
 */
public class BondPool extends ObjectPool<IBond>{


    private IChemObjectBuilder builder;

    public BondPool(IChemObjectBuilder builder) {
        super();
        this.builder = builder;
    }

    @Override
    protected IBond create() {
        return builder.newInstance(Bond.class);
    }

    @Override
    public void expire(IBond o) {
        o=null;
    }

}
