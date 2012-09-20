/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.structure;

import java.io.IOException;
import java.io.InputStream;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.MDLV2000Reader;

/**
 * HeadGroup enumeration represents possible head groups to be used in enumerations. Each head group defined needs to 
 * have a MDL mol file with the <same name>.mol in the structure.models package. The head group initialization includes 
 * the number of slots for FattyAcids.
 *
 * @author pmoreno
 */
public enum HeadGroup {

    DG(3), Glycerol(3), PA(2), PC(2), PE(2), PG(2), PI(2), PS(2),
    DG12(2), DG13(2), LPA1(1), LPC1(1), LPE1(1), LPG1(1), LPI1(1), LPS1(1),
    MG1(1), LPA2(1), LPC2(1), LPE2(1), LPG2(1), LPI2(1), LPS2(1), MG2(1);
    
    private Integer numOfFattyAcidSlots;
    
    /**
     * Internal constructor, taking the number of slots of fatty acids that the Head group has.
     * @param numOfSlots 
     */
    HeadGroup(Integer numOfSlots) {
        this.numOfFattyAcidSlots = numOfSlots;
    }

    /**
     * Reads the IAtomContainer from the structures.models package for the HeadGroup. The molecule is built with
     * the provided builder. The MDL Mol file is read each time that the method is invoked (this is probably not the
     * ideal behaviour).
     * 
     * @param builder
     * @return IAtomContainer molecule for the head group.
     * @throws CDKException produced by the CDK MDLV2000Reader
     * @throws IOException produced by the CDK MDLV2000Reader
     */
    public IAtomContainer getHeadMolecule(IChemObjectBuilder builder) throws CDKException, IOException {
        InputStream headMolStream = getHeadMolStream();
        MDLV2000Reader reader = new MDLV2000Reader(headMolStream);

        IAtomContainer mol = reader.read(builder.newInstance(IAtomContainer.class));

        reader.close();
        headMolStream.close();
        
        return mol;
    }
    
    /**
     * Produces an inputstream that allows to read the MDL Mol file for the head group.
     * @return 
     */
    public InputStream getHeadMolStream() {
        return HeadGroup.class.getResourceAsStream("/structures/models/" + this.name() + ".mol");
    }
    
    /**
     * Returns the number of slots for fatty acids that this head group has.
     * 
     * @return Integer number of fatty acids that can be attached to this head group.
     */
    public Integer getNumOfSlots() {
        return this.numOfFattyAcidSlots;
    }
}
