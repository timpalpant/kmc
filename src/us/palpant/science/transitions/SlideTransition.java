package us.palpant.science.transitions;

import org.apache.log4j.Logger;

import us.palpant.science.Lattice;
import us.palpant.science.LatticeObject;

/**
 * A Transition for when a nucleosome moves to a new position in the lattice
 * @author palpant
 *
 */
public class SlideTransition implements Transition {

	private static final Logger log = Logger.getLogger(SlideTransition.class);
	
	protected final Lattice lattice;
	protected final LatticeObject object;
	/**
	 * The new position in the lattice
	 */
	protected int newPosition;
	protected double rate;
	
	public SlideTransition(Lattice lattice, LatticeObject object, int newPosition, double rate) {
		this.lattice = lattice;
		this.object = object;
		this.newPosition = newPosition;
		this.rate = rate;
	}
	
	@Override
	public void perform() {
		log.debug(toString());
		lattice.setPosition(object, newPosition);
	}
	
	@Override
	public String toString() {
		return "Slide "+object+" from "+lattice.getPosition(object)+" to "+newPosition;
	}

	@Override
	public double getRate() {
		return rate;
	}

}
