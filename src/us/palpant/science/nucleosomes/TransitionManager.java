package us.palpant.science.nucleosomes;

import java.util.Collection;

/**
 * Transition managers return a list of Transitions for a given lattice
 * Different TransitionManagers can implement different logic
 * @author timpalpant
 *
 */
public abstract class TransitionManager {
	/**
	 * The Lattice that this TransitionManager is managing
	 */
	protected final Lattice lattice;
	
	protected TransitionManager(Lattice lattice) {
		this.lattice = lattice;
	}
	
	/**
	 * Return a list of all possible transitions for the current lattice state
	 * @return a List of all possible transitions for the current lattice state
	 */
	public abstract Collection<Transition> getAllTransitions();
	
	/**
	 * Get a transition based on the random variable u \in [0,1]
	 * @param u a random variable, uniformly distributed \in [0,1]
	 * @return the randomly selected Transition corresponding to u
	 */
	public Transition getTransition(double u) {
		if (u < 0 || u > 1) {
			throw new IllegalArgumentException("u must be in [0,1]! (u = "+u+")");
		}
		
		double selected = u * getRateTotal();
		double cumulative = 0;
		for (Transition t : getAllTransitions()) {
			cumulative += t.getRate();
			if (cumulative >= selected) {
				return t;
			}
		}
		
		throw new RuntimeException("Error selecting transition!");
	}
	
	/**
	 * Get the sum of the rates for all transitions
	 * @return the sum of the rates of all possible transitions
	 */
	public double getRateTotal() {
		double total = 0;
		for (Transition t : getAllTransitions()) {
			total += t.getRate();
		}
		return total;
	}
}
