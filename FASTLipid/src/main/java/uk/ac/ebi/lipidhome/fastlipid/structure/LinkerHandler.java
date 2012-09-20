/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.structure;

import org.openscience.cdk.interfaces.IMolecule;

/**
 *
 * @author pmoreno
 */
public interface LinkerHandler {
 
    public boolean checkLinker(IMolecule mol, SingleLinkConfiguration linker);
    public void setLinker(IMolecule mol, SingleLinkConfiguration linker);
    
}
