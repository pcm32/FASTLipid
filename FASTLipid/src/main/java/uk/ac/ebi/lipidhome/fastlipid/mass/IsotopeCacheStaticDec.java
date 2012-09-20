/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.mass;

/**
 *
 * @author pmoreno
 */
public class IsotopeCacheStaticDec {

    private static IsotopeInfoCache cache = new IsotopeInfoCache();

    public static IsotopeInfoCache getCacheInstance() {
        return cache;
    }
}
