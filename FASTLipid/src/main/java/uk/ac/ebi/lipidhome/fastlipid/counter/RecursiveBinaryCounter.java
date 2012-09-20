/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.counter;

import java.math.BigInteger;

/**
 *
 * @author pmoreno
 */
public class RecursiveBinaryCounter {
   private BigInteger counter;
   private Integer binaryPositions;
   private Integer bitCount;
   private boolean next;

    public RecursiveBinaryCounter(Integer binaryPositions, Integer bitCount) {
        this.binaryPositions = binaryPositions;
        this.bitCount = bitCount;
        this.counter = BigInteger.ZERO;
        if(binaryPositions>bitCount) {
            this.next=true;
            this.initialStep();
        }
        else
            this.next=false;
    }

    private char[] getBinaryRep() {
        return this.appendZeros((new StringBuffer(counter.toString(2))).reverse().toString()).toCharArray();
    }

    private void initialStep() {
        // All to the right
        for(int i=0;i<bitCount;i++) {
            this.counter = this.counter.flipBit(i);
        }
    }

    // Try to move bit in pos_i to pos_i+1 (to the left one)
    private boolean stepType1(int pos_i) {
        //char[] binary = this.getBinaryRep();
        if(pos_i+1<this.binaryPositions && !counter.testBit(pos_i+1) ) {
            counter = counter.setBit(pos_i+1);
            counter = counter.flipBit(pos_i);
            return true;
        }
        return false;
    }

    // Move position pos_i to righmost possible (boundary and no bit set)
    private boolean stepTyp2(int pos_i) {
        boolean moved=false;
        while(pos_i-1>=0 && !counter.testBit(pos_i-1)) {
            counter = counter.setBit(pos_i-1);
            counter = counter.flipBit(pos_i);
            pos_i--;
            moved=true;
        }
        return moved;
    }

    public Long nextBinaryAsLong() {
        Long res = counter.longValue();
        char[] current = this.getBinaryRep();
        // Find leftmost 1
        int leftMost_pos=-1;
        for(int i=current.length-1;i>=0;i--)
            if(current[i]=='1') {
                leftMost_pos=i;
                break;
            }
        if(leftMost_pos==-1)
            return null;
        this.next = this.callMovementRecursion(leftMost_pos);
        return res;
    }

    public char[] nextBinary() {
        char[] current = this.getBinaryRep();
        // Find leftmost 1
        int leftMost_pos=-1;
        for(int i=current.length-1;i>=0;i--)
            if(current[i]=='1') {
                leftMost_pos=i;
                break;
            }
        if(leftMost_pos==-1)
            return null;
        this.next = this.callMovementRecursion(leftMost_pos);
        return current;
    }

    private boolean callMovementRecursion(int pos_i) {
        if(this.stepType1(pos_i))
            return true;
        else
        {
            int nextBitSetRight = this.nextBitSetToTheRight(pos_i);
            if(nextBitSetRight<0) {
                // We have nothing more to move
                return false;
            }
            else {
                if(!this.callMovementRecursion(nextBitSetRight))
                    return false;
                this.stepTyp2(pos_i);
                return true;
            }
        }
    }

    private int nextBitSetToTheRight(int pos_i) {
        for(int i=pos_i-1;i>-1;i--)
            if(counter.testBit(i))
                return i;
        return -1;
    }

    public boolean hasNext() {
        return this.next;
    }

    private String appendZeros(String binaryString) {
        while(binaryString.length()<this.binaryPositions)
            binaryString = binaryString + "0";
        return binaryString;
    }

}
