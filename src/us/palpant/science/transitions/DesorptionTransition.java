package us.palpant.science.transitions;

import org.apache.log4j.Logger;

import us.palpant.science.objects.LatticeObject;

/**
 * A Transition for when a nucleosome is removed from the lattice
 * 
 * @author palpant
 * 
 */
public class DesorptionTransition extends FixedRateTransition {

  private static final Logger log = Logger.getLogger(DesorptionTransition.class);

  private final LatticeObject object;
  
  public DesorptionTransition(LatticeObject object, double rate) {
    super(rate);
    this.object = object;
  }

  @Override
  public void perform() {
    if (log.isDebugEnabled()) {
      log.debug(toString());
    }
    object.getLattice().removeObject(object);
  }

  @Override
  public String toString() {
    return "Desorbing " + object + " from position " + object.getPos();
  }

}
