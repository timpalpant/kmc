package us.palpant.science.nucleosomes;

import org.apache.log4j.Logger;

/**
 * A Transition for adding a new LatticeObject to the Lattice
 * @author palpant
 *
 */
public class AdsorptionTransition extends FixedRateTransition {
	
	private static final Logger log = Logger.getLogger(AdsorptionTransition.class);

	private Lattice lattice;
	private LatticeObjectFactory factory;
	private int position;
	
	public AdsorptionTransition(Lattice lattice, LatticeObjectFactory factory, int position, double rate) {
		super(rate);
		this.lattice = lattice;
		this.factory = factory;
		this.position = position;
	}
	
	public AdsorptionTransition(Lattice lattice, LatticeObjectFactory factory, int position) {
		this(lattice, factory, position, Parameters.K_ON);
	}

	@Override
	public void perform() {
		LatticeObject object = factory.newInstance();
		log.debug("Adsorbing object at position "+position+", occupying "+object.low(position)+"-"+object.high(position));
		lattice.addObject(object, position);
	}

}
