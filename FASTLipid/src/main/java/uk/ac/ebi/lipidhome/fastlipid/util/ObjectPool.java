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
    //unlocked = new HashSet<T>();
  }

  protected abstract T create();

  //public abstract boolean validate(T o);

  public abstract void expire(T o);
  

  public T checkOut() {
    T t;
    if (!unlocked.isEmpty()) {
      t = unlocked.pollFirst();
      locked.add(t);
      return t;
    }
    // no objects available, create a new one
    t = create();
    //locked.put(t, now);
    locked.add(t);
    return t;
  }

  public void checkIn(T t) {
    locked.remove(t);
    //unlocked.put(t, System.currentTimeMillis());
    unlocked.addLast(t);
  }
  
  public void clearPool() {
      this.locked.clear();
      this.unlocked.clear();
  }
}

//The three remaining methods are abstract
//and therefore must be implemented by the subclass
