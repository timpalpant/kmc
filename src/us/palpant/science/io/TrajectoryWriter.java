package us.palpant.science.io;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
public class TrajectoryWriter implements Closeable {
  private static final Logger log = Logger.getLogger(TrajectoryWriter.class);
  
  private final ObjectOutputStream os;
  
  public TrajectoryWriter(OutputStream os, Lattice lattice, Parameters params) throws IOException {
    log.debug("Initialized output trajectory for lattice with "+lattice.size()+" grid points");
    this.os = new ObjectOutputStream(os);
    this.os.writeObject(lattice);
    this.os.writeObject(params);
  }
  
  public TrajectoryWriter(Path p, Lattice lattice, Parameters params) throws IOException {
    this(new BufferedOutputStream(Files.newOutputStream(p)), lattice, params);
  }
  
  public void writeFrame(Frame frame) throws IOException {
    os.writeObject(frame);
  }
  
  public void writeFrame(double time, int[] positions) throws IOException {
    writeFrame(new Frame(time, positions));
  }

  @Override
  public void close() throws IOException {
    os.close();
  }
}
