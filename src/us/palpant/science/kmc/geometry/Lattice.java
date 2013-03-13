package us.palpant.science.kmc.geometry;

import java.util.Arrays;
import java.util.Iterator;

import us.palpant.science.kmc.Condition;
import us.palpant.science.kmc.State;

/**
 * An array of States (possibly with periodic boundaries)
 * @author palpant
 *
 */
public class Lattice implements Iterable<State> {
  
  private final State[] states;
  private final BoundaryCondition bc;
  
  public Lattice(int size, BoundaryCondition bc) {
    this.bc = bc;
    states = new State[size];
  }

  @Override
  public Iterator<State> iterator() {
    return Arrays.asList(states).iterator();
  }
  
  public State get(int i) {
    if (bc == BoundaryCondition.PERIODIC) {
      i = getPeriodicWrap(i);
    }
    
    return states[i];
  }
  
  public void set(int i, State s) {
    if (bc == BoundaryCondition.PERIODIC) {
      i = getPeriodicWrap(i);
    }
    
    states[i] = s;
  }
  
  public boolean isSatisfied(Condition c) {
    return get(c.getCoord()) == c.getState();
  }
  
  /**
   * Set all coordinates in the Lattice to a specific State
   * @param s the state to set the lattice to
   */
  public void fill(State s) {
    Arrays.fill(states, s);
  }
  
  public final int size() {
    return states.length;
  }
  
  public final int count(State state) {
    int count = 0;
    for (State s : states) {
      if (s == state) {
        count++;
      }
    }
    return count;
  }
  
  private int getPeriodicWrap(int i) {
    if (i < 0) {
      return getPeriodicWrap(size()+i);
    }
    
    return i % size();
  }
  
  /**
   * @return the bc
   */
  public BoundaryCondition getBoundaryCondition() {
    return bc;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (State s : states) {
      sb.append(s.getId());
    }
    return sb.toString();
  }
  
  public enum BoundaryCondition {
    PERIODIC("periodic"), 
    FIXED("fixed");
    
    private final String name;
    
    private BoundaryCondition(String name) {
      this.name = name;
    }
    
    public static BoundaryCondition forName(String name) {
      for (BoundaryCondition bc : BoundaryCondition.values()) {
        if (bc.name.equals(name)) {
          return bc;
        }
      }
      
      return null;
    }
  }
  
}
