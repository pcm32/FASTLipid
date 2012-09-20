/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.lnetmoleculegenerator;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import structure.ChemInfoContainer;

/**
 *
 * @author pmoreno
 */
public interface IterableGenerator {

    BlockingQueue<ChemInfoContainer> queue = new ArrayBlockingQueue<ChemInfoContainer>(1000);

    void setIterableMode(boolean isIterable);
    boolean isInIterableMode();
    ChemInfoContainer getNext() throws LNetMoleculeGeneratorException;
    void executeInSeparateThread();

}
