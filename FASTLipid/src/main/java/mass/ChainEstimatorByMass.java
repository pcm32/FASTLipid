/**
 * ChainEstimatorByMass.java
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
package mass;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import structure.HeadGroup;
import structure.SingleLinkConfiguration;
import util.GenericAtomDetector;
import util.LipidChainConfigEstimate;

/**
 * @name ChainEstimatorByMass @date 2012.09.03
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) @brief Given a head group, possible linkers and a mass range, this class estimates
 * the minimal and maximal number of carbons (total chain length) and double bonds (total) that fit within those mass
 * boundaries. Maximal and minimal double bonds are calculated for each carbon length.
 *
 * Given a total number of carbons, each double bond reduces the possible mass by 2 Hydrogens, hence the maximal mass is
 * with zero double bonds and the maximal mass is a totally saturated state (which is not really feasible). The 3n+2
 * rule requires that at the most 1 third of possible bonds are double bonds (that is, a double bond every 3 bonds).
 * Maybe each rule should be able to compute a maximum and minimum double/single bonds ratio.
 *
 * In common nomenclature, the linker carbon is counted as part of the fatty acid chain length
 *
 */
public class ChainEstimatorByMass implements Callable<LipidChainConfigEstimate> {

    private static final Logger LOGGER = Logger.getLogger(ChainEstimatorByMass.class);
    private IAtomContainer headMol;
    private HeadGroup hg;
    private Double minMass;
    private Double maxMass;
    private List<SingleLinkConfiguration> allowedLinkers;
    private ChainPartMassProvider massProvider;

    public ChainEstimatorByMass(Double minMass, Double maxMass, HeadGroup hg, List<SingleLinkConfiguration> allowedLinkers) throws CDKException, IOException {
        this.hg = hg;
        this.headMol = hg.getHeadMolecule(SilentChemObjectBuilder.getInstance());
        this.minMass = minMass;
        this.maxMass = maxMass;
        this.allowedLinkers = allowedLinkers;
        massProvider = ChainPartMassProvider.getInstance();
    }

