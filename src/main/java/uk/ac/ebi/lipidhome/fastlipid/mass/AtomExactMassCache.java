/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.mass;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;

/**
 * A cache for atom masses to speed up the process of retrieving masses from CDK.
 * 
 * @author pmoreno
 */
public class AtomExactMassCache {

    private Map<String, Double> symbol2exactMass;
    
    private static AtomExactMassCache instance;
    
    public static AtomExactMassCache getInstance() {
        if(instance==null)
            instance = new AtomExactMassCache();
        return instance;
    }

    /**
     * Constructor.
     */
    public AtomExactMassCache() {
        this.symbol2exactMass = new HashMap<String, Double>();
    }

    /**
     * Given an element symbol (C,O,H,N,etc.), it retrieves the exact mass. If the symbol has been seen before by this
     * cache, then the stored mass is retrieved. Else, the IsotopeFactory is used.
     * 
     * @param symbol
     * @return exact mass for the symbol.
     */
    public Double getExactMassForSymbol(String symbol) {
        if (this.symbol2exactMass.get(symbol) == null) {
            this.symbol2exactMass.put(symbol, this.exactMassForSymbol(symbol));
        }
        return this.symbol2exactMass.get(symbol);
    }

    private Double exactMassForSymbol(String symbol) {
        double mass = 0.0;
        IsotopeFactory factory;
        try {
            factory = Isotopes.getInstance();
        } catch (IOException e) {
            throw new RuntimeException("Could not instantiate the IsotopeFactory.");
        }
        mass += factory.getMajorIsotope(symbol).getExactMass();

        return mass;
    }
}
