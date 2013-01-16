package us.palpant.science.io;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * A crude index for trajectories
 * @author timpalpant
 *
 */
public class TrajectoryIndex implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private int numFrames = 0;
  private double[] times;
  
  public void save(Path p) {
    
  }
  
  public static TrajectoryIndex load(Path p) {
    TrajectoryIndex index = new TrajectoryIndex();
    return index;
  }

  public int getNumFrames() {
    return numFrames;
  }

  public void setNumFrames(int numFrames) {
    this.numFrames = numFrames;
  }

}
