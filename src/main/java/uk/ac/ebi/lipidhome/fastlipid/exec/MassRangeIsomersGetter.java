/**
 * MassRangeIsomersGetter.java
 *
 * 2012.09.03
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with CheMet. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import java.io.IOException;
import uk.ac.ebi.lipidhome.fastlipid.mass.FutureEstimatesIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import uk.ac.ebi.lipidhome.fastlipid.generator.ChainFactoryGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.GeneralIsomersGenerator;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import uk.ac.ebi.lipidhome.fastlipid.mass.*;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SpeciesInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.util.LipidChainConfigEstimate;

/**
 * @name MassRangeIsomersGetter @date 2012.09.03
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) 
 * @brief This class is executable. It receives the desired head group, desired links,
 * and minimum and maximum mass. This class is meant for a short mass range. It should return the different possible
 * configurations and the number of molecules they could have. This second case can only be done if the
 * GeneralIsomersGenerator or equivalent used can produce the mass, to get rid of the cases that fail the mass
 * boundaries.
 *
 */
public class MassRangeIsomersGetter implements Iterator<SpeciesInfoContainer> {

    //private static final Logger LOGGER = Logger.getLogger(MassRangeIsomersGetter.class);
    private Iterator<LipidChainConfigEstimate> estimatesIterator;
    private Iterator<GeneralIsomersGenerator> generatorIterator;
    private GeneralIsomersGenerator generator;
    private Double minMass;
    private Double maxMass;
    private LipidChainConfigEstimate currentEstimate;
    private SpeciesInfoContainer currentResult;
    private final List<SingleLinkConfiguration> allowedLinkers;
    private final ChainFactoryGenerator cfGenerator;
    private Boolean exoticModeOn;
    private final ChemInfoContainerGenerator cicg;
    private MassDeviationCalculator devCalculator;
    private boolean calculateDeviation = false;

    /**
     * Setups the iteration process, using the ChainEstimatorByMass to decide which carbons and double bonds ranges to
     * use, according to the given head, linkers, mode and mass range given.
     *
     * @param allowedHeadGroups a list of head groups that can be used.
     * @param allowedLinkers a list of linkers that can be used.
     * @param minMass of the mass range.
     * @param maxMass of the mass range.
     * @throws CDKException
     * @throws IOException 
     */
    public MassRangeIsomersGetter(List<HeadGroup> allowedHeadGroups, List<SingleLinkConfiguration> allowedLinkers, ChainFactoryGenerator cfGenerator,
            Double minMass, Double maxMass, Boolean exoticModeOn, ChemInfoContainerGenerator cicg) throws CDKException, IOException {
        this.maxMass = maxMass;
        this.minMass = minMass;
        this.allowedLinkers = allowedLinkers;
        this.cfGenerator = cfGenerator;
        this.exoticModeOn = exoticModeOn;
        this.cicg = cicg;

        List<Future<LipidChainConfigEstimate>> estimates = new ArrayList<Future<LipidChainConfigEstimate>>();
        ExecutorService execServ = Executors.newFixedThreadPool(4);
        for (HeadGroup headGroup : allowedHeadGroups) {
            ChainEstimatorByMass estimatorByMass = new ChainEstimatorByMass(minMass, maxMass, headGroup, allowedLinkers);
            estimates.add(execServ.submit(estimatorByMass));
        }
        execServ.shutdown();

        this.estimatesIterator = new FutureEstimatesIterator(estimates);
        resetGeneratorIterator(allowedLinkers, cfGenerator);
        getNextGenerator();
    }

    /**
     * Setups the iteration process, using the ChainEstimatorByMass to decide which carbons and double bonds ranges to
     * use, according to the given head, linkers, mode and mass range given.
     * 
     * @param allowedHeadGroups a list of head groups that can be used.
     * @param allowedLinkers a list of linkers that can be used.
     * @param cfGenerator {@link ChainFactoryGenerator} with settings on how to generate the fatty acid chains.
     * @param range {@link MassRange} object to bound the mass range of the getter.
     * @param exoticModeOn true if odd length chains are allowed.
     * @param cicg {@link ChemInfoContainerGenerator} which is configured with the outputs desired (mass, SMILES, etc.)
     * @throws CDKException
     * @throws IOException 
     */
    public MassRangeIsomersGetter(List<HeadGroup> allowedHeadGroups, List<SingleLinkConfiguration> allowedLinkers, ChainFactoryGenerator cfGenerator,
            MassRange range, Boolean exoticModeOn, ChemInfoContainerGenerator cicg) throws CDKException, IOException {
        this(allowedHeadGroups, allowedLinkers, cfGenerator, range.getMinMass(), range.getMaxMass(), exoticModeOn, cicg);
    }
    
