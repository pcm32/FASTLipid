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
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 *
 * @author pmoreno
 */
public class AtomExactMassCache {

    private Map<String, Double> symbol2exactMass;

    public AtomExactMassCache() {
        this.symbol2exactMass = new HashMap<String, Double>();
    }

    public Double getExactMassForSymbol(String symbol) {
        if (this.symbol2exactMass.get(symbol) == null) {
            this.symbol2exactMass.put(symbol, this.exactMassForSymbol(symbol));
        }
        return this.symbol2exactMass.get(symbol);
    }

    public void setExactMassForSymbol(String symbol, Double exactMass) {
        this.symbol2exactMass.put(symbol, exactMass);
    }

    private Double exactMassForSymbol(String symbol) {
        double mass = 0.0;
        IsotopeFactory factory;
        try {
            factory = IsotopeFactory.getInstance(SilentChemObjectBuilder.getInstance());
        } catch (IOException e) {
            throw new RuntimeException("Could not instantiate the IsotopeFactory.");
        }
        mass += factory.getMajorIsotope(symbol).getExactMass();

        return mass;
    }
}
