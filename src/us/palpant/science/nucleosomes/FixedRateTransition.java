package us.palpant.science.nucleosomes;

/**
 * A Transition that occurs with a fixed rate constant
 * @author timpalpant
 *
 */
public abstract class FixedRateTransition implements Transition {
	protected double rate;
	
	public FixedRateTransition(double rate) {
		this.rate = rate;
	}
	
	@Override
	public double getRate() {
		return rate;
	}
}
