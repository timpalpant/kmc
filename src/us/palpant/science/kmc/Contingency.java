package us.palpant.science.kmc;

import java.util.HashSet;
import java.util.Set;

import us.palpant.science.kmc.geometry.Lattice.Coordinate;

/**
 * An Edge in a dependency graph
 * @author palpant
 *
 */
public class Contingency {

  private final Coordinate coord;
  private final State state;
  
  public Contingency(Coordinate coord, State state) {
    this.coord = coord;
    this.state = state;
  }

  public final Coordinate getCoord() {
    return coord;
  }

  public final State getState() {
    return state;
  }
  
  @Override
  public String toString() {
    return coord + " - " + state;
  }
  
  /**
   * Get the union set of coordinates for an array of Contingencies
   * @param c an array of Contingencies
   * @return the union set of coordinates in the Contingencies
   */
  public static final Coordinate[] getCoordinates(Contingency[] contingencies) {
    Set<Coordinate> coordSet = new HashSet<>();
    for (Contingency c : contingencies) {
      coordSet.add(c.getCoord());
    }
    Coordinate[] coordinates = new Coordinate[coordSet.size()];
    coordSet.toArray(coordinates);
    return coordinates;
  }

}
