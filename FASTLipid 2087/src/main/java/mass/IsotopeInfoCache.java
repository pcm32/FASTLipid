/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mass;

import java.util.Hashtable;

/**
 *
 * @author pmoreno
 */
public class IsotopeInfoCache {

    private Hashtable<String,Integer> symbol2AtomicNumber;
    private Hashtable<String,Double> symbol2ExactMass;
    private Hashtable<String,Double> symbol2NaturalAbundance;

    public IsotopeInfoCache() {
        this.symbol2AtomicNumber = new Hashtable<String, Integer>();
        this.symbol2ExactMass = new Hashtable<String, Double>();
        this.symbol2NaturalAbundance = new Hashtable<String, Double>();
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
