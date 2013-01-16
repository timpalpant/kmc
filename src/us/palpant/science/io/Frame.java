package us.palpant.science.io;

/**
 * Model for a single frame of a trajectory
 * @author timpalpant
 *
 */
public class Frame {

  private double time;
  private int[] positions;
  
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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(time);
    if (positions.length > 0) {
      builder.append('\t').append(positions[0]);
      for (int i = 1; i < positions.length; i++) {
        builder.append(',').append(positions[i]);
      }
    }
    return builder.toString();
  }
}
