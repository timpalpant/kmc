package us.palpant.science.transitions;

import org.apache.log4j.Logger;

import us.palpant.science.objects.LatticeObject;

/**
 * A Transition for adding a new LatticeObject to the Lattice
 * 
 * @author palpant
 * 
 */
public class AdsorptionTransition extends FixedRateTransition {

  private static final Logger log = Logger.getLogger(AdsorptionTransition.class);

  private final LatticeObject object;

  public AdsorptionTransition(LatticeObject object, double rate) {
    super(rate);
    this.object = object;
  }

  @Override
  public void perform() {
    if (log.isDebugEnabled()) {
      log.debug(toString());
    }
    object.getLattice().addObject(object);
  }

  @Override
  public String toString() {
    return "Adsorbing object at position " + object.getPos() 
        + ", occupying " + object.low() + "-" + object.high();
  }

}
