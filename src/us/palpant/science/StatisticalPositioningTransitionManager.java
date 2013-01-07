package us.palpant.science;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import us.palpant.science.transitions.AdsorptionTransition;
import us.palpant.science.transitions.DesorptionTransition;
import us.palpant.science.transitions.SlideTransition;
import us.palpant.science.transitions.ThermalSlideTransition;
import us.palpant.science.transitions.Transition;

/**
 * A TransitionManager that only considers adsorption, desorption, and thermal equilibration
 * @author timpalpant
 *
 */
public class StatisticalPositioningTransitionManager extends TransitionManager {

	private static final Logger log = Logger.getLogger(StatisticalPositioningTransitionManager.class);
	
	protected StatisticalPositioningTransitionManager(Lattice lattice) {
		super(lattice);
	}
	
	@Override
	public List<Transition> getAllTransitions() {
		List<Transition> allTransitions = new ArrayList<Transition>(getAdsorptionTransitions());
		allTransitions.addAll(getDesorptionTransitions());
		allTransitions.addAll(getThermalSlideTransitions());
		return allTransitions;
	}
	
	/**
	 * Get a List of all possible AdsorptionTransitions
	 * @return a List of all possible AdsorptionTransitions
	 */
	protected List<AdsorptionTransition> getAdsorptionTransitions() {
		List<AdsorptionTransition> transitions = new ArrayList<AdsorptionTransition>();
		int start = 0;
		int end = lattice.size();
		LatticeObjectFactory factory = new Nucleosome.Factory();
		for (LatticeObject object : lattice) {
			int pos = lattice.getPosition(object);
			int low = object.low(pos);
			for (int i = start+Parameters.NUC_SIZE/2+1; i+Parameters.NUC_SIZE/2 < low; i++) {
				transitions.add(new AdsorptionTransition(lattice, factory, i));
			}
			start = object.high(pos);
		}
		
		// One last check to see if a nucleosome can be added on the end
		// If periodic boundary conditions, check if a nucleosome can be added with wrapping
		if (lattice.getBoundaryCondition() == BoundaryCondition.PERIODIC && lattice.numObjects() > 0) {
			LatticeObject first = lattice.first();
			int firstLow = first.low(lattice.getPosition(first));
			for (int i = start+Parameters.NUC_SIZE/2+1; lattice.getPeriodicWrap(i+Parameters.NUC_SIZE/2) < firstLow; i++) {
				transitions.add(new AdsorptionTransition(lattice, factory, i));
			}
		} else {
			for (int i = start+Parameters.NUC_SIZE/2+1; i+Parameters.NUC_SIZE/2 < end; i++) {
				transitions.add(new AdsorptionTransition(lattice, factory, i));
			}
		}
		
		return transitions;
	}
	
	/**
	 * Get a List of all possible DesorptionTransitions
	 * There is one DesorptionTransition for each nucleosome in the Lattice,
	 * so this List will only need to be recalculated when a nucleosome is
	 * added to the Lattice (an AdsorptionTransition).
	 * @return a List of all possible DesorptionTransitions
	 */
	protected List<DesorptionTransition> getDesorptionTransitions() {
		List<DesorptionTransition> transitions = new ArrayList<DesorptionTransition>();
		
		for (LatticeObject object : lattice) {
			transitions.add(new DesorptionTransition(lattice, object));
		}
		
		return transitions;
	}
	
	/**
	 * Get a List of all possible SlideTransitions
	 * @return a List of all possible SlideTransitions
	 */
	protected List<SlideTransition> getThermalSlideTransitions() {
		List<SlideTransition> transitions = new ArrayList<SlideTransition>();
		
		int start = -1;
		int end = lattice.size();
		Iterator<LatticeObject> it = lattice.iterator();
		if (it.hasNext()) {
			LatticeObject object = it.next();
			int pos = lattice.getPosition(object);
			int low = object.low(pos);
			int high = object.high(pos);
			
			if (lattice.getBoundaryCondition() == BoundaryCondition.PERIODIC) {
				LatticeObject last = lattice.last();
				int lastPos = lattice.getPosition(last);
				int lastHigh = last.high(lastPos);
				if (low <= 0) {
					if (lastHigh < lattice.getPeriodicWrap(low-1)) {
						transitions.add(new ThermalSlideTransition(lattice, object, low-1));
					}
				}
				if (lastHigh+1 >= end) {
					if (lattice.getPeriodicWrap(lastHigh+1) < low) {
						transitions.add(new ThermalSlideTransition(lattice, last, lastHigh+1));
					}
				}
			}
			
			do {
				// Slide left
				if (low-1 > start) {
					transitions.add(new ThermalSlideTransition(lattice, object, pos-1));
				}
				start = high;
				
				if (it.hasNext()) {
					LatticeObject next = it.next();
					int nPos = lattice.getPosition(next);
					low = next.low(nPos);
					// Slide right
					if (high+1 < low) {
						transitions.add(new ThermalSlideTransition(lattice, object, pos+1));
					}
					object = next;
					pos = nPos;
					high = next.high(nPos);
				}
			} while (it.hasNext());
			
			// The last nucleosome
			if (low-1 > start) {
				transitions.add(new ThermalSlideTransition(lattice, object, pos-1));
			}
			if (high+1 < end) {
				transitions.add(new ThermalSlideTransition(lattice, object, pos+1));
			}
		}
		
		return transitions;
	}

}
