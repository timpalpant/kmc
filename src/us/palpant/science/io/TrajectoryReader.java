package us.palpant.science.io;

import java.io.Closeable;
import java.io.IOException;

public interface TrajectoryReader extends Closeable {

  Frame readFrame() throws IOException, ClassNotFoundException;

  /**
   * @return the number of Frames read from this trajectory
   */
  int getCurrentFrameNumber();

}