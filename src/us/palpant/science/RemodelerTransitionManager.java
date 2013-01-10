package us.palpant.science;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import us.palpant.science.objects.LatticeObject;
import us.palpant.science.transitions.SlideTransition;
import us.palpant.science.transitions.Transition;

/**
 * Include all thermal (equilibrium) transitions, plus non-equilibrium
 * ATP-dependent remodeling
 * 
 * @author timpalpant
 * 
 */
public class RemodelerTransitionManager extends StatisticalPositioningTransitionManager {

  private static final Logger log = Logger.getLogger(RemodelerTransitionManager.class);

  public RemodelerTransitionManager(Lattice lattice, Parameters params) {
    super(lattice, params);
  }

  @Override
  public List<Transition> getAllTransitions() {
    List<Transition> allTransitions = super.getAllTransitions();
    double thermalRateTotal = getRateTotal(allTransitions);

    List<SlideTransition> remodelerTransitions = getRemodelerSlideTransitions();
    double remodelerRateTotal = getRateTotal(remodelerTransitions);
    allTransitions.addAll(remodelerTransitions);

    double rateTotal = thermalRateTotal + remodelerRateTotal;
    log.debug("Transition probabilities: thermal = " + 100 * thermalRateTotal / rateTotal + " remodeler = " + 100
        * remodelerRateTotal / rateTotal);
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

      if (lattice.getBoundaryCondition() == Lattice.BoundaryCondition.PERIODIC) {
        LatticeObject last = lattice.last();
        int lastPos = lattice.getPosition(last);
        int lastHigh = last.high(lastPos);
        if (low <= 0) {
          linker = lattice.getPeriodicWrap(low) - lastHigh;
          if (linker >= params.getLMin()) {
            transitions.add(new SlideTransition(lattice, object, pos - params.getRemodelerStepSize(),
                getRateForLinker(linker)));
          }
        }
        if (lastHigh + 1 >= end) {
          linker = low - lattice.getPeriodicWrap(lastHigh);
          if (linker <= params.getLMin()) {
            transitions.add(new SlideTransition(lattice, last, lastPos + params.getRemodelerStepSize(),
                getRateForLinker(linker)));
          }
        }
      }

      do {
        // Slide left
        linker = low - start;
        if (linker >= params.getLMin()) {
          transitions.add(new SlideTransition(lattice, object, pos - params.getRemodelerStepSize(),
              getRateForLinker(linker)));
        }
        start = high;

        if (it.hasNext()) {
          LatticeObject next = it.next();
          int nPos = lattice.getPosition(next);
          low = next.low(nPos);
          // Slide right
          linker = low - high;
          if (linker >= params.getLMin()) {
            transitions.add(new SlideTransition(lattice, object, pos + params.getRemodelerStepSize(),
                getRateForLinker(linker)));
          }
          object = next;
          pos = nPos;
          high = next.high(nPos);
        }
      } while (it.hasNext());

      // The last nucleosome
      linker = low - start;
      if (linker >= params.getLMin()) {
        transitions.add(new SlideTransition(lattice, object, pos - params.getRemodelerStepSize(),
            getRateForLinker(linker)));
      }
      linker = end - high;
      if (linker >= params.getLMin()) {
        transitions.add(new SlideTransition(lattice, object, pos + params.getRemodelerStepSize(),
            getRateForLinker(linker)));
      }
    }

    return transitions;
  }

  private double getRateForLinker(int linker) {
    double rate = params.getK0();
    if (params.useLinkerDependentRate()) {
      if (linker > params.getLMax()) {
        linker = params.getLMax();
      }
      rate *= Math.exp(params.getA() * linker);
    }

    return rate;
  }

}
