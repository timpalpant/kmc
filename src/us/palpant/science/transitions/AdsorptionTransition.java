package us.palpant.science.transitions;

import org.apache.log4j.Logger;

import us.palpant.science.Lattice;
import us.palpant.science.objects.LatticeObject;

/**
 * A Transition for adding a new LatticeObject to the Lattice
 * 
 * @author palpant
 * 
 */
public class AdsorptionTransition extends FixedRateTransition {

  private static final Logger log = Logger.getLogger(AdsorptionTransition.class);

  private final Lattice lattice;
  private final LatticeObject object;
  private final int position;

  public AdsorptionTransition(Lattice lattice, LatticeObject object, int position, double rate) {
    super(rate);
    this.lattice = lattice;
    this.object = object;
    this.position = position;
  }

  @Override
  public void perform() {
    log.debug("Adsorbing object at position " + position 
      + ", occupying " + object.low(position) + "-" + object.high(position));
    lattice.addObject(object, position);
  }

  @Override
  public String toString() {
    return "Adsorption at " + position;
  }

}
