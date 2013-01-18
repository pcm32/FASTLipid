/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.mass;

/**
 * Describes a mass range, for query purposes.
 *
 * @author pmoreno
 */
public interface MassRange {
    
    /**
     * 
     * @return The minimum mass of the range. 
     */
    public Double getMinMass();
    /**
     * 
     * @return The maximum mass of the range. 
     */
    public Double getMaxMass();
    
}
