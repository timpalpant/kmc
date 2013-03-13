package us.palpant.science.kmc.plugins;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import us.palpant.Ark;
import us.palpant.science.kmc.geometry.Lattice;

/**
 * Write a trajectory of the lattice state at each time point
 * @author timpalpant
 *
 */
public class Trajectory extends Plugin {
  
  private static final Logger log = Logger.getLogger(Trajectory.class);

  private final PrintWriter writer;
  private final long first;
  private final long stride;
  
  private long currentStep = 0; 
  
  public Trajectory(Lattice lattice, Ark config) throws IOException {
    super(lattice);
    String name = (String) config.get("name");
    Path outputFile = Paths.get(name);
    log.info("Initializing trajectory output: "+outputFile);
    writer = new PrintWriter(Files.newBufferedWriter(outputFile, Charset.defaultCharset()));

    if (config.has("first")) {
      first = Integer.parseInt((String) config.get("first"));
    } else {
      first = 0;
    }
    
    if (config.has("stride")) {
      stride = Integer.parseInt((String) config.get("stride"));
    } else {
      stride = 1;
    }
  }

  @Override
  public void process(double time) {
    if (((currentStep++) - first) % stride == 0) {
      writer.println(time+"\t"+lattice);
    }
  }

  @Override
  public void close() throws IOException {
    log.debug("Closing trajectory plugin");
    writer.close();
  }

}
