package us.palpant.science.io;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;

/**
 * Read Trajectory files, which are series of Frames
 * @author timpalpant
 *
 */
public class TrajectoryReader implements Closeable {
  private static final Logger log = Logger.getLogger(TrajectoryReader.class);
  
  private final DataInputStream is;
  private final int latticeSize;
  private boolean eof = false;
  
  public TrajectoryReader(InputStream is) throws IOException {
    this.is = new DataInputStream(is);
    this.latticeSize = this.is.readInt();
    log.debug("Trajectory for lattice with "+latticeSize+" grid points");
  }
  
  public TrajectoryReader(Path p) throws IOException {
    this(new BufferedInputStream(Files.newInputStream(p)));
  }

  public Frame readFrame() throws IOException, ClassNotFoundException {
    if (!eof) {
      try {
        double time = is.readDouble();
        int nPositions = is.readInt();
        int[] positions = new int[nPositions];
        for (int i = 0; i < nPositions; i++) {
          positions[i] = is.readInt();
        }
        return new Frame(time, positions);
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

  public int getLatticeSize() {
    return latticeSize;
  }
}
