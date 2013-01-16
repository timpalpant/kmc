package us.palpant.science;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import us.palpant.science.io.AsciiTrajectoryWriter;
import us.palpant.science.io.Frame;
import us.palpant.science.io.BinaryTrajectoryReader;
import us.palpant.science.io.TrajectoryReader;
import us.palpant.science.io.TrajectoryWriter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class ConvertTrajectory {

  private static final Logger log = Logger.getLogger(ConvertTrajectory.class);
  
  @Parameter(names = { "-i", "--input" }, description = "Input trajectory", 
      required = true, converter = PathConverter.class, validateWith = ReadablePathValidator.class)
  public Path inputFile;
  @Parameter(names = { "-o", "--output" }, description = "Output ASCII trajectory", 
      required = true, converter = PathConverter.class)
  public Path outputFile;
  
  private void run() throws IOException, ClassNotFoundException {
    log.info("Opening trajectory");
    try (TrajectoryReader reader = new BinaryTrajectoryReader(inputFile);
         TrajectoryWriter writer = new AsciiTrajectoryWriter(outputFile)) {
      log.info("Translating...");
      Frame frame = null;
      while ((frame = reader.readFrame()) != null) {
        writer.writeFrame(frame);
      }
      log.info("Wrote "+writer.getNumFrames()+" frames");
    }
  }
  
  /**
   * @param args
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public static void main(String[] args) throws ClassNotFoundException, IOException {
    ConvertTrajectory app = new ConvertTrajectory();
    // Initialize the command-line options parser
    JCommander jc = new JCommander(app);
    jc.setProgramName("ConvertTrajectory");

    try {
      jc.parse(args);
    } catch (ParameterException e) {
      System.err.println(e.getMessage());
      jc.usage();
      System.exit(-1);
    }

    app.run();
  }

}
