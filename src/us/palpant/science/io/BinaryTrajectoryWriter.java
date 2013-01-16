package us.palpant.science.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import us.palpant.science.Lattice;
import us.palpant.science.Parameters;

/**
 * Write trajectory files, which are series of Frames
 * @author timpalpant
 *
 */
public class BinaryTrajectoryWriter implements TrajectoryWriter {
  private static final Logger log = Logger.getLogger(BinaryTrajectoryWriter.class);
  
  private final ObjectOutputStream os;
  private int numFrames = 0;
  
  public BinaryTrajectoryWriter(Path p, Lattice lattice, Parameters params) throws IOException {
    log.debug("Initialized output trajectory for lattice with "+lattice.size()+" grid points");
    this.os = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(p)));
    this.os.writeObject(lattice);
    this.os.writeObject(params);
  }
  
  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryWriter#writeFrame(us.palpant.science.io.Frame)
   */
  @Override
  public void writeFrame(Frame frame) throws IOException {
    writeFrame(frame.getTime(), frame.getPositions());
  }
  
  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryWriter#writeFrame(double, int[])
   */
  @Override
  public void writeFrame(double time, int[] positions) throws IOException {
    os.writeDouble(time);
    os.writeInt(positions.length);
    for (int p : positions) {
      os.writeInt(p);
    }
    numFrames++;
  }

  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryWriter#close()
   */
  @Override
  public void close() throws IOException {
    os.close();
  }
  
  
  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryWriter#getNumFrames()
   */
  @Override
  public int getNumFrames() {
    return numFrames;
  }
}
