package us.palpant.science.kmc;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

/**
 * An Edge in a dependency graph
 * @author palpant
 *
 */
public class Contingency {

  private final int coord;
  private final State state;
  
  public Contingency(int coord, State state) {
    this.coord = coord;
    this.state = state;
  }

  public final int getCoord() {
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
  public static final int[] getCoordinates(Contingency[] contingencies) {
    Set<Integer> coordSet = new HashSet<>();
    for (Contingency c : contingencies) {
      coordSet.add(c.getCoord());
    }
    return ArrayUtils.toPrimitive(coordSet.toArray(new Integer[coordSet.size()]));
  }

}
