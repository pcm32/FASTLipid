/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mass;

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
public class AtomNaturalMassCache {

    private Map<String, Double> symbol2NaturalMass;

    public AtomNaturalMassCache() {
        this.symbol2NaturalMass = new HashMap<String, Double>();
    }

    public Double getNaturalMassForSymbol(String symbol) {
        if (this.symbol2NaturalMass.get(symbol) == null) {
            this.symbol2NaturalMass.put(symbol, this.massForSymbol(symbol));
        }
        return this.symbol2NaturalMass.get(symbol);
    }

    public void setNaturalMassForSymbol(String symbol, Double exactMass) {
        this.symbol2NaturalMass.put(symbol, exactMass);
    }

    private Double massForSymbol(String symbol) {
        double mass = 0.0;
        IsotopeFactory factory;
        try {
            factory = IsotopeFactory.getInstance(SilentChemObjectBuilder.getInstance());
        } catch (IOException e) {
            throw new RuntimeException("Could not instantiate the IsotopeFactory.");
        }
        // We'll have to change this for CDK 1.3.8
        IElement isotopesElement = SilentChemObjectBuilder.getInstance().newInstance(Element.class);
        //IElement isotopesElement = SilentChemObjectBuilder.getInstance().newElement();
        isotopesElement.setSymbol(symbol);
        mass += factory.getNaturalMass(isotopesElement);

        return mass;
    }
}
