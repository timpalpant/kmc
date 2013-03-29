package us.palpant.science.kmc.plugins;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import us.palpant.Ark;
import us.palpant.science.kmc.State;
import us.palpant.science.kmc.geometry.Lattice;

/**
 * Calculate the number of objects of a certain state on the Lattice
 * 
 * @author timpalpant
 *
 */
public class NObjects extends Plugin {
  
  private static final Logger log = Logger.getLogger(NObjects.class);
  
  private final State state;
  private final Path outputFile;
  private final PrintWriter writer;
  
  public NObjects(Lattice lattice, Ark config) throws IOException {
    super(lattice);
    state = State.forName((String)config.get("state"));
    outputFile = Paths.get((String)config.get("name"));
    log.debug("Opening nobjects output: "+outputFile);
    writer = new PrintWriter(Files.newBufferedWriter(outputFile, Charset.defaultCharset()));
  }
  
  @Override
  public void process(double time) {
    writer.println(time+"\t"+lattice.count(state));
  }

  @Override
  public void close() throws IOException {
    log.debug("Closing output file: "+outputFile);
    writer.close();
  }

}
