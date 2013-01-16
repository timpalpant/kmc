package us.palpant.science.io;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ed.javatools.BufferedRandomAccessFile;

import us.palpant.science.Lattice;
import us.palpant.science.Parameters;

/**
 * Read Trajectory files, which are series of Frames
 * @author timpalpant
 *
 */
public class AsciiTrajectoryReader implements TrajectoryReader {
  private static final Logger log = Logger.getLogger(AsciiTrajectoryReader.class);
  public static final String INDEX_EXTENSION = ".idx";
  
  private BufferedRandomAccessFile raf;
  private Lattice lattice;
  private Parameters params;
  private TrajectoryIndex index;
  private int currentFrame = 0;
  private boolean eof = false;
  private List<Path> trajectories = new ArrayList<>();
  
  public AsciiTrajectoryReader(Path p) throws IOException, ClassNotFoundException {
    try (BufferedReader reader = Files.newBufferedReader(p, Charset.defaultCharset())) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        trajectories.add(Paths.get(line));
      }
    } catch (MalformedInputException e) {
      trajectories.clear();
      trajectories.add(p);
    }
    
    init(trajectories.remove(0));
  }
  
  private void init(Path p) throws IOException, ClassNotFoundException {
    raf = new BufferedRandomAccessFile(p.toFile(), "r");
    // Look for an index
    Path indexFile = p.resolveSibling(p.getFileName()+INDEX_EXTENSION);
    if (Files.isReadable(indexFile)) {
      index = TrajectoryIndex.load(indexFile);
    }
    log.debug("Initialized trajectory for lattice with "+lattice.size()+" grid points");
  }

  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryReader#readFrame()
   */
  @Override
  public Frame readFrame() throws IOException, ClassNotFoundException {
    if (!eof) {
      try {
        String line = raf.readLine2();
        String[] entry = line.split("\t");
        double time = Double.parseDouble(entry[0]);
        int[] positions = null;
        if (entry.length > 1) {
          String[] sPositions = entry[1].split(",");
          positions = new int[sPositions.length];
          for (int i = 0; i < positions.length; i++) {
            positions[i] = Integer.parseInt(sPositions[i]);
          }
        }
        currentFrame++;
        return new Frame(time, positions);
      } catch (EOFException e) {
        eof = true;
        close();
      }
    } else if (trajectories.size() > 0) {
      init(trajectories.remove(0));
      eof = false;
      return readFrame();
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
    raf.close();
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
