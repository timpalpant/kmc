package us.palpant.science.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Write trajectory files, which are series of Frames
 * @author timpalpant
 *
 */
public class AsciiTrajectoryWriter implements TrajectoryWriter {
  private final PrintWriter writer;
  private int numFrames = 0;
  
  public AsciiTrajectoryWriter(Path p) throws IOException {
    writer = new PrintWriter(Files.newBufferedWriter(p, Charset.defaultCharset()));
  }
  
  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryWriter#writeFrame(us.palpant.science.io.Frame)
   */
  @Override
  public void writeFrame(Frame frame) throws IOException {
    writer.println(frame);
    numFrames++;
  }
  
  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryWriter#writeFrame(double, int[])
   */
  @Override
  public void writeFrame(double time, int[] positions) throws IOException {
    writeFrame(new Frame(time, positions));
  }

  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryWriter#close()
   */
  @Override
  public void close() throws IOException {
    writer.close();
  }
  
  
  /* (non-Javadoc)
   * @see us.palpant.science.io.TrajectoryWriter#getNumFrames()
   */
  @Override
  public int getNumFrames() {
    return numFrames;
  }
}
