/**
 * EstimateBasedGeneratorIterator.java
 *
 * 2012.09.07
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

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import uk.ac.ebi.lipidhome.fastlipid.generator.ChainFactoryGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.GeneralIsomersGenerator;
import uk.ac.ebi.lipidhome.fastlipid.generator.LinkersIterator;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.util.LipidChainConfigEstimate;

/**
 * @name EstimateBasedGeneratorIterator @date 2012.09.07
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) @brief Iterates on generators that are combinations of the given estimate of lipid
 * chains config estimate and the allowed linkers.
 *
 */
public class EstimateBasedGeneratorIterator implements Iterator<GeneralIsomersGenerator> {

    private static final Logger LOGGER = Logger.getLogger(EstimateBasedGeneratorIterator.class);
    private final Integer maxCarbons;
    private final Integer minCarbons;
    private final Integer maxDoubleBonds;
    private final Integer minDoubleBonds;
    private Integer currentCarbons;
    private Integer currentDoubleBonds;
    private LipidChainConfigEstimate estimate;
    private GeneralIsomersGenerator currentGenerator;
    private final LinkersIterator linkersIterator;
    private List<SingleLinkConfiguration> currentLinkersConfig;
    private ChainFactoryGenerator cfGen;
    private boolean followingConfigExists;
    private boolean exoticModeOn = false;
    private ChemInfoContainerGenerator chemInfoContainerGenerator;

    public EstimateBasedGeneratorIterator(LipidChainConfigEstimate currentEstimate,
            List<SingleLinkConfiguration> allowedLinkers,
            ChainFactoryGenerator cfGen,
            ChemInfoContainerGenerator chIContGen,
            boolean exoticMode) {
        this.estimate = currentEstimate;
        this.exoticModeOn = exoticMode;
        this.maxCarbons = processMaxCarbons(currentEstimate.getMaxCarbons());
        this.minCarbons = processMinCarbons(currentEstimate.getMinCarbons());
        this.currentCarbons = this.minCarbons;
        this.maxDoubleBonds = currentEstimate.getMaxDoubleBonds(currentEstimate.getMaxCarbons());
        this.minDoubleBonds = currentEstimate.getMinDoubleBonds(currentEstimate.getMinCarbons());
        this.currentDoubleBonds = this.minDoubleBonds;
        this.chemInfoContainerGenerator = chIContGen;
        this.cfGen = cfGen;


        this.linkersIterator = new LinkersIterator(allowedLinkers, currentEstimate.getHeadGroup());
        if (this.linkersIterator.hasNext()) {
            currentLinkersConfig = this.linkersIterator.next();
            followingConfigExists = true;
        } else {
            followingConfigExists = false;
        }

        setInitialGenerator();
    }

    public boolean hasNext() {
        setCurrent();
        return followingConfigExists;
    }

    public GeneralIsomersGenerator next() {
        // The problem here is for the first iteration, where the double bonds are increased in setCurrent() before 
        // toRet is returned. This didn't happen before because we were creating a new object each time
        // a new GeneralIsomersGenerator, however now we are returning a modifying the same object. We could either 
        // clone the object or modify it on each 
        return currentGenerator;
    }

    public void remove() {
        if (this.hasNext()) {
            this.next();
        }
    }

    private void setInitialGenerator() {
        if (followingConfigExists) {
            this.currentGenerator = new GeneralIsomersGenerator();// once
            this.currentGenerator.setHeadGroup(estimate.getHeadGroup());// once
            this.currentGenerator.setChainFactoryGenerator(cfGen);// once
            this.currentGenerator.setChemInfoContainerGenerator(this.chemInfoContainerGenerator);// once
            this.currentGenerator.setPrintOut(Boolean.FALSE); // once
            this.currentGenerator.setStepOfChange(2); // once
        }
    }

    private void setCurrent() {

        // We should only set the HeadGroup, ChainFactoryGenerator, ChemInfoContainerGenerator, 
        if (followingConfigExists) {

            this.currentGenerator.setLinkConfigs(currentLinkersConfig.toArray(new SingleLinkConfiguration[currentLinkersConfig.size()]));
            this.currentGenerator.setTotalCarbons(currentCarbons);
            this.currentGenerator.setTotalDoubleBonds(currentDoubleBonds);

            while(true) {
                currentDoubleBonds++;
                if (currentDoubleBonds > maxDoubleBonds) {
                    currentCarbons += 2;
                    currentDoubleBonds = minDoubleBonds;
                    if (currentCarbons > maxCarbons) {
                        currentCarbons = minCarbons;
                        
                        if (linkersIterator.hasNext()) {
                            currentLinkersConfig = linkersIterator.next();
                        } else {
                            this.followingConfigExists = false;
                        }
                    }
                }
                if(currentCarbons >= currentDoubleBonds)
                    break;
            }

        }

    }

    private Integer processMaxCarbons(Integer maxCarbons) {
        if (this.exoticModeOn || maxCarbons % 2 == 0) {
            return maxCarbons;
        } else {
            return maxCarbons - 1; // if the number is odd and exoticModeOn is false, we reduce by one.
        }
    }

    private Integer processMinCarbons(Integer minCarbons) {
        if (this.exoticModeOn || minCarbons % 2 == 0) {
            return minCarbons;
        } else {
            return minCarbons + 1; // if the number is odd and exoticModeOn is false, we increase by one.
        }
    }
}
