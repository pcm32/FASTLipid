/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.structure;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *
 * @author pmoreno
 */
public interface LinkerHandler {
 
    public boolean checkLinker(IAtomContainer mol, SingleLinkConfiguration linker);
    public void setLinker(IAtomContainer mol, SingleLinkConfiguration linker);
    
}
