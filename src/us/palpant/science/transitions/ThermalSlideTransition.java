package us.palpant.science.transitions;

import us.palpant.science.Lattice;
import us.palpant.science.LatticeObject;
import us.palpant.science.Parameters;

/**
 * A SlideTransition representing thermal equilibration to a new location
 * @author palpant
 *
 */
public class ThermalSlideTransition extends SlideTransition {
	
	public ThermalSlideTransition(Lattice lattice, LatticeObject object, int newPosition, double diffusionConstant) {
		super(lattice, object, newPosition, diffusionConstant);
	}
	
	public ThermalSlideTransition(Lattice lattice, LatticeObject object, int newPosition) {
		this(lattice, object, newPosition, Parameters.DIFFUSION);
	}

	@Override
	public double getRate() {
		double vi = lattice.getPotential(lattice.getPosition(object));
		double vj = lattice.getPotential(lattice.getPeriodicWrap(newPosition));
		return rate * Math.exp((Parameters.BETA/2) * (vi-vj));
	}

}
