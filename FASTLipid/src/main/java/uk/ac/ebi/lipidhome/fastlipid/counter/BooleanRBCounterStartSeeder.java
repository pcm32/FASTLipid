/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.counter;

import java.util.ArrayList;
import java.util.List;
import structure.rule.BondRule;

/**
 *
 * @author pmoreno
 */
public class BooleanRBCounterStartSeeder {

    private List<BondRule> rules = new ArrayList<BondRule>();
    private int maxShiftableDistance;
    
    public BooleanRBCounterStartSeeder(List<BondRule> rules) {
        this.rules = rules;
    }

    public int getNumberOfRules() {
        return rules.size();
    }

    public int getInitialPosition(int positions, int onPositions) {
        int maxInitialPos=0;
        for (BondRule bondRule : rules) {
            maxInitialPos = Math.max(maxInitialPos, bondRule.firstUsedPosition());
        }
        return maxInitialPos;
    }

    public void addRule(BondRule rule) {
        rules.add(rule);
        maxShiftableDistance = maxMovableDistance();
    }

    public boolean[] getStartingSeedFor(int positions, int onPositions) {
        int initialPos = getInitialPosition(positions, onPositions);
        for (BondRule rule : rules) {
            boolean[] cand = rule.leftMostValue(positions, onPositions, initialPos);
            if(numberOfOnes(cand)<onPositions)
                continue;
            boolean reject=false;
            for (BondRule anotherRule : rules) {
                if(rule.equals(anotherRule))
                    continue;
                if(!anotherRule.isCompliantWithRule(cand)) {
                    reject=true;
                    break;
                }

            }
            if(!reject)
                return cand;
        }
        return null;
    }

    private int numberOfOnes(boolean[] counter) {
        int count=0;
        for (int i = 0; i < counter.length; i++) {
            if(counter[i])
                count++;

        }
        return count;
    }

    public int nextStepTypeIGivenContext(int previous_one, int current_pos, int next_one, int totalSize) {
        int maxPosNextStepI=-1;
        for (int r1=0;r1<rules.size();r1++) {
            int posForNextStepI = rules.get(r1).nextStepTypeIGivenContext(previous_one, current_pos, next_one, totalSize);
            if(posForNextStepI<0)
                continue; // In this case is clear that we don't need to check for
            // the context adequacy. Just get the next candidate.
            boolean isContextPossible=true;
            for (int r2=0;r2<rules.size();r2++) {
                if(r1==r2)
                    continue;
                if(!rules.get(r2).acceptContext(previous_one,posForNextStepI,next_one)) {
                    isContextPossible=false;
                    break;
                }
            }
            if(isContextPossible)
                maxPosNextStepI = Math.max(posForNextStepI, maxPosNextStepI);
        }
        return maxPosNextStepI;

    }

    public boolean acceptContext(int previous_one, int current_pos, int next_one) {
        boolean isContextPossible=true;
        for (int r2=0;r2<rules.size();r2++) {
                if(!rules.get(r2).acceptContext(previous_one,current_pos,next_one)) {
                    isContextPossible=false;
                    break;
                }
            }
        return isContextPossible;
    }

    /*
     * This method delivers the maximal distance moved per iteration that is
     * suggested by the different Rules
     */
    public int maxMovableDistance() {
        int distance=-1;
        for (BondRule bondRule : rules) {
            distance=Math.max(distance, bondRule.getShiftingDistance());
        }
        return distance;
    }
}
