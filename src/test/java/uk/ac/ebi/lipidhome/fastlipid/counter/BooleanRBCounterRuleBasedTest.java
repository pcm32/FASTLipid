/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.counter;

import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterRuleBased;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import structure.rule.BondDistance3nPlus2Rule;
import structure.rule.BondRule;
import structure.rule.NoDoubleBondsTogetherRule;
import structure.rule.StarterDoubleBondRule;

/**
 *
 * @author pmoreno
 */
public class BooleanRBCounterRuleBasedTest extends TestCase {

    public BooleanRBCounterRuleBasedTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of nextBinaryAsLong method, of class BooleanRBCounterRuleBased.
     */
    public void testNextBinaryAsLong() {
        System.out.println("nextBinaryAsLong");
        long start = System.currentTimeMillis();
        long current;
        int total = 23;
        int on = 7;

        List<BondRule> rules = Arrays.asList(new BondDistance3nPlus2Rule(),
                new NoDoubleBondsTogetherRule(),
                new StarterDoubleBondRule(2));

        BooleanRBCounterStartSeeder seeder = new BooleanRBCounterStartSeeder(rules);
        BooleanRBCounterRuleBased binCounter = new BooleanRBCounterRuleBased(total, on, seeder);


        boolean[] counter = seeder.getStartingSeedFor(total, on);
        if (counter == null) {
            System.out.println("No capacity for the given rules, exiting.");
            System.exit(1);
        }

        binCounter.setCounter(counter);



        int generatedBinaries = 0;
        long max = 0;
        while (binCounter.hasNext()) {
            String rep = binCounter.binaryAsString();
            assertNotNull(rep);
            Long binary = binCounter.nextBinaryAsLong();
            assertNotNull(binary);
            generatedBinaries++;
        }
        System.out.println("Generated structs:" + generatedBinaries);
        System.out.println("Max binary:" + max);
        current = System.currentTimeMillis() - start;
        System.out.println("Time:" + current);
    }
}
