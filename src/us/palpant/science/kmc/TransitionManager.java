package us.palpant.science.kmc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import us.palpant.science.kmc.geometry.Lattice;
import us.palpant.science.kmc.geometry.Lattice.Coordinate;

/**
 * Manage the Transitions database
 * Constructs a graph of Transitions based on their Conditions and Actions
 * so that the database can be updated efficiently
 * @author palpant
 *
 */
public class TransitionManager {
  
  private static final Logger log = Logger.getLogger(TransitionManager.class);

  private final Lattice lattice;
  private final Transition[] transitions;
  private final double[] accumulatedRates;
  private final Map<Coordinate,List<Transition>> dependencies;
  
  public TransitionManager(Lattice lattice, Transition[] transitions) {
    this.lattice = lattice;
    this.transitions = transitions;
    if (transitions.length == 0) {
      throw new IllegalArgumentException("KMC requires at least one Transition!");
    }
    log.debug("Initializing transition manager with "+transitions.length+" transitions");
    accumulatedRates = new double[transitions.length];
    
    // Initialize the dependency graph
    dependencies = new HashMap<Coordinate,List<Transition>>();
    for (Transition t : transitions) {
      for (Coordinate c : t.getUpstreamCoordinates()) {
        if (!dependencies.containsKey(c)) {
          dependencies.put(c, new ArrayList<Transition>());
        }
        dependencies.get(c).add(t);
      }
    }
    log.debug(dependencies.size()+" coordinates in dependency tree");
    
    updateAllTransitions();
    updateAccumulatedRates();
  }
  
  /**
   * Get the Transition corresponding to r \in [0,1]
   * @return the Transition selected by r
   */
  public Transition getTransition(double r) {
    r *= getKTotal();
    
    int selected = Arrays.binarySearch(accumulatedRates, r);
    if (selected < 0) {
      selected = -selected - 1;
    }
    
    // Don't select transitions with rate 0
    while (selected < accumulatedRates.length-1
        && transitions[selected].getRate() == 0) {
      selected++;
    }
    
    return transitions[selected];
  }
  
  /**
   * Perform a Transition
   * @param t the Transition to perform
   */
  public void perform(Transition t) {
    for (Action a : t.getActions()) {
      lattice.set(a.getCoord(), a.getState());
    }
    
    // Update downstream transitions
    for (Coordinate c : t.getDownstreamCoordinates()) {
      updateTransitions(c);
    }
    
    updateAccumulatedRates();
  }
  
  /**
   * Update whether a transition is valid or not
   * @param t the transition to update
   */
  private void updateTransition(Transition t) {
    t.setEnabled(true);
    for (Condition c : t.getConditions()) {
      if (!c.isSatified()) {
        t.setEnabled(false);
        break;
      }
    }
  }
  
  /**
   * Update transitions that have a condition on coord
   * @param coord the coordinate that is changing
   */
  private void updateTransitions(Coordinate coord) {
    for (Transition t : dependencies.get(coord)) {
      updateTransition(t);
    }
  }
  
  /**
   * Do a full sweep through all transitions and update
   */
  private void updateAllTransitions() {
    for (Transition t : transitions) {
      updateTransition(t);
    }
  }
  
  /**
   * Update all accumulated rates
   */
  public void updateAccumulatedRates() {
    accumulatedRates[0] = transitions[0].getRate();
    for (int i = 1; i < transitions.length; i++) {
      accumulatedRates[i] = accumulatedRates[i-1] + transitions[i].getRate();
    }
  }
  
  public double getKTotal() {
    return accumulatedRates[accumulatedRates.length-1];
  }
  
}
