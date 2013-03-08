package us.palpant.science.kmc.plugins;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import us.palpant.science.kmc.State;
import us.palpant.science.kmc.config.Ark;
import us.palpant.science.kmc.geometry.Lattice;
import us.palpant.science.kmc.geometry.Lattice.Coordinate;

public class Distribution extends Plugin {
  
  private static final Logger log = Logger.getLogger(Distribution.class);
  
  private final State state;
  private final Path outputFile;
  private double lastTime = 0;
  private Lattice lastState;
  private double[][][] dist;
  
  public Distribution(Lattice lattice, Ark config) {
    super(lattice);
    lastState = new Lattice(lattice.sizeX(), lattice.sizeY(), lattice.sizeZ());
    dist = new double[lattice.sizeX()][lattice.sizeY()][lattice.sizeZ()];
    state = State.forName((String)config.get("state"));
    outputFile = Paths.get((String)config.get("name"));
  }

  @Override
  public void process(double time) {
    double dt = time - lastTime;
    for (Coordinate c : lattice) {
      if (lastState.get(c) == state) {
        dist[c.getX()][c.getY()][c.getZ()] += dt;
      }
      lastState.set(c, lattice.get(c));
    }
    
    lastTime = time;
  }

  @Override
  public void close() throws IOException {
    log.debug("Writing distribution to output file: "+outputFile);
    try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile, Charset.defaultCharset()))) {
      for (Coordinate c : lastState) {
        writer.println(c+"\t"+dist[c.getX()][c.getY()][c.getZ()]/lastTime);
      }
    }
  }

}
