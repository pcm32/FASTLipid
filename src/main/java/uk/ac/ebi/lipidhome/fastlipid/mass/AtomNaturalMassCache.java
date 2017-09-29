/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.mass;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.openscience.cdk.Element;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * A cache for atom masses to speed up the process of retrieving masses from CDK.
 *
 * @author pmoreno
 */
public class AtomNaturalMassCache {

    private Map<String, Double> symbol2NaturalMass;
    
    private static AtomNaturalMassCache instance;
    
    public static AtomNaturalMassCache getInstance() {
        if(instance==null)
            instance = new AtomNaturalMassCache();
        return instance;
    }

    private AtomNaturalMassCache() {
        this.symbol2NaturalMass = new HashMap<String, Double>();
    }

    /**
     * Given an element symbol (C,O,H,N,etc.), it retrieves the natural mass. If the symbol has been seen before by this
     * cache, then the stored mass is retrieved. Else, the IsotopeFactory is used.
     * 
     * @param symbol
     * @return exact mass for the symbol.
     */
    public Double getNaturalMassForSymbol(String symbol) {
        if (this.symbol2NaturalMass.get(symbol) == null) {
            this.symbol2NaturalMass.put(symbol, this.massForSymbol(symbol));
        }
        return this.symbol2NaturalMass.get(symbol);
    }

//    public void setNaturalMassForSymbol(String symbol, Double exactMass) {
//        this.symbol2NaturalMass.put(symbol, exactMass);
//    }

    private Double massForSymbol(String symbol) {
        double mass = 0.0;
        IsotopeFactory factory;
        try {
            factory = Isotopes.getInstance();
        } catch (IOException e) {
            throw new RuntimeException("Could not instantiate the IsotopeFactory.");
        }
        // We'll have to change this for CDK 1.3.8
        IElement isotopesElement = SilentChemObjectBuilder.getInstance().newInstance(IElement.class);
        isotopesElement.setSymbol(symbol);
        mass += factory.getNaturalMass(isotopesElement);

        return mass;
    }
}
