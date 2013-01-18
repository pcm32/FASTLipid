/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.structure.rule;

/**
 * This rule enforces that no two double bonds can be next to each other. This rule is redundant when using the 
 * 3n + 2 rule. TODO check whether is relevant to have this rule.
 *
 * @author pmoreno
 */
public class NoDoubleBondsTogetherRule extends BondRule{

    @Override
    public boolean isCompliantWithRule(char[] binaryRep) {
        for(int i=0;i<binaryRep.length-1;i++)
            if(binaryRep[i]=='1' && binaryRep[i+1]=='1')
                return false;
        return true;
    }

    @Override
    public boolean isCompliantWithRule(boolean[] counter) {
        for(int i=0;i<counter.length-1;i++)
            if(counter[i] && counter[i+1])
                return false;
        return true;
    }

    @Override
    public boolean[] leftMostValue(int positions, int on, int initialSpace) {
        boolean[] counter = new boolean[positions];
        int usedPositions=0;
        int previousPos=-1;
        for (int i = 0; i <= Math.min(counter.length-1, initialSpace); i++) {
            counter[i]=false;
        }
        for(int i=initialSpace+1;i<counter.length;i++) {
            if(previousPos<0 && usedPositions<on) {
                counter[i]=true;
                previousPos=i;
                usedPositions++;
                continue;
            }
            int dist = (i - previousPos)-1;
            if(dist>0 && usedPositions<on) {
                counter[i]=true;
                previousPos=i;
                usedPositions++;
                continue;
            }
            counter[i]=false;
        }
        return counter;
    }

    @Override
    public int firstUsedPosition() {
        return 0;
    }

    @Override
    public int nextStepTypeIGivenContext(int previous_one, int current_pos, int next_one, int total_size) {
        int maxBoundary=total_size;
        if(next_one > current_pos)
            maxBoundary=next_one;
        for(int nextPos=current_pos+this.shiftingDistance;nextPos<maxBoundary;nextPos+=this.shiftingDistance) {
            if(this.acceptContext(previous_one, nextPos, next_one))
                return nextPos;
        }
        return -1;
    }

    @Override
    public boolean acceptContext(int previous_one, int current_pos, int next_one) {
        if(current_pos < firstUsedPosition())
            return false;
        if(current_pos > previous_one && previous_one >= firstUsedPosition()) {
            int distPrev = (current_pos - previous_one)-1;
            if(distPrev == 0)
                return false;
        }
        if(next_one >= current_pos){
            int distNext = (next_one - current_pos)-1;
            if(distNext == 0)
                return false;
        }
        return true;
    }

}
