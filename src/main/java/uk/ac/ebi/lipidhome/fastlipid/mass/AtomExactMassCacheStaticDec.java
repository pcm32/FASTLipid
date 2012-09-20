/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.mass;

/**
 *
 * @author pmoreno
 */
public class AtomExactMassCacheStaticDec {

    private static final AtomExactMassCache atomExactMassCache = new AtomExactMassCache();

    public static AtomExactMassCache getCacheInstance() {
        return atomExactMassCache;
    }

}
