/**
 * AbstractIsomersGenerator.java
 *
 * 2012.10.04
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.lipidhome.fastlipid.generator;


import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IPseudoAtom;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChainFactory;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;

/**
 * @name    AbstractIsomersGenerator
 * @date    2012.10.04
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public abstract class AbstractIsomersGenerator {

    List<ChainFactory> chainFactories;

    public AbstractIsomersGenerator() {
    }
    
    ChainFactoryGenerator chainFactoryGenerator;
    ChemInfoContainerGenerator chemInfoContainerGenerator;
    boolean exoticModeOn = false;
    boolean firstResultOnly = false;
    HeadGroup headGroup;
    List<SingleLinkConfiguration> linkConfigs;
    Integer maxCarbonsPerSingleChain = 0;
    Integer stepOfChange;

    boolean allRAtomAreNotNull(List<IPseudoAtom> rAtoms) {
        for (IPseudoAtom atom : rAtoms) {
            if (atom == null) {
                return false;
            }
        }
        return true;
    }

    public abstract void execute();

    /**
     * Get the value of headGroup
     *
     * @return the value of headGroup
     */
    public HeadGroup getHeadGroup() {
        return headGroup;
    }

    boolean incompatibleDoubleBondsWithCarbons(List<Integer> doubleBondsDisp, List<Integer> carbonDisp) {
        for (int i = 0; i < doubleBondsDisp.size(); i++) {
            if (doubleBondsDisp.get(i) >= carbonDisp.get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the weirdModeOn
     */
    public boolean isExoticModeOn() {
        return exoticModeOn;
    }

    String makeChainConfigStr(List<Integer> carbonDisp, List<Integer> dbDisp) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < carbonDisp.size(); i++) {
            builder.append(carbonDisp.get(i)).append(":").append(dbDisp.get(i));
            if (i < carbonDisp.size() - 1) {
                builder.append("_");
            }
        }
        return builder.toString();
    }

    /**
     * @param chainFactoryGenerator the chainFactoryGenerator to set
     */
    public void setChainFactoryGenerator(ChainFactoryGenerator chainFactoryGenerator) {
        this.chainFactoryGenerator = chainFactoryGenerator;
    }

    public void setChemInfoContainerGenerator(ChemInfoContainerGenerator generator) {
        this.chemInfoContainerGenerator = generator;
    }

    /**
     * @param exoticModeOn the weirdModeOn to set
     */
    public void setExoticModeOn(boolean exoticModeOn) {
        this.exoticModeOn = exoticModeOn;
    }

    /**
     * @param firstResultOnly the firstResultOnly to set
     */
    public void setFirstResultOnly(boolean firstResultOnly) {
        this.firstResultOnly = firstResultOnly;
    }

    /**
     * Set the value of headGroup
     *
     * @param headGroup new value of headGroup
     */
    public void setHeadGroup(HeadGroup headGroup) {
        this.headGroup = headGroup;
    }

    /**
     * This method sets the linkage to be used in each of the positions.
     *
     * @param configs
     */
    public void setLinkConfigs(SingleLinkConfiguration... configs) {
        this.linkConfigs = Arrays.asList(configs);
    }

    /**
     * @param maxCarbonsPerSingleChain the maxCarbonsPerSingleChain to set
     */
    public void setMaxCarbonsPerSingleChain(Integer maxCarbonsPerSingleChain) {
        this.maxCarbonsPerSingleChain = maxCarbonsPerSingleChain;
    }

    /**
     * @param stepOfChange the stepOfChange to set
     */
    public void setStepOfChange(Integer stepOfChange) throws LNetMoleculeGeneratorException {
        if (!exoticModeOn && stepOfChange % 2 != 0) {
            throw new LNetMoleculeGeneratorException("Steps of change can only be even if the exotic mode is not on");
        }
        this.stepOfChange = stepOfChange;
    }


}
