package us.palpant.science.io;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import us.palpant.science.Lattice;
import us.palpant.science.Parameters;

/**
 * Read Trajectory files, which are series of Frames
 * @author timpalpant
 *
 */
public class TrajectoryReader implements Closeable {
  private static final Logger log = Logger.getLogger(TrajectoryReader.class);
  
  private final ObjectInputStream is;
  private final Lattice lattice;
  private final Parameters params;
  private boolean eof = false;
  
  public TrajectoryReader(InputStream is) throws IOException, ClassNotFoundException {
    this.is = new ObjectInputStream(is);
    lattice = (Lattice) this.is.readObject();
    params = (Parameters) this.is.readObject();
    log.debug("Trajectory for lattice with "+lattice.size()+" grid points");
  }
  
  public TrajectoryReader(Path p) throws IOException, ClassNotFoundException {
    this(new BufferedInputStream(Files.newInputStream(p)));
  }

  public Frame readFrame() throws IOException, ClassNotFoundException {
    if (!eof) {
      try {
        return (Frame) is.readObject();
      } catch (EOFException e) {
        eof = true;
        close();
      }
    }
    
    return null;
  }

  @Override
  public void close() throws IOException {
    is.close();
  }

  public Lattice getLattice() {
    return lattice;
  }
  
  public Parameters getParams() {
    return params;
  }
}
