/**
 * StatsForMassRangeIsomerGetter.java
 *
 * 2012.09.12
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

package uk.ac.ebi.lipidhome.fastlipid.stats;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import uk.ac.ebi.lipidhome.fastlipid.exec.MassRangeIsomersGetter;
import uk.ac.ebi.lipidhome.fastlipid.generator.ChainFactoryGenerator;
import uk.ac.ebi.lipidhome.fastlipid.mass.MassRange;
import uk.ac.ebi.lipidhome.fastlipid.mass.PPMBasedMassRange;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChemInfoContainerGenerator;
import uk.ac.ebi.lipidhome.fastlipid.structure.HeadGroup;
import uk.ac.ebi.lipidhome.fastlipid.structure.SpeciesInfoContainer;
import uk.ac.ebi.lipidhome.fastlipid.structure.SingleLinkConfiguration;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondDistance3nPlus2Rule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.NoDoubleBondsTogetherRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.StarterDoubleBondRule;

/**
 * @name    StatsForMassRangeIsomerGetter
 * @date    2012.09.12
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   This is an executable to get execution times statistics for the MassRangeIsomerGetter
 *
 */
public class StatsForMassRangeIsomerGetter {

    private static final Logger LOGGER = Logger.getLogger( StatsForMassRangeIsomerGetter.class );
    
    
    public static void main(String[] args) throws CDKException, IOException {
        Long startSetup = System.currentTimeMillis();
        
        List<HeadGroup> allowed4HeadGroups = Arrays.asList(HeadGroup.DG, HeadGroup.PE, HeadGroup.PI);
        List<SingleLinkConfiguration> allowedLinkers = Arrays.asList(SingleLinkConfiguration.Acyl, SingleLinkConfiguration.Alkyl);

        List<BondRule> rules = Arrays.asList(new BondDistance3nPlus2Rule(), new NoDoubleBondsTogetherRule(), new StarterDoubleBondRule(2));
        ChainFactoryGenerator cfGen = new ChainFactoryGenerator(rules, new BooleanRBCounterStartSeeder(rules), true);

        ChemInfoContainerGenerator cicg = new ChemInfoContainerGenerator();
        cicg.setGenerateInChi(false);
        cicg.setGenerateInChiKey(false);
        cicg.setGenerateInChIAux(false);
        cicg.setGenerateMolFormula(true);
        cicg.setGenerateMass(true);
        cicg.setGenerateSmiles(false);
        cicg.setUseCachedObjects(true);
        
        Long elapsedSetup = System.currentTimeMillis() - startSetup;
        System.out.println("Setup time : "+elapsedSetup);

        System.out.println("Min\tMax\tNumOfMols\tElapsed");
        
        int steps = 5;
        int initialMass = 400;
        int increment = 10;
        
        Set<Double> seenMass = new HashSet<Double>();
        for (int i = 0; i < steps; i++) {
            double minMass = initialMass;
            double maxMass = initialMass + increment - 1;
            
            initialMass += increment;

            Long startTime = System.currentTimeMillis();
            MassRangeIsomersGetter instance = new MassRangeIsomersGetter(allowed4HeadGroups, allowedLinkers, cfGen, minMass, maxMass, Boolean.FALSE, cicg);
            int numOfMols = 0;
            while (instance.hasNext()) {
                SpeciesInfoContainer res = instance.next();
                numOfMols+=res.getNumOfMolsGenerated();
                if(res.getNumOfMolsGenerated()>0) {
                    seenMass.add(res.getMass());
                }
            }
            Long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println(minMass+"\t"+maxMass+"\t"+numOfMols+"\t"+elapsedTime);
        }
        
        System.out.println("\n\n\n");
        
        System.out.println("Min\tMax\tNumOfMols\tElapsed");
        Float ppm = 5f;
        for (Double mass : seenMass) {
            Long startTime = System.currentTimeMillis();
            MassRange range = new PPMBasedMassRange(mass, ppm);
            MassRangeIsomersGetter isomersGetter = new MassRangeIsomersGetter(allowed4HeadGroups,allowedLinkers,cfGen,range,false,cicg);
            int numOfMols = 0;
            while (isomersGetter.hasNext()) {                
                SpeciesInfoContainer res = isomersGetter.next();
                numOfMols += res.getNumOfMolsGenerated();
            }
            Long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println(range.getMinMass()+"\t"+range.getMaxMass()+"\t"+numOfMols+"\t"+elapsedTime);
        }
    }

}
