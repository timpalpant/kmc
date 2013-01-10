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

public class CountObjectsVsTime {

  private static final Logger log = Logger.getLogger(CountObjectsVsTime.class);
  
  @Parameter(names = { "-i", "--input" }, description = "Input trajectory", 
      required = true, converter = PathConverter.class, validateWith = ReadablePathValidator.class)
  public Path inputFile;
  @Parameter(names = { "-o", "--output" }, description = "Output file with number of objects at each time", 
      required = true, converter = PathConverter.class)
  public Path outputFile;
  
  private void run() throws IOException, ClassNotFoundException {
    log.info("Opening trajectory");
    int count = 0;
    try (TrajectoryReader reader = new TrajectoryReader(inputFile);
         PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile, Charset.defaultCharset()))) {
      Frame frame = null;
      while ((frame = reader.readFrame()) != null) {
        writer.println(frame.getTime()+"\t"+frame.getPositions().length);
        count++;
      } 
    }
    log.info("Processed "+count+" frames");
  }
  
  /**
   * @param args
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public static void main(String[] args) throws ClassNotFoundException, IOException {
    CountObjectsVsTime app = new CountObjectsVsTime();
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