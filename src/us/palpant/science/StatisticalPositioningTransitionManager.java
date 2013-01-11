package us.palpant.science;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import us.palpant.science.objects.LatticeObject;
import us.palpant.science.objects.FixedWidthObject;
import us.palpant.science.transitions.AdsorptionTransition;
import us.palpant.science.transitions.DesorptionTransition;
import us.palpant.science.transitions.SlideTransition;
import us.palpant.science.transitions.Transition;

/**
 * A TransitionManager that only considers adsorption, desorption, and thermal
 * equilibration
 * 
 * @author timpalpant
 * 
 */
public class StatisticalPositioningTransitionManager extends TransitionManager {
  
  private List<Transition> allTransitions = new ArrayList<>();
  private List<AdsorptionTransition> adsorptionTransitions = new ArrayList<>();
  private List<DesorptionTransition> desorptionTransitions = new ArrayList<>();
  private List<SlideTransition> slideTransitions = new ArrayList<>();

  protected StatisticalPositioningTransitionManager(Lattice lattice, Parameters params) {
    super(lattice, params);
  }

  @Override
  public List<Transition> getAllTransitions() {
    allTransitions.clear();
    allTransitions.addAll(getAdsorptionTransitions());
    allTransitions.addAll(getDesorptionTransitions());
    allTransitions.addAll(getThermalSlideTransitions());
    return allTransitions;
  }

  /**
   * Get a List of all possible AdsorptionTransitions
   * 
   * @return a List of all possible AdsorptionTransitions
   */
  public List<AdsorptionTransition> getAdsorptionTransitions() {
    adsorptionTransitions.clear();
    int start = 0;
    int end = lattice.size();
    int halfNuc = params.getNucSize() / 2;
    for (LatticeObject object : lattice) {
      int low = object.low();
      for (int i = start+halfNuc+1; i+halfNuc < low; i++) {
    	LatticeObject newObject = new FixedWidthObject(lattice, i, params.getNucSize());
    	adsorptionTransitions.add(new AdsorptionTransition(newObject, params.getKOn()));
      }
      start = object.high();
    }

    // One last check to see if a nucleosome can be added on the end
    // If periodic boundary conditions, check if a nucleosome can be added with wrapping
    if (lattice.getBoundaryCondition() == Lattice.BoundaryCondition.PERIODIC && lattice.numObjects() > 0) {
      LatticeObject first = lattice.first();
      int firstLow = first.low();
      for (int i = start+halfNuc+1; lattice.getPeriodicWrap(i+halfNuc) < firstLow; i++) {
        LatticeObject newObject = new FixedWidthObject(lattice, i, params.getNucSize());
        adsorptionTransitions.add(new AdsorptionTransition(newObject, params.getKOn()));
      }
    } else {
      for (int i = start+halfNuc+1; i+halfNuc < end; i++) {
    	LatticeObject newObject = new FixedWidthObject(lattice, i, params.getNucSize());
    	adsorptionTransitions.add(new AdsorptionTransition(newObject, params.getKOn()));
      }
    }

    return adsorptionTransitions;
  }

  /**
   * Get a List of all possible DesorptionTransitions There is one
   * DesorptionTransition for each nucleosome in the Lattice, so this List will
   * only need to be recalculated when a nucleosome is added to the Lattice (an
   * AdsorptionTransition).
   * 
   * @return a List of all possible DesorptionTransitions
   */
  public List<DesorptionTransition> getDesorptionTransitions() {
    desorptionTransitions.clear();

    for (LatticeObject object : lattice) {
      double rate = params.getKOn() * Math.exp(params.getBeta() * lattice.getPotential(object.getPos()));
      desorptionTransitions.add(new DesorptionTransition(object, rate));
    }

    return desorptionTransitions;
  }

  /**
   * Get a List of all possible SlideTransitions
   * 
   * @return a List of all possible SlideTransitions
   */
  public List<SlideTransition> getThermalSlideTransitions() {
    slideTransitions.clear();

    int start = -1;
    int end = lattice.size();
    Iterator<LatticeObject> it = lattice.iterator();
    if (it.hasNext()) {
      LatticeObject object = it.next();
      int pos = object.getPos();
      int low = object.low();
      int high = object.high();

      if (lattice.getBoundaryCondition() == Lattice.BoundaryCondition.PERIODIC) {
        LatticeObject last = lattice.last();
        int lastHigh = last.high();
        if (low <= 0) {
          if (lastHigh < lattice.getPeriodicWrap(low - 1)) {
            slideTransitions.add(getThermalSlideTransition(object, pos-1));
          }
        }
        if (lastHigh + 1 >= end) {
          if (lattice.getPeriodicWrap(lastHigh + 1) < low) {
            slideTransitions.add(getThermalSlideTransition(object, pos+1));
          }
        }
      }

      do {
        // Slide left
        if (low-1 > start) {
          slideTransitions.add(getThermalSlideTransition(object, pos-1));
        }
        start = high;

        if (it.hasNext()) {
          LatticeObject next = it.next();
          int nPos = next.getPos();
          low = next.low();
          // Slide right
          if (high+1 < low) {
            slideTransitions.add(getThermalSlideTransition(object, pos+1));
          }
          object = next;
          pos = nPos;
          high = next.high();
        }
      } while (it.hasNext());

      // The last nucleosome
      if (low-1 > start) {
        slideTransitions.add(getThermalSlideTransition(object, pos-1));
      }
      if (high+1 < end) {
        slideTransitions.add(getThermalSlideTransition(object, pos+1));
      }
    }

    return slideTransitions;
  }
  
  private SlideTransition getThermalSlideTransition(LatticeObject object, int newPos) {
	double vi = lattice.getPotential(object.getPos());
  	double vj = lattice.getPotential(lattice.getPeriodicWrap(newPos));
  	double rate = params.getDiffusion() * Math.exp((params.getBeta()/2) * (vi - vj));
    return new SlideTransition(object, newPos, rate);
  }

}
