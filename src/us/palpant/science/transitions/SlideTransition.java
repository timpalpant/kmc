package us.palpant.science.transitions;

import org.apache.log4j.Logger;

import us.palpant.science.objects.LatticeObject;

/**
 * A Transition for when a nucleosome moves to a new position in the lattice
 * 
 * @author palpant
 * 
 */
public class SlideTransition implements Transition {

  private static final Logger log = Logger.getLogger(SlideTransition.class);

  protected final LatticeObject object;
  /**
   * The new position in the lattice
   */
  protected int newPosition;
  protected double rate;

  public SlideTransition(LatticeObject object, int newPosition, double rate) {
    this.object = object;
    this.newPosition = newPosition;
    this.rate = rate;
  }

  @Override
  public void perform() {
    if (log.isDebugEnabled()) {
      log.debug(toString());
    }
    object.setPos(newPosition);
  }

  @Override
  public String toString() {
    return "Slide " + object + " from " + object.getPos() + " to " + newPosition;
  }

  @Override
  public double getRate() {
    return rate;
  }

}
