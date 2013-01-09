package us.palpant.science.transitions;

/**
 * A Transition that occurs with a fixed rate constant
 * 
 * @author timpalpant
 * 
 */
public abstract class FixedRateTransition implements Transition {
  protected double rate;

  public FixedRateTransition(double rate) {
    this.rate = rate;
  }

  @Override
  public final double getRate() {
    return rate;
  }
}
