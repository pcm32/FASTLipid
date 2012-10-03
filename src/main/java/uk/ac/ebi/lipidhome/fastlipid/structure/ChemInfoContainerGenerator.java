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
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import uk.ac.ebi.lipidhome.fastlipid.mass.IsotopeCacheStaticDec;
import uk.ac.ebi.lipidhome.fastlipid.mass.MolMassCachedCalculator;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *
 * @author pmoreno
 */
public class ChemInfoContainerGenerator {

    private Boolean generateInChi;
    private Boolean generateInChiKey;
    private Boolean generateInChIAux;
    private Boolean generateMolFormula;
    private Boolean generateMass;
    private Boolean generateSmiles;
    private Boolean useCachedObjects=false;
    private InChIGeneratorFactory inchiGenFact;
    private InChIGenerator inChIGenerator;
    private Boolean generateChainInfoContainers;

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

    public void setGenerateInChIAux(Boolean generateInChIAux) {
        this.generateInChIAux = generateInChIAux;
    }

    public void setGenerateInChi(Boolean generateInChi) {
        this.generateInChi = generateInChi;
    }

    public void setGenerateInChiKey(Boolean generateInChiKey) {
        this.generateInChiKey = generateInChiKey;
    }

    public void setGenerateMass(Boolean generateMass) {
        this.generateMass = generateMass;
    }

    public void setGenerateMolFormula(Boolean generateMolFormula) {
        this.generateMolFormula = generateMolFormula;
    }

    public ChemInfoContainer generateChemInfoContainer(IAtomContainer molOriginal) {
        ChemInfoContainer container = new ChemInfoContainer();
        IAtomContainer mol=null;
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
                //atom.setPoint2d(null);
                if (atom.getNaturalAbundance() == null || !this.useCachedObjects) {
                    IIsotope major = null;
                    if (IsotopeCacheStaticDec.getCacheInstance().getAtomicNumberForSymbol(atom.getSymbol()) == null) {
                        try {
                            major = IsotopeFactory.getInstance(SilentChemObjectBuilder.getInstance()).getMajorIsotope(atom.getSymbol());
                        } catch (IOException ex) {
                            System.out.println("Could not read isotopes file");
                            System.exit(1);
                        }
                        Integer atomicNumber = major.getAtomicNumber();
                        IsotopeCacheStaticDec.getCacheInstance().setAtomicNumberForSymbol(atom.getSymbol(), atomicNumber);
                        atom.setAtomicNumber(atomicNumber);
                        Double exactMass = major.getExactMass();
                        IsotopeCacheStaticDec.getCacheInstance().setExactMassForSymbol(atom.getSymbol(), exactMass);
                        atom.setExactMass(exactMass);
                        Double natAbundance = major.getNaturalAbundance();
                        IsotopeCacheStaticDec.getCacheInstance().setNaturalAbundanceForSymbol(atom.getSymbol(), natAbundance);
                        atom.setNaturalAbundance(natAbundance);
                    } else {
                        atom.setAtomicNumber(IsotopeCacheStaticDec.getCacheInstance().getAtomicNumberForSymbol(atom.getSymbol()));
                        atom.setExactMass(IsotopeCacheStaticDec.getCacheInstance().getExactMassForSymbol(atom.getSymbol()));
                        atom.setNaturalAbundance(IsotopeCacheStaticDec.getCacheInstance().getNaturalAbundanceForSymbol(atom.getSymbol()));
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
                if(this.useCachedObjects) {
                    container.setNaturalMass(MolMassCachedCalculator.calcNaturalMass(mol));
                    container.setExactMass(MolMassCachedCalculator.calcExactMass(mol));
                }
                else {
                    container.setNaturalMass(AtomContainerManipulator.getNaturalExactMass(mol));
                    container.setExactMass(AtomContainerManipulator.getTotalExactMass(mol));
                }
            }
            if (this.getGenerateMolFormula()) {
                //IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(mol);
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

    public void setGenerateChainInfoContainers(boolean b) {
        this.generateChainInfoContainers = b;
    }

    boolean getGenerateChainInfoContainers() {
        return generateChainInfoContainers;
    }
}
