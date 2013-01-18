/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.mass;

/**
 *
 * @author pmoreno
 */
public class AtomNaturalMassCacheStaticDec {

    private static AtomNaturalMassCache atomNaturalMassCache = new AtomNaturalMassCache();

    public static AtomNaturalMassCache getCacheInstance() {
        return atomNaturalMassCache;
    }

}
