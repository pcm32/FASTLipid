/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.structure.rule;

/**
 *
 * @author pmoreno
 */
public class BondDistance3nPlus2Rule extends BondRule {

    public BondDistance3nPlus2Rule() {
        this.shiftingDistance=3;
    }

    @Override
    public boolean isCompliantWithRule(char[] binaryRep) {
        int previousDoubleBond=-1;
        for(int i=0;i<binaryRep.length;i++) {
            if(previousDoubleBond==-1 && binaryRep[i]=='1') {
                previousDoubleBond = i;
                continue;
            } else if(binaryRep[i]=='1') {
                int dist = (i - previousDoubleBond)-1;
                if((dist-2) % 3 != 0)
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCompliantWithRule(boolean[] counter) {
        int previousDoubleBond=-1;
        for(int i=0;i<counter.length;i++) {
            if(previousDoubleBond==-1 && counter[i]) {
                previousDoubleBond = i;
                continue;
            } else if(counter[i]) {
                int dist = (i - previousDoubleBond)-1;
                if((dist-2) % 3 != 0)
                    return false;
            }
        }
        return true;
    }

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
            if(dist==2 && usedPositions<on) {
                counter[i]=true;
                previousPos=i;
                usedPositions++;
                continue;
            }
            counter[i]=false;
        }
        return counter;
    }

    public int firstUsedPosition() {
        return 0;
    }

    @Override
    public int nextStepTypeIGivenContext(int previous_one, int current_pos, int next_one, int totalSize) {
        int maxBoundary=totalSize;
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
            if((distPrev-2) % 3 != 0)
                return false;
        }
        if(next_one >= current_pos){
            int distNext = (next_one - current_pos)-1;
            if((distNext-2) % 3 != 0)
                return false;
        }
        return true;
    }

}
