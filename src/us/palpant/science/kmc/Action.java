package us.palpant.science.kmc;

import us.palpant.science.kmc.geometry.Lattice.Coordinate;

/**
 * An Action is a downstream Contingency
 * @author palpant
 *
 */
public class Action extends Contingency {

  public Action(Coordinate coord, State state) {
    super(coord, state);
  }

}
