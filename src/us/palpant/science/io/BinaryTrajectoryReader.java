package us.palpant.science.io;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
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
public class BinaryTrajectoryReader implements TrajectoryReader {
  private static final Logger log = Logger.getLogger(BinaryTrajectoryReader.class);
  public static final String INDEX_EXTENSION = ".idx";
  
  private final ObjectInputStream is;
  private final Lattice lattice;
  private final Parameters params;
  private TrajectoryIndex index;
  private int currentFrame = 0;
  private boolean eof = false;
  
  public BinaryTrajectoryReader(Path p) throws IOException, ClassNotFoundException {
    is = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(p)));
    lattice = (Lattice) this.is.readObject();
    log.debug("Trajectory lattice has "+lattice.size()+" grid points");
    params = (Parameters) this.is.readObject();
    // Look for an index
    Path indexFile = p.resolveSibling(p.getFileName()+INDEX_EXTENSION);
    if (Files.isReadable(indexFile)) {
      index = TrajectoryIndex.load(indexFile);
    }
  }

  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryReader#readFrame()
   */
  @Override
  public Frame readFrame() throws IOException, ClassNotFoundException {
    if (!eof) {
      try {
        double time = is.readDouble();
        int nPos = is.readInt();
        int[] positions = new int[nPos];
        for (int i = 0; i < nPos; i++) {
          positions[i] = is.readInt();
        }
        currentFrame++;
        return new Frame(time, positions);
      } catch (EOFException e) {
        eof = true;
        close();
      }
    }
    
    return null;
  }
  
  public Frame readFrame(int frameNum) throws ClassNotFoundException, IOException {
    seek(frameNum);
    return readFrame();
  }
  
  public void seek(int frameNum) {
    
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

  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryReader#getCurrentFrameNumber()
   */
  @Override
  public int getCurrentFrameNumber() {
    return currentFrame;
  }

  public TrajectoryIndex getIndex() {
    return index;
  }
}
