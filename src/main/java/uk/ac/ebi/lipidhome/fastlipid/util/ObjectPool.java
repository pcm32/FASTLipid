/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.lipidhome.fastlipid.util;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * A generic object pool implementation. Since we are interested in objects that do not expire, 
 * we avoid the validate method. It is implemented with a Set for locked elements (so that removals are cheap) and a deque
 * for unlocked elements, so that checkouts are cheap as well (no iterator needed).
 *
 * @author pmoreno
 */
public abstract class ObjectPool<T> { 
    

  /**
   * A good compromise is to leave locked as HashSet, so that removals are cheap and unlocked as a deque, so that
   * checkouts are cheap as well (no iterator needed).
   */
  private Set<T> locked;//, unlocked;
  private Deque<T> unlocked;

  public ObjectPool() {
    locked = new HashSet<T>();
    unlocked = new LinkedList<T>();
  }

  protected abstract T create();

  public abstract void expire(T o);
  

  /**
   * Gets an object from the pool. Creates a new one if there is none in the deque.
   * 
   * @return an object T from the pool. 
   */
  public T checkOut() {
    T t;
    if (!unlocked.isEmpty()) {
      t = unlocked.pollFirst();
      locked.add(t);
      return t;
    }
    // no objects available, create a new one
    t = create();
    locked.add(t);
    return t;
  }

  /**
   * Gives t back to the pool.
   * 
   * @param t the object to be inserted again in the pool.
   */
  public void checkIn(T t) {
    locked.remove(t);
    unlocked.addLast(t);
  }
  
  /**
   * Clears the pool.
   */
  public void clearPool() {
      this.locked.clear();
      this.unlocked.clear();
  }
}
