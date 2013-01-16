package us.palpant.science.io;

import java.io.Closeable;
import java.io.IOException;

public interface TrajectoryWriter extends Closeable {

  /**
   * @param frame
   * @throws IOException
   */
  void writeFrame(Frame frame) throws IOException;

  /**
   * @param time
   * @param positions
   * @throws IOException
   */
  void writeFrame(double time, int[] positions) throws IOException;

  /**
   * @return the number of frames written to this trajectory
   */
  int getNumFrames();

}