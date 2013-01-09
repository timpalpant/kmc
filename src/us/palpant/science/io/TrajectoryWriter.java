package us.palpant.science.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import us.palpant.science.io.FrameProto.Frame;

/**
 * Write trajectory files, which are series of Frames
 * @author timpalpant
 *
 */
public class TrajectoryWriter implements Closeable {
  private final OutputStream os;
  
  public TrajectoryWriter(OutputStream os) {
    this.os = os;
  }
  
  public TrajectoryWriter(Path p) throws IOException {
    this(Files.newOutputStream(p));
  }
  
  public void writeFrame(Frame frame) throws IOException {
    // Write the size of the message, then the message itself
    frame.writeTo(os);
  }
  
  public void writeFrame(double time, Collection<? extends Integer> positions) throws IOException {
    Frame frame = Frame.newBuilder().setTime(time).addAllPositions(positions).build();
    writeFrame(frame);
  }

  @Override
  public void close() throws IOException {
    os.close();
  }
}
