package us.palpant.science.transitions;
/**
 * Interface for all Transitions
 * @author palpant
 *
 */
public interface Transition {
	/**
	 * Perform the Transition represented by this object
	 */
	public abstract void perform();

	/**
	 * @return the rate at which this Transition occurs
	 */
	public double getRate();
}
