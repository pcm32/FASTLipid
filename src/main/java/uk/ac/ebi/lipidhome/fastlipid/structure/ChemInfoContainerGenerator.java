/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.structure;

import java.io.IOException;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import uk.ac.ebi.lipidhome.fastlipid.mass.IsotopeInfoCache;
import uk.ac.ebi.lipidhome.fastlipid.mass.MolMassCachedCalculator;

/**
 * Generator that produces the ChemInfoContainer, one of the central objects of the project. This generator is configured
 * to set which data types/descriptors should be computed for the resulting molecule (of the enumeration). That data are
 * stored in the ChemInfoContainer. The rationality here is that the least descriptors calculated, the fastest the
 * enumeration. Only the minimally required should be generated.
 * 
 * TODO InChI support might be broken, requires to be checked/completed/fixed.
 *
 * @author pmoreno
 */
public class ChemInfoContainerGenerator {

    private Boolean generateInChi = false;
    private Boolean generateInChiKey = false;
    private Boolean generateInChIAux = false;
    private Boolean generateMolFormula = false;
    private Boolean generateMass = false;
    private Boolean generateSmiles = false;
    private Boolean useCachedObjects = false;
    private Boolean generateCDKMol = false;
    private InChIGeneratorFactory inchiGenFact;
    private InChIGenerator inChIGenerator;
    private Boolean generateChainInfoContainers = false;

    private void init() {
        //this.inchiGenFact = InChIGeneratorFactory.getInstance();
        try {
            //this.inchiGenFact = new InChIGeneratorFactory();
            this.inchiGenFact = InChIGeneratorFactory.getInstance();
            //this.inChIGenerator = null;
        } catch (Exception e) {
            //System.err.println("Exception inchi:"+e.getMessage());
        }

        this.useCachedObjects = false;
        this.generateSmiles = false;
        this.generateChainInfoContainers = false;
    }

    public ChemInfoContainerGenerator() {
        this.init();
    }

    /**
     * Set to true if the InChI Aux String should be generated for the molecule.
     * 
     * @param generateInChIAux 
     */
    public void setGenerateInChIAux(Boolean generateInChIAux) {
        this.generateInChIAux = generateInChIAux;
    }

    /**
     * Set to true if the InChI string should be generated for the molecule.
     * 
     * @param generateInChi 
     */
    public void setGenerateInChi(Boolean generateInChi) {
        this.generateInChi = generateInChi;
    }

    /**
     * Set to true if the InChI Key should be generated for the molecule.
     * 
     * @param generateInChiKey 
     */
    public void setGenerateInChiKey(Boolean generateInChiKey) {
        this.generateInChiKey = generateInChiKey;
    }

    /**
     * Set to true to generate the mass (TODO which mass, natural or exact?).
     * @param generateMass 
     */
    public void setGenerateMass(Boolean generateMass) {
        this.generateMass = generateMass;
    }

    public void setGenerateMolFormula(Boolean generateMolFormula) {
        this.generateMolFormula = generateMolFormula;
    }

