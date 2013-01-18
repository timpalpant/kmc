package us.palpant.science;

import java.util.Collection;

import org.apache.log4j.Logger;

import us.palpant.science.transitions.Transition;

/**
 * Transition managers return a list of Transitions for a given lattice
 * Different TransitionManagers can implement different logic
 * 
 * @author timpalpant
 * 
 */
public abstract class TransitionManager {
  private static final Logger log = Logger.getLogger(TransitionManager.class);

  /**
   * The Lattice that this TransitionManager is managing
   */
  protected final Lattice lattice;
  protected final Parameters params;

  protected TransitionManager(Lattice lattice, Parameters params) {
    this.lattice = lattice;
    this.params = params;
  }

  /**
   * Return a list of all possible transitions for the current lattice state
   * 
   * @return a List of all possible transitions for the current lattice state
   */
  public abstract Collection<Transition> getAllTransitions();

  /**
   * Get a transition based on the random variable u \in [0,1]
   * 
   * @param transitions a Collection of Transitions with rates
   * @param u a random variable, uniformly distributed \in [0,1]
   * @return the randomly selected Transition corresponding to u
   */
  public static Transition getTransition(Collection<? extends Transition> transitions, double rateTotal, double u) {
    if (u < 0 || u > 1) {
      throw new IllegalArgumentException("u must be in [0,1]! (u = " + u + ")");
    }
    if (transitions.size() == 0) {
      throw new IllegalArgumentException("No transitions available!");
    }

    double selected = u * rateTotal;
    double cumulative = 0;
    for (Transition t : transitions) {
      cumulative += t.getRate();
      if (cumulative >= selected) {
        if (log.isDebugEnabled()) {
          log.debug("Selected random transition: " + t);
        }
        return t;
      }
    }

    throw new RuntimeException("Error selecting transition!");
  }
  
  public static Transition getTransition(Collection<? extends Transition> transitions, double u) {
    return getTransition(transitions, getRateTotal(transitions), u);
  }

  /**
   * Get the sum of the rates for all transitions
   * 
   * @return the sum of the rates of all possible transitions
   */
  public static double getRateTotal(Collection<? extends Transition> transitions) {
    double total = 0;
    for (Transition t : transitions) {
      total += t.getRate();
    }
    if (log.isDebugEnabled()) {
      log.debug(transitions.size() + " transitions with total rate = " + total);
    }
    return total;
  }
}
