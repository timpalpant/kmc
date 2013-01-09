package us.palpant.science.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import us.palpant.science.io.FrameProto.Frame;

/**
 * Read Trajectory files, which are series of Frames
 * @author timpalpant
 *
 */
public class TrajectoryReader implements Iterable<Frame> {
  private final InputStream is;
  
  public TrajectoryReader(InputStream is) {
    this.is = is;
  }
  
  public TrajectoryReader(Path p) throws IOException {
    this(Files.newInputStream(p));
  }

  @Override
  public Iterator<Frame> iterator() {
    // TODO Auto-generated method stub
    return null;
  }
}
