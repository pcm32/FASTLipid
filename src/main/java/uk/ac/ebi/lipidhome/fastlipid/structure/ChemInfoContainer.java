 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The purpose of the ChemInfoContainer is to produce results at the molecule/sub-specie level. The configuration of which
 * fields are computed for the ChemInfoContainer is handled by {@link ChemInfoContainerGenerator} class.
 * 
 * Currently, the fields are fixed in the ChemInfoContainer, and hence this produces great coupling with the 
 * ChemInfoContainerGenerator. This should be modified towards a more modular approach, where the ChemInfoContainer
 * and the ChemInfoContainerGenerator accept lists of Delegators/Properties calculators, instead of having the properties
 * that can or cannot be calculated hard coded in this classes (use dependency injection). We need a similar object for
 * population results.
 * 
 * @author pmoreno
 */
public class ChemInfoContainer {

    private double naturalMass;
    private String molecularFormula;
    private String inchi;
    private String inchiKey;
    private String auxInfo;
    private String smiles;
    private double exactMass;
    private HeadGroup hg;
    private List<SingleLinkConfiguration> linkers;
    
    private List<ChainInfoContainer> chains = new ArrayList<ChainInfoContainer>();

    public String getAuxInfo() {
        return auxInfo;
    }

    public void setAuxInfo(String auxInfo) {
        this.auxInfo = auxInfo;
    }

    public String getInchi() {
        return inchi;
    }

    public void setInchi(String inchi) {
        this.inchi = inchi;
    }

    public String getInchiKey() {
        return inchiKey;
    }

    public void setInchiKey(String inchiKey) {
        this.inchiKey = inchiKey;
    }

    public double getNaturalMass() {
        return naturalMass;
    }

    public void setNaturalMass(double naturalMass) {
        this.naturalMass = naturalMass;
    }

    public String getMolecularFormula() {
        return molecularFormula;
    }

    public void setMolecularFormula(String molecularFormula) {
        this.molecularFormula = molecularFormula;
    }

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    public void setExactMass(double exactMass) {
        this.exactMass = exactMass;
    }
    
    public double getExactMass() {
        return this.exactMass;
    }

    /**
     * @return the chains
     */
    public List<ChainInfoContainer> getChainsInfo() {
        return Collections.unmodifiableList(getChains());
    }

    public void setChainsInfo(List<ChainInfoContainer> chainsInfo) {
        this.getChains().clear();
        this.getChains().addAll(chainsInfo);
    }

    /**
     * @return the hg
     */
    public HeadGroup getHg() {
        return hg;
    }

    /**
     * @param hg the hg to set
     */
    public void setHg(HeadGroup hg) {
        this.hg = hg;
    }

    /**
     * @return the linkers
     */
    public List<SingleLinkConfiguration> getLinkers() {
        return linkers;
    }

    /**
     * @param linkers the linkers to set
     */
    public void setLinkers(List<SingleLinkConfiguration> linkers) {
        this.linkers = linkers;
    }

    /**
     * @return the chains
     */
    public List<ChainInfoContainer> getChains() {
        return chains;
    }

    /**
     * @param chains the chains to set
     */
    public void setChains(List<ChainInfoContainer> chains) {
        this.chains = chains;
    }

    
    
}
