package us.palpant.science;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import us.palpant.science.io.Frame;
import us.palpant.science.io.TrajectoryReader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class PDistribution {

  private static final Logger log = Logger.getLogger(PDistribution.class);

  @Parameter(names = { "-i", "--input" }, 
      description = "Input trajectory", required = true, 
      converter = PathConverter.class, validateWith = ReadablePathValidator.class)
  public Path inputFile;
  @Parameter(names = { "-o", "--output" }, 
      description = "Output file with probability of finding an object at each position", 
      required = true, converter = PathConverter.class)
  public Path outputFile;

  private void run() throws IOException, ClassNotFoundException {
    log.info("Opening trajectory");
    int numFrames = 0;
    double prevTime = 0, time = 0, dt;
    double[] counts = null;
    int[] positions = null;
    try (TrajectoryReader reader = new TrajectoryReader(inputFile)) {
      counts = new double[reader.getLatticeSize()];
      Frame frame = null;
      log.info("Accumulating counts");
      while ((frame = reader.readFrame()) != null) {
        time = frame.getTime();
        dt = time - prevTime;
        if (positions != null) {
          for (int p : positions) {
            counts[p] += dt;
          }
        }
        prevTime = time;
        positions = frame.getPositions();
        numFrames++;
      }
    }
    log.info("Processed " + numFrames + " frames");
    
    log.info("Writing probabilities to output");
    try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile, Charset.defaultCharset()))) {
      double p;
      for (int i = 0; i < counts.length; i++) {
        p = counts[i] / time;
        writer.println(i + "\t" + p);
      }
    }
  }

  /**
   * @param args
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public static void main(String[] args) throws ClassNotFoundException,
      IOException {
    PDistribution app = new PDistribution();
    // Initialize the command-line options parser
    JCommander jc = new JCommander(app);
    jc.setProgramName("CountObjectsVersusTime");

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
