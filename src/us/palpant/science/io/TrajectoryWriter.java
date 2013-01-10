package us.palpant.science.io;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;

/**
 * Write trajectory files, which are series of Frames
 * @author timpalpant
 *
 */
public class TrajectoryWriter implements Closeable {
  private static final Logger log = Logger.getLogger(TrajectoryWriter.class);
  
  private final DataOutputStream os;
  
  public TrajectoryWriter(OutputStream os, int latticeSize) throws IOException {
    log.debug("Initialized output trajectory for lattice with "+latticeSize+" grid points");
    this.os = new DataOutputStream(os);
    this.os.writeInt(latticeSize);
  }
  
  public TrajectoryWriter(Path p, int latticeSize) throws IOException {
    this(new BufferedOutputStream(Files.newOutputStream(p)), latticeSize);
  }
  
  public void writeFrame(Frame frame) throws IOException {
    writeFrame(frame.getTime(), frame.getPositions());
  }
  
  public void writeFrame(double time, int[] positions) throws IOException {
    os.writeDouble(time);
    os.writeInt(positions.length);
    for (int p : positions) {
      os.writeInt(p);
    }
  }

  @Override
  public void close() throws IOException {
    os.close();
  }
}
