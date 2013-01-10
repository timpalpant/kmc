package us.palpant.science.transitions;

import org.apache.log4j.Logger;

import us.palpant.science.Lattice;
import us.palpant.science.objects.LatticeObject;

/**
 * A Transition for when a nucleosome is removed from the lattice
 * 
 * @author palpant
 * 
 */
public class DesorptionTransition extends FixedRateTransition {

  private static final Logger log = Logger.getLogger(DesorptionTransition.class);

  private final Lattice lattice;
  private final LatticeObject object;
  
  public DesorptionTransition(Lattice lattice, LatticeObject object, double rate) {
    super(rate);
    this.lattice = lattice;
    this.object = object;
  }

  @Override
  public void perform() {
    log.debug("Desorbing " + object + " from position "
        + lattice.getPosition(object));
    lattice.removeObject(object);
  }

  @Override
  public String toString() {
    return "Desorption of " + object;
  }

}
