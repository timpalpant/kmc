package us.palpant.science.kmc;

import us.palpant.science.kmc.geometry.Lattice.Coordinate;

/**
 * A Condition is an upstream Contingency
 * @author palpant
 *
 */
public class Condition extends Contingency {

  public Condition(Coordinate coord, State state) {
    super(coord, state);
  }
  
  public boolean isSatified() {
    return getCoord().getState() == getState();
  }
  
}