    /**
     * Setups the iteration process, using the ChainEstimatorByMass to decide which carbons and double bonds ranges to
     * use, according to the given head, linkers, mode and mass range given. When given a {@link PPMBasedMassRange}, a
     * mass deviation calculator is initialized internally.
     * 
     * @param allowedHeadGroups a list of head groups that can be used.
     * @param allowedLinkers a list of linkers that can be used.
     * @param cfGenerator {@link ChainFactoryGenerator} with settings on how to generate the fatty acid chains.
     * @param range {@link PPMBasedMassRange} object to bound the mass range of the getter.
     * @param exoticModeOn true if odd length chains are allowed.
     * @param cicg {@link ChemInfoContainerGenerator} which is configured with the outputs desired (mass, SMILES, etc.)
     * @throws CDKException
     * @throws IOException 
     */
    public MassRangeIsomersGetter(List<HeadGroup> allowedHeadGroups, List<SingleLinkConfiguration> allowedLinkers, ChainFactoryGenerator cfGenerator,
            PPMBasedMassRange range, Boolean exoticModeOn, ChemInfoContainerGenerator cicg) throws CDKException, IOException {
        this(allowedHeadGroups, allowedLinkers, cfGenerator, range.getMinMass(), range.getMaxMass(), exoticModeOn, cicg);
        this.devCalculator = new MassDeviationCalculator(range.getQueriedMass()); //removed redundant cast :) - jm
        this.calculateDeviation = true;
    }

    /**
     * Restarts the generatorIterator based on a new set of estimates.
     *
     * @param allowedLinkers
     * @param cfGenerator
     */
    private void resetGeneratorIterator(List<SingleLinkConfiguration> allowedLinkers, ChainFactoryGenerator cfGenerator) {
        if (this.estimatesIterator.hasNext()) {
            currentEstimate = this.estimatesIterator.next();
            this.generatorIterator = new EstimateBasedGeneratorIterator(currentEstimate, allowedLinkers, cfGenerator, cicg, exoticModeOn);
        } else {
            this.generator = null;
        }
    }

    /**
     * Retrieves the next result as a {@link SpeciesInfoContainer}. Each mass estimation can generate a number of generators
     * (different head, linkers, carbons and double bonds configs), which are iterated within this method.
     * @return 
     */
    public SpeciesInfoContainer next() {
        /**
         * First we need to get the generator to use on this iteration. The generator comes initialized and ready to
         * use.
         */
        SpeciesInfoContainer toRet = currentResult;
        if (this.generator != null) {
            currentResult = this.generator.getIsomerInfoContainer();
            if (this.calculateDeviation) {
                currentResult.setMassDeviationInPPM(devCalculator.calculateDeviationInPPM(currentResult.getMass()));
            }
        }
        getNextGenerator();

        return toRet;

    }

    private void getNextGenerator() {
        /**
         * The current generator depends on the estimate used. Each estimate will generate several generators. Each
         * generator produces only one result. Hence each time this method is called, we should iterator inside the
         * current estimate (number of carbons and double bonds). If the current carbon/double bond config does not show
         * a result, then the following config needs to be tried, until there is a generator with a count of molecules
         * above zero.
         *
         * Once the maximum possible carbon/double bond scenario has been picked for the estimate, the next estimate
         * needs to be retrieved.
         *
         * This should probably be a different class.
         */
        while (true) {
            if (this.generatorIterator.hasNext()) {
                generator = this.generatorIterator.next();
                generator.setFirstResultOnly(true);
                generator.execute();
                SpeciesInfoContainer res = generator.getIsomerInfoContainer();
                if (res.getMass() != null) {
                    if (res.getMass() >= this.minMass && res.getMass() <= this.maxMass) {
                        this.currentResult = res;
                        if (this.calculateDeviation) {
                            currentResult.setMassDeviationInPPM(devCalculator.calculateDeviationInPPM(currentResult.getMass()));
                        }
                        break;
                    }
                    //else {
                    //  generator = null;
                    //}
                }
            } else {
                resetGeneratorIterator(allowedLinkers, cfGenerator);
                if (this.generator == null) {
                    this.currentResult = null;
                    break;
                }
            }
        }


    }

    /**
     * 
     * @return true if the getter has more results. 
     */
    public boolean hasNext() {
        return currentResult != null;
    }

    /**
     * Removes the next result to be retrieved.
     */
    public void remove() {
        next();
    }
}
