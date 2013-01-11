package us.palpant.science;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
  
  private List<SlideTransition> remodelerTransitions = new ArrayList<>();

  public RemodelerTransitionManager(Lattice lattice, Parameters params) {
    super(lattice, params);
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
    remodelerTransitions.clear();

    int start = 0, linker = 0;
    int end = lattice.size();
    Iterator<LatticeObject> it = lattice.iterator();
    if (it.hasNext()) {
      LatticeObject object = it.next();
      int pos = object.getPos();
      int low = object.low();
      int high = object.high();

      if (lattice.getBoundaryCondition() == Lattice.BoundaryCondition.PERIODIC) {
        LatticeObject last = lattice.last();
        int lastPos = last.getPos();
        int lastHigh = last.high();
        if (low <= 0) {
          linker = lattice.getPeriodicWrap(low) - lastHigh;
          if (linker >= params.getLMin()) {
            remodelerTransitions.add(new SlideTransition(object, pos-params.getRemodelerStepSize(), getRateForLinker(linker)));
          }
        }
        if (lastHigh + 1 >= end) {
          linker = low - lattice.getPeriodicWrap(lastHigh);
          if (linker <= params.getLMin()) {
            remodelerTransitions.add(new SlideTransition(last, lastPos+params.getRemodelerStepSize(), getRateForLinker(linker)));
          }
        }
      }

      do {
        // Slide left
        linker = low - start;
        if (linker >= params.getLMin()) {
          remodelerTransitions.add(new SlideTransition(object, pos-params.getRemodelerStepSize(), getRateForLinker(linker)));
        }
        start = high;

        if (it.hasNext()) {
          LatticeObject next = it.next();
          int nPos = next.getPos();
          low = next.low();
          // Slide right
          linker = low - high;
          if (linker >= params.getLMin()) {
            remodelerTransitions.add(new SlideTransition(object, pos+params.getRemodelerStepSize(), getRateForLinker(linker)));
          }
          object = next;
          pos = nPos;
          high = next.high();
        }
      } while (it.hasNext());

      // The last nucleosome
      linker = low - start;
      if (linker >= params.getLMin()) {
        remodelerTransitions.add(new SlideTransition(object, pos-params.getRemodelerStepSize(), getRateForLinker(linker)));
      }
      linker = end - high;
      if (linker >= params.getLMin()) {
        remodelerTransitions.add(new SlideTransition(object, pos+params.getRemodelerStepSize(), getRateForLinker(linker)));
      }
    }

    return remodelerTransitions;
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
