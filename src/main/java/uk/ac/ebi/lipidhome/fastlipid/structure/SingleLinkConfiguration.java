/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.structure;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
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
    private Integer heavyAtoms;
    
    SingleLinkConfiguration(String formula) {
        this.formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula(formula, SilentChemObjectBuilder.getInstance());
        this.mass = MolecularFormulaManipulator.getTotalExactMass(this.formula);
        this.heavyAtoms = AtomContainerManipulator.getHeavyAtoms(MolecularFormulaManipulator.getAtomContainer(this.formula)).size();
    }
    
    /**
     * The exact mass of the linkage, based on the formula.
     * 
     * @return Double with the mass of the link, based on its formula. 
     */
    public Double getMass() {
        return mass;
    }

    /**
     * 
     * The heavy atom (non hydrogen) count. 
     * 
     * @return int with the number of atoms
     */
    public int getHeavyAtomCount() {
        return heavyAtoms;
    }

}