    /**
     * Produces a ChemInfoContainer which contains the data of the generated molecule according to the settings of this
     * object.
     * 
     * @param molOriginal to produce the data from
     * @return ChemInfoContainer with all the requested data of the original mol.
     */
    public ChemInfoContainer generateChemInfoContainer(IAtomContainer molOriginal) {
        // TODO This method is extremely long and complicated, improve it.
        ChemInfoContainer container = new ChemInfoContainer();
        try {
            if (this.generateCDKMol) {
                container.setCDKMolecule((IAtomContainer) molOriginal.clone());
            }
        } catch (CloneNotSupportedException e) {
            System.out.println("Not clonable" + e.getMessage());
        }
        IAtomContainer mol = null;
        if (this.generateInChi || this.generateSmiles || this.generateMolFormula || this.generateMass) {
            try {
                mol = (IAtomContainer) molOriginal.clone();
                //mol = molOriginal;
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            } catch (CDKException ex) {
                System.out.println("Troubles here:" + ex.getMessage());
            } catch (CloneNotSupportedException e) {
                System.out.println("Not clonable" + e.getMessage());
            }
        }
        try {
            if (this.getGenerateInChi()) {
                this.inChIGenerator = inchiGenFact.getInChIGenerator(mol);
                container.setInchi(this.inChIGenerator.getInchi());
                if (this.getGenerateInChiKey()) {
                    container.setInchiKey(this.inChIGenerator.getInchiKey());
                }
                if (this.getGenerateInChIAux()) {
                    container.setAuxInfo(this.inChIGenerator.getAuxInfo());
                }
            }
            if (this.generateSmiles) {
                container.setSmiles(StaticSmilesGenerator.getSmiles(mol));
            }
        } catch (CDKException ex) {
        }

        if (this.getGenerateMass() || this.getGenerateMolFormula()) {

            for (IAtom atom : mol.atoms()) {
                if (atom.getNaturalAbundance() == null || !this.useCachedObjects) {
                    IIsotope major = null;
                    IsotopeInfoCache isotopeCache = IsotopeInfoCache.getInstance();
                    if (isotopeCache.getAtomicNumberForSymbol(atom.getSymbol()) == null) {
                        try {
                            major = IsotopeFactory.getInstance(SilentChemObjectBuilder.getInstance()).getMajorIsotope(atom.getSymbol());
                        } catch (IOException ex) {
                            System.out.println("Could not read isotopes file");
                            System.exit(1);
                        }
                        Integer atomicNumber = major.getAtomicNumber();
                        isotopeCache.setAtomicNumberForSymbol(atom.getSymbol(), atomicNumber);
                        atom.setAtomicNumber(atomicNumber);
                        Double exactMass = major.getExactMass();
                        isotopeCache.setExactMassForSymbol(atom.getSymbol(), exactMass);
                        atom.setExactMass(exactMass);
                        Double natAbundance = major.getNaturalAbundance();
                        isotopeCache.setNaturalAbundanceForSymbol(atom.getSymbol(), natAbundance);
                        atom.setNaturalAbundance(natAbundance);
                    } else {
                        atom.setAtomicNumber(isotopeCache.getAtomicNumberForSymbol(atom.getSymbol()));
                        atom.setExactMass(isotopeCache.getExactMassForSymbol(atom.getSymbol()));
                        atom.setNaturalAbundance(isotopeCache.getNaturalAbundanceForSymbol(atom.getSymbol()));
                    }
                }

            }
            try {
                CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(mol);
                AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
            } catch (CDKException ex) {
                System.out.println("Troubles here:" + ex.getMessage());
            }

            if (this.getGenerateMass()) {
                if (this.useCachedObjects) {
                    // TODO if the atoms where previously configured with their masses, 
                    // do we need to use the MolMassCachedCalculator??
                    container.setNaturalMass(MolMassCachedCalculator.calcNaturalMass(mol));
                    container.setExactMass(MolMassCachedCalculator.calcExactMass(mol));
                } else {
                    container.setNaturalMass(AtomContainerManipulator.getNaturalExactMass(mol));
                    container.setExactMass(AtomContainerManipulator.getTotalExactMass(mol));
                }
            }
            if (this.getGenerateMolFormula()) {
                container.setMolecularFormula(MolecularFormulaManipulator.getString(MolecularFormulaManipulator.getMolecularFormula(mol)));
            }

            // if we are not duplicating the molecule, we need to get rid of hydrogens.
            AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(mol);
        }
        
        return container;
    }

    /**
     * @return the generateInChi
     */
    public Boolean getGenerateInChi() {
        return generateInChi;
    }

    /**
     * @return the generateInChiKey
     */
    public Boolean getGenerateInChiKey() {
        return generateInChiKey;
    }

    /**
     * @return the generateInChIAux
     */
    public Boolean getGenerateInChIAux() {
        return generateInChIAux;
    }

    /**
     * @return the generateMolFormula
     */
    public Boolean getGenerateMolFormula() {
        return generateMolFormula;
    }

    /**
     * @return the generateMass
     */
    public Boolean getGenerateMass() {
        return generateMass;
    }

    /**
     * @return the generateSmiles
     */
    public Boolean getGenerateSmiles() {
        return generateSmiles;
    }

    /**
     * @param generateSmiles the generateSmiles to set
     */
    public void setGenerateSmiles(Boolean generateSmiles) {
        this.generateSmiles = generateSmiles;
    }

    /**
     * @param useCachedObjects the useCachedObjects to set
     */
    public void setUseCachedObjects(Boolean useCachedObjects) {
        this.useCachedObjects = useCachedObjects;
    }

    /**
     * Sets whether {@link ChainInfoContainer} should be generated. 
     * @param generateContainers true to generate ChainInfoContainers.
     */
    public void setGenerateChainInfoContainers(boolean generateContainers) {
        this.generateChainInfoContainers = generateContainers;
    }

    boolean getGenerateChainInfoContainers() {
        return generateChainInfoContainers;
    }

    /**
     * Whether a clone of the generated molecule should be stored in the cheminfo container.
     *
     * @param b
     */
    public void setGenerateCDKMol(boolean b) {
        this.generateCDKMol = b;
    }
}
