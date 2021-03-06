/**
 * ChainFactoryGenerator.java
 *
 * 2012.08.22
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
package uk.ac.ebi.lipidhome.fastlipid.generator;

import java.util.List;
import org.apache.log4j.Logger;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import uk.ac.ebi.lipidhome.fastlipid.structure.ChainFactory;
import uk.ac.ebi.lipidhome.fastlipid.structure.PooledChainFactory;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;

/**
 * @name ChainFactoryGenerator @date 2012.08.22
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) 
 * @brief A generator of ChainFactories which allows to unify the creation settings used. Currently it generates 
 * PooledChainFactories.
 *
 */
public class ChainFactoryGenerator {

    private static final Logger LOGGER = Logger.getLogger(ChainFactoryGenerator.class);
    private List<BondRule> rules;
    private final BooleanRBCounterStartSeeder seeder;
    private boolean useRuleBasedBooleanCounter;

    /**
     * Constructor which takes the list of BondRules, the Seeder and whether to use a rule based boolean counter or not.
     * This last boolean is probably redundant, as the boolean counter is always used rule based. TODO remove this setting
     * option from this class and underlying classes.
     * 
     * @param rules
     * @param seeder
     * @param useRuleBasedBooleanCounter 
     */
    public ChainFactoryGenerator(List<BondRule> rules,
                                 BooleanRBCounterStartSeeder seeder,
                                 boolean useRuleBasedBooleanCounter) {
        this.rules = rules;
        this.useRuleBasedBooleanCounter = useRuleBasedBooleanCounter;
        this.seeder = seeder;
    }

    /**
     * Produce a ChainFactory (pooled) with the settings specified in the constructor.]
     * 
     * @return a ChainFactory 
     */
    public ChainFactory makeChainFactory() {
        ChainFactory factory = new PooledChainFactory();
        for (BondRule bondRule : rules) {
            factory.addAlwaysRule(bondRule);
        }
        factory.setSeeder(seeder);
        factory.setUseRuleBasedBooleanCounter(useRuleBasedBooleanCounter);
        return factory;
    }
}