    public LipidChainConfigEstimate call() throws Exception {
        Double headMass = MolMassCachedCalculator.calcExactMassGenericMol(this.headMol);
        Double minLinkersMass = null;
        Double maxLinkersMass = null;
        for (SingleLinkConfiguration singleLinkConfiguration : allowedLinkers) {
            Double mass = singleLinkConfiguration.getMass();
            if (minLinkersMass == null || minLinkersMass > mass) {
                minLinkersMass = mass;
            }
            if (maxLinkersMass == null || maxLinkersMass < mass) {
                maxLinkersMass = mass;
            }
        }

        LipidChainConfigEstimate estimate = new LipidChainConfigEstimate(this.hg);
        Integer numberOfChains = this.hg.getNumOfSlots();

        // The maximum weight of a chain is achieved in its fully saturated state, no double bonds, (n-2)CH2+CH3
        //double fullySatAtomicUnitWeight = MolecularFormulaManipulator.getTotalExactMass(MolecularFormulaManipulator.getMolecularFormula("CH2", SilentChemObjectBuilder.getInstance()));
        double fullySatAtomicUnitWeight = massProvider.getFullySatAtomicUnitWeight();
        //double endPartFullySatWeight = MolecularFormulaManipulator.getTotalExactMass(MolecularFormulaManipulator.getMolecularFormula("CH3", SilentChemObjectBuilder.getInstance()));
        double endPartFullySatWeight = massProvider.getEndPartFullySatWeight();
        // The minimum weight of a chain is achieved in its fully unsaturated state, all double bonds, (n-2)C+CH2
        //double fullyUnSatAtomicUnitWeight = MolecularFormulaManipulator.getTotalExactMass(MolecularFormulaManipulator.getMolecularFormula("C", SilentChemObjectBuilder.getInstance()));
        double fullyUnSatAtomicUnitWeight = massProvider.getFullyUnSatAtomicUnitWeight();
        double endPartFullyUnSatWeight = fullySatAtomicUnitWeight;

        // Consider the mass of the head and of the heaviest and lightest linkers


        // The highest amount of carbons can be achieved in the conditions with the lightest linkers and end part:
        double remainingMaxMass = this.maxMass              // maximum masss
                - headMass                                  // mass of the head
                - minLinkersMass * numberOfChains           // lighter linkers
                - endPartFullyUnSatWeight * numberOfChains; // lighter end parts.  so only the mass of the central chain is left, without linker and end.
        if (remainingMaxMass > 0) {
            // There is space for carbons in the chain 
            Double maxNumCarbonsMinus1 = remainingMaxMass > 0 ? remainingMaxMass / fullyUnSatAtomicUnitWeight : 0d;
            // We add two : one for the linker and one of the end part.
            Integer maxNumCarbs = Math.max(maxNumCarbonsMinus1.intValue() + 2 * numberOfChains, 0);
            estimate.setMaxCarbons(maxNumCarbs, 0, Math.max(maxNumCarbs - 1, 0)); // to avoid negative double bonds
            estimate.setMaxMass(headMass 
                    + (maxLinkersMass * numberOfChains)
                    + ((maxNumCarbs - numberOfChains * 2) * fullyUnSatAtomicUnitWeight) // 1 for each linker and one for each border
                    + (numberOfChains * endPartFullyUnSatWeight));
        } else {
            estimate.setMaxCarbons(0, 0, 0);
            estimate.setMaxMass(0d);
        }

        // The lowest amount of carbons is achieved in the conditions where everything else is the heaviest and we are at the
        // minimum mass boundary. In this case, if the remainingMinMass goes < 0 we can iterate, decreasing the weight of linkers
        // and end parts. 
        int balanceLinkers = 0;
        int balanceEndPart = 0;
        double remainingMinMass;
        while (true) {
            remainingMinMass = this.minMass
                    - headMass
                    - (maxLinkersMass * (numberOfChains - balanceLinkers) + minLinkersMass * balanceLinkers)
                    - (endPartFullySatWeight * (numberOfChains - balanceEndPart) + endPartFullyUnSatWeight * balanceEndPart);

            if (remainingMinMass > 0) {
                break;
            }

            if (maxLinkersMass - minLinkersMass < endPartFullySatWeight - endPartFullyUnSatWeight) {
                if(balanceLinkers < numberOfChains) {
                    balanceLinkers++;
                } else if(balanceEndPart < numberOfChains) {
                    balanceEndPart++;
                } else {
                    break;
                }
            } else {
                if(balanceEndPart < numberOfChains) {
                    balanceEndPart++;
                } else if(balanceLinkers < numberOfChains) {
                    balanceLinkers++;
                } else {
                    break;
                }
            }
        }

        Integer minNumCarbs;
        Double unitWeightForMin;
        Double endUnitWeightForMin;
        if (remainingMinMass < 0) {
            // if there is mass remaining, there is no minimum, as we have already
            // fulfilled the minimum mass. The minimum is just the linker + end.
            //Double minNumCarbonsMinus1 = remainingMinMass / fullySatAtomicUnitWeight;
            minNumCarbs = 2 * numberOfChains; // since this is the minimum.
            unitWeightForMin = fullyUnSatAtomicUnitWeight;
            endUnitWeightForMin = endPartFullyUnSatWeight;
        } else {
            // if we are still lacking mass to reach the minimum, we need to add at 
            // least a number of carbons in the heaviest state.
            unitWeightForMin = fullySatAtomicUnitWeight;
            endUnitWeightForMin = endPartFullySatWeight;
            Double minNumCarbonsMinus1 = remainingMinMass / fullySatAtomicUnitWeight;
            minNumCarbs = Math.max(minNumCarbonsMinus1.intValue() + 2 * numberOfChains, 0); //+1
        }
        estimate.setMinCarbons(minNumCarbs, 0, Math.max(minNumCarbs - 1, 0)); // to avoid negative double bonds.

        Double headPlusLinkers = headMass + minLinkersMass*numberOfChains;
        Double centralChain = (minNumCarbs - numberOfChains * 2)*unitWeightForMin;
        Double chainEndings = (endUnitWeightForMin*numberOfChains);
        
        
        estimate.setMinMass( headPlusLinkers
                + centralChain
                + chainEndings);

        return estimate;

    }
}
