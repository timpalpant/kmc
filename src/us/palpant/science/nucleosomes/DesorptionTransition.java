package us.palpant.science.nucleosomes;

import org.apache.log4j.Logger;

/**
 * A Transition for when a nucleosome is removed from the lattice
 * @author palpant
 *
 */
public class DesorptionTransition extends PotentialTransition {

	private static final Logger log = Logger.getLogger(DesorptionTransition.class);
	
	public DesorptionTransition(Lattice lattice, LatticeObject object, double rate) {
		super(lattice, object, rate);
	}
	
	public DesorptionTransition(Lattice lattice, LatticeObject object) {
		this(lattice, object, Parameters.K_ON);
	}

	@Override
	public void perform() {
		log.debug("Desorbing "+object+" from position "+lattice.getPosition(object));
		lattice.removeObject(object);
	}

}
