package us.palpant.science.nucleosomes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manage the List of Transitions for a Lattice
 * @author timpalpant
 *
 */
public class RemodelerTransitionManager extends StatisticalPositioningTransitionManager {
	
	public RemodelerTransitionManager(Lattice lattice) {
		super(lattice);
	}
	
	@Override
	public List<Transition> getAllTransitions() {
		List<Transition> allTransitions = super.getAllTransitions();
		allTransitions.addAll(getRemodelerSlideTransitions());
		return allTransitions;
	}
	
	/**
	 * @return a List of SlideTransitions due to remodeling activity
	 */
	public List<SlideTransition> getRemodelerSlideTransitions() {
		List<SlideTransition> transitions = new ArrayList<SlideTransition>();
		
		int start = 0, linker = 0;
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
					linker = lattice.getPeriodicWrap(low) - lastHigh;
					if (linker >= Parameters.L_MIN) {
						transitions.add(new SlideTransition(lattice, object, pos-Parameters.REMODELER_STEP_SIZE, getRateForLinker(linker)));
					}
				}
				if (lastHigh+1 >= end) {
					linker = low - lattice.getPeriodicWrap(lastHigh);
					if (linker <= Parameters.L_MIN) {
						transitions.add(new SlideTransition(lattice, last, lastPos+Parameters.REMODELER_STEP_SIZE, getRateForLinker(linker)));
					}
				}
			}
			
			do {
				// Slide left
				linker = low - start;
				if (linker >= Parameters.L_MIN) {
					transitions.add(new SlideTransition(lattice, object, pos-Parameters.REMODELER_STEP_SIZE, getRateForLinker(linker)));
				}
				start = high;
				
				if (it.hasNext()) {
					LatticeObject next = it.next();
					int nPos = lattice.getPosition(next);
					low = next.low(nPos);
					// Slide right
					linker = low - high;
					if (linker >= Parameters.L_MIN) {
						transitions.add(new SlideTransition(lattice, object, pos+Parameters.REMODELER_STEP_SIZE, getRateForLinker(linker)));
					}
					object = next;
					pos = nPos;
					high = next.high(nPos);
				}
			} while (it.hasNext());
			
			// The last nucleosome
			linker = low - start;
			if (linker >= Parameters.L_MIN) {
				transitions.add(new SlideTransition(lattice, object, pos-Parameters.REMODELER_STEP_SIZE, getRateForLinker(linker)));
			}
			linker = end - high;
			if (linker >= Parameters.L_MIN) {
				transitions.add(new SlideTransition(lattice, object, pos+Parameters.REMODELER_STEP_SIZE, getRateForLinker(linker)));
			}
		}
		
		return transitions;
	}
	
	private double getRateForLinker(int linker) {
		double pml = Parameters.K0;
		if (Parameters.LINKER_DEPENDENT_RATE) {
			if (linker > Parameters.L_MAX) {
				linker = Parameters.L_MAX;
			}
			pml *= Math.exp(Parameters.A * linker);
		}	
		
		return pml * Parameters.ATP / (Parameters.ATP + Parameters.K_M);
	}

}
