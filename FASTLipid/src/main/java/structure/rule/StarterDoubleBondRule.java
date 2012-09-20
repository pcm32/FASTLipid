/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package structure.rule;

/**
 *
 * @author pmoreno
 */
public class StarterDoubleBondRule extends BondRule {

    private int startPos;

    public StarterDoubleBondRule(int startPosition) {
        this.startPos = startPosition-1;
    }

    @Override
    public boolean isCompliantWithRule(char[] binaryRep) {
        for(int i=0;i<binaryRep.length;i++)
            if(binaryRep[i]=='1' && i < startPos)
                return false;
        return true;
    }

    @Override
    public boolean isCompliantWithRule(boolean[] counter) {
        for(int i=0;i<counter.length;i++)
            if(counter[i] && i < startPos)
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
        for (int i = initialSpace+1; i<counter.length; i++) {
            if(usedPositions<on) {
                counter[i]=true;
                usedPositions++;
            } else
                break;

        }
        return counter;
    }

    @Override
    public int firstUsedPosition() {
        return startPos;
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
        if(previous_one > -1 && previous_one < firstUsedPosition()) {
            return false;
        }
        return true;
    }

}
