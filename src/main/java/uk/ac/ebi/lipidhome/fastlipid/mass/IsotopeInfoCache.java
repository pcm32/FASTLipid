/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.mass;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pmoreno
 */
public class IsotopeInfoCache {

    private Map<String,Integer> symbol2AtomicNumber;
    private Map<String,Double> symbol2ExactMass;
    private Map<String,Double> symbol2NaturalAbundance;
    
    private static IsotopeInfoCache instance;
    
    public static IsotopeInfoCache getInstance() {
        if(instance==null)
            instance = new IsotopeInfoCache();
        return instance;
    }

    public IsotopeInfoCache() {
        this.symbol2AtomicNumber = new HashMap<String, Integer>();
        this.symbol2ExactMass = new HashMap<String, Double>();
        this.symbol2NaturalAbundance = new HashMap<String, Double>();
    }

    public Integer getAtomicNumberForSymbol(String symbol) {
        return this.symbol2AtomicNumber.get(symbol);
    }

    public void setAtomicNumberForSymbol(String symbol, Integer atomicNumber) {
        this.symbol2AtomicNumber.put(symbol, atomicNumber);
    }

    public Double getExactMassForSymbol(String symbol) {
        return this.symbol2ExactMass.get(symbol);
    }

    public void setExactMassForSymbol(String symbol, Double exactMass) {
        this.symbol2ExactMass.put(symbol, exactMass);
    }

    public Double getNaturalAbundanceForSymbol(String symbol) {
        return this.symbol2NaturalAbundance.get(symbol);
    }

    public void setNaturalAbundanceForSymbol(String symbol,Double naturalAbundance) {
        this.symbol2NaturalAbundance.put(symbol, naturalAbundance);
    }
}
