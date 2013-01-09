package us.palpant.science.transitions;

import us.palpant.science.Lattice;
import us.palpant.science.Parameters;
import us.palpant.science.objects.LatticeObject;

/**
 * A Transition whose rate is Boltzmann-weighted by the potential at the
 * LatticeObject's position
 * 
 * @author timpalpant
 * 
 */
public abstract class PotentialTransition implements Transition {

  protected final Lattice lattice;
  protected final LatticeObject object;
  protected double rateConstant;

  public PotentialTransition(Lattice lattice, LatticeObject object, double rateConstant) {
    this.lattice = lattice;
    this.object = object;
    this.rateConstant = rateConstant;
  }

  @Override
  public final double getRate() {
    return rateConstant * Math.exp(Parameters.BETA * lattice.getPotential(lattice.getPosition(object)));
  }

}
