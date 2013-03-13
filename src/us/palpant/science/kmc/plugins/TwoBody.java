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
 * Calculate the two-body distribution of a certain state
 * throughout the trajectory
 * 
 * @author timpalpant
 *
 */
public class TwoBody extends Plugin {
  
  private static final Logger log = Logger.getLogger(TwoBody.class);
  
  private final State state;
  private final Path outputFile;
  private double lastTime = 0;
  private Lattice lastState;
  private double[] dist;
  
  public TwoBody(Lattice lattice, Ark config) {
    super(lattice);
    lastState = new Lattice(lattice.size(), lattice.getBoundaryCondition());
    dist = new double[lattice.size()];
    state = State.forName((String)config.get("state"));
    outputFile = Paths.get((String)config.get("name"));
  }

  @Override
  public void process(double time) {
    double dt = time - lastTime;
    int first = -1;
    for (int i = 0; i < lastState.size(); i++) {
      if (lastState.get(i) == state) {
        if (first == -1) {
          first = i;
        } else {
          dist[i-first] += dt;
        }
      }
      lastState.set(i, lattice.get(i));
    }
    
    lastTime = time;
  }

  @Override
  public void close() throws IOException {
    log.debug("Writing distribution to output file: "+outputFile);
    try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile, Charset.defaultCharset()))) {
      for (int i = 0; i < dist.length; i++) {
        writer.println(i+"\t"+dist[i]/lastTime);
      }
    }
  }

}
