package us.palpant.science.nucleosomes;

import org.apache.log4j.Logger;

/**
 * A Transition for when a nucleosome moves to a new position in the lattice
 * @author palpant
 *
 */
public class SlideTransition extends FixedRateTransition {

	private static final Logger log = Logger.getLogger(SlideTransition.class);
	
	protected final Lattice lattice;
	protected final LatticeObject object;
	/**
	 * The new position in the lattice
	 */
	protected int newPosition;
	
	public SlideTransition(Lattice lattice, LatticeObject object, int newPosition, double rateConstant) {
		super(rateConstant);
		this.lattice = lattice;
		this.object = object;
		this.newPosition = newPosition;
	}
	
	@Override
	public void perform() {
		log.debug("Sliding object "+object+" from "+lattice.getPosition(object)+" to "+newPosition);
		lattice.setPosition(object, newPosition);
	}

}
