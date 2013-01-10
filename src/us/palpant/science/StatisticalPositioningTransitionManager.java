package us.palpant.science;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

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

  private static final Logger log = Logger.getLogger(StatisticalPositioningTransitionManager.class);

  protected StatisticalPositioningTransitionManager(Lattice lattice, Parameters params) {
    super(lattice, params);
  }

  @Override
  public List<Transition> getAllTransitions() {
    List<AdsorptionTransition> adsorptionTransitions = getAdsorptionTransitions();
    double adsorptionRateTotal = getRateTotal(adsorptionTransitions);
    List<Transition> allTransitions = new ArrayList<Transition>(adsorptionTransitions);

    List<DesorptionTransition> desorptionTransitions = getDesorptionTransitions();
    double desorptionRateTotal = getRateTotal(desorptionTransitions);
    allTransitions.addAll(desorptionTransitions);

    List<SlideTransition> slideTransitions = getThermalSlideTransitions();
    double slideRateTotal = getRateTotal(slideTransitions);
    allTransitions.addAll(slideTransitions);

    double rateTotal = adsorptionRateTotal + desorptionRateTotal + slideRateTotal;
    log.debug("Transition probabilities: adsorption = " + 100 * adsorptionRateTotal / rateTotal + " desorption = "
        + 100 * desorptionRateTotal / rateTotal + " slide = " + 100 * slideRateTotal / rateTotal);
    return allTransitions;
  }

  /**
   * Get a List of all possible AdsorptionTransitions
   * 
   * @return a List of all possible AdsorptionTransitions
   */
  public List<AdsorptionTransition> getAdsorptionTransitions() {
    List<AdsorptionTransition> transitions = new ArrayList<AdsorptionTransition>();
    int start = 0;
    int end = lattice.size();
    int halfNuc = params.getNucSize() / 2;
    for (LatticeObject object : lattice) {
      int pos = lattice.getPosition(object);
      int low = object.low(pos);
      for (int i = start+halfNuc+1; i+halfNuc < low; i++) {
    	LatticeObject newObject = new FixedWidthObject(params.getNucSize());
        transitions.add(new AdsorptionTransition(lattice, newObject, i, params.getKOn()));
      }
      start = object.high(pos);
    }

    // One last check to see if a nucleosome can be added on the end
    // If periodic boundary conditions, check if a nucleosome can be added with
    // wrapping
    if (lattice.getBoundaryCondition() == Lattice.BoundaryCondition.PERIODIC && lattice.numObjects() > 0) {
      LatticeObject first = lattice.first();
      int firstLow = first.low(lattice.getPosition(first));
      for (int i = start+halfNuc+1; lattice.getPeriodicWrap(i+halfNuc) < firstLow; i++) {
        LatticeObject newObject = new FixedWidthObject(params.getNucSize());
        transitions.add(new AdsorptionTransition(lattice, newObject, i, params.getKOn()));
      }
    } else {
      for (int i = start+halfNuc+1; i+halfNuc < end; i++) {
    	LatticeObject newObject = new FixedWidthObject(params.getNucSize());
        transitions.add(new AdsorptionTransition(lattice, newObject, i, params.getKOn()));
      }
    }

    return transitions;
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
    List<DesorptionTransition> transitions = new ArrayList<DesorptionTransition>();

    for (LatticeObject object : lattice) {
      double rate = params.getKOn() * Math.exp(params.getBeta() * lattice.getPotential(lattice.getPosition(object)));
      transitions.add(new DesorptionTransition(lattice, object, rate));
    }

    return transitions;
  }

  /**
   * Get a List of all possible SlideTransitions
   * 
   * @return a List of all possible SlideTransitions
   */
  public List<SlideTransition> getThermalSlideTransitions() {
    List<SlideTransition> transitions = new ArrayList<SlideTransition>();

    int start = -1;
    int end = lattice.size();
    Iterator<LatticeObject> it = lattice.iterator();
    if (it.hasNext()) {
      LatticeObject object = it.next();
      int pos = lattice.getPosition(object);
      int low = object.low(pos);
      int high = object.high(pos);

      if (lattice.getBoundaryCondition() == Lattice.BoundaryCondition.PERIODIC) {
        LatticeObject last = lattice.last();
        int lastPos = lattice.getPosition(last);
        int lastHigh = last.high(lastPos);
        if (low <= 0) {
          if (lastHigh < lattice.getPeriodicWrap(low - 1)) {
            transitions.add(getThermalSlideTransition(object, pos-1));
          }
        }
        if (lastHigh + 1 >= end) {
          if (lattice.getPeriodicWrap(lastHigh + 1) < low) {
            transitions.add(getThermalSlideTransition(object, pos+1));
          }
        }
      }

      do {
        // Slide left
        if (low-1 > start) {
          transitions.add(getThermalSlideTransition(object, pos-1));
        }
        start = high;

        if (it.hasNext()) {
          LatticeObject next = it.next();
          int nPos = lattice.getPosition(next);
          low = next.low(nPos);
          // Slide right
          if (high+1 < low) {
            transitions.add(getThermalSlideTransition(object, pos+1));
          }
          object = next;
          pos = nPos;
          high = next.high(nPos);
        }
      } while (it.hasNext());

      // The last nucleosome
      if (low-1 > start) {
        transitions.add(getThermalSlideTransition(object, pos-1));
      }
      if (high+1 < end) {
        transitions.add(getThermalSlideTransition(object, pos+1));
      }
    }

    return transitions;
  }
  
  private SlideTransition getThermalSlideTransition(LatticeObject object, int newPos) {
	int pos = lattice.getPosition(object);
	double vi = lattice.getPotential(pos);
  	double vj = lattice.getPotential(lattice.getPeriodicWrap(newPos));
  	double rate = params.getDiffusion() * Math.exp((params.getBeta()/2) * (vi - vj));
    return new SlideTransition(lattice, object, newPos, rate);
  }

}
