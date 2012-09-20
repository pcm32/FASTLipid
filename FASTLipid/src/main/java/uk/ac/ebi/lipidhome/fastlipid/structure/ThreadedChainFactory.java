/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.lipidhome.fastlipid.structure;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.openscience.cdk.interfaces.IMolecule;

/**
 *
 * @author pmoreno
 */
public class ThreadedChainFactory extends ChainFactory implements Runnable {

    private BlockingQueue<IMolecule> dataQueue;
    private boolean initialized;
    private boolean stopThread;
    private IMolecule currentMol;

    public ThreadedChainFactory() {
        super();
        dataQueue = new LinkedBlockingQueue<IMolecule>(20);
        initialized = false;
        stopThread = false;
    }

    @Override
    public void setChainIterator(int minCarbonAtoms, int maxCarbonAtoms, int minUnsatBonds, int maxUnsatBonds) {
        super.setChainIterator(minCarbonAtoms, maxCarbonAtoms, minUnsatBonds, maxUnsatBonds);
        initialized = true;
    }

    @Override
    public IMolecule nextChain() {
        try {
            if (dataQueue.size() == 0 && !super.couldNextExist()) {
                return null;
            }
            IMolecule mol = this.dataQueue.take();
            this.currentMol = mol;
            return mol;
        } catch (InterruptedException ex) {
            System.out.print("Threading error" + ex.getMessage());
            return null;
        }
    }

    @Override
    public IMolecule getCurrentChain() {
        return this.currentMol;
    }

    @Override
    public boolean couldNextExist() {
        if (!initialized) {
            return true;
        } else {
            return this.dataQueue.size() > 0;
        }
    }

    public void stopThread() {
        this.dataQueue.clear();
        this.stopThread = true;
    }

    public void run() {
        while (!stopThread) {
            try {
                if (super.couldNextExist()) {
                    IMolecule mol = super.nextChain();
                    if(mol!=null)
                        this.dataQueue.put(mol);
                }
            } catch (InterruptedException ex) {
                System.out.println("Threading error:" + ex.getMessage());
            }
        }
    }
}
