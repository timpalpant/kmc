package us.palpant.science.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Model for a single frame of a trajectory
 * @author timpalpant
 *
 */
public class Frame implements Serializable {

  private static final long serialVersionUID = 1L;

  private double time;
  private int[] positions;
  
  public Frame(final double time, final int[] positions) {
    this.time = time;
    this.positions = positions;
  }
  
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeDouble(time);
    out.writeInt(positions.length);
    for (int p : positions) {
      out.writeInt(p);
    }
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    time = in.readDouble();
    int nPos = in.readInt();
    positions = new int[nPos];
    for (int i = 0; i < nPos; i++) {
      positions[i] = in.readInt();
    }
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
