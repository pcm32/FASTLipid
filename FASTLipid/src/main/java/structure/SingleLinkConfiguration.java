/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package structure;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * Make sure that this is properly used to set the first bond.
 * 
 * @author pmoreno
 */
public enum SingleLinkConfiguration {

    Acyl("CO"),Alkyl("CH2");
    
    private IMolecularFormula formula;
    private Double mass;
    
    SingleLinkConfiguration(String formula) {
        this.formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula(formula, SilentChemObjectBuilder.getInstance());
        this.mass = MolecularFormulaManipulator.getTotalExactMass(this.formula);
    }
    
    public Double getMass() {
        return mass;
    }

}
