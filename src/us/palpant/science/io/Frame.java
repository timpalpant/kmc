package us.palpant.science.io;

/**
 * Model for a single frame of a trajectory
 * @author timpalpant
 *
 */
public class Frame {
  private final double time;
  private final int[] positions;
  
  public Frame(final double time, final int[] positions) {
    this.time = time;
    this.positions = positions;
  }

  /**
   * @return the time
   */
  public double getTime() {
    return time;
  }

  /**
   * @return the positions
   */
  public int[] getPositions() {
    return positions;
  }
}
