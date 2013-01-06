package us.palpant.science.nucleosomes;

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
		double vi = lattice.potential(lattice.getPosition(object));
		double vj = lattice.potential(lattice.getPeriodicWrap(newPosition));
		return rate * Math.exp((Parameters.BETA/2) * (vi-vj));
	}

}
