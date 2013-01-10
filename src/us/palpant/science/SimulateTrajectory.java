package us.palpant.science;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import us.palpant.science.io.TrajectoryWriter;
import us.palpant.science.transitions.Transition;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Command-line interface to the kinetic nucleosome simulator
 * 
 * @author timpalpant
 * 
 */
public class SimulateTrajectory {

  private static final Logger log = Logger.getLogger(SimulateTrajectory.class);

  @Parameter(names = { "-i", "--potential" }, description = "Input file with potential energy landscape", 
             converter = PathConverter.class, validateWith = ReadablePathValidator.class)
  private Path potentialEnergyFile;
  @Parameter(names = { "-l", "--length" }, description = "Length of lattice to simulate, if potential is not provided")
  private int latticeLength = 5000;
  @Parameter(names = { "--veff" }, description = "If potential is not provided, initialize flat potential with this value")
  private double vEff = 0;
  @Parameter(names = { "-t", "--time" }, description = "Length of time to simulate (min)", required = true)
  private double tFinal;
  @Parameter(names = {"-p", "--periodic"}, description = "Use periodic boundary conditions")
  private boolean periodic = false;
  @Parameter(names = { "--seed" }, description = "Seed for random number generator")
  private long seed = 123456789;
  @Parameter(names = { "-o", "--output" }, description = "Output trajectory with Lattice configuration at each timestep", 
             converter = PathConverter.class, required = true)
  private Path outputFile;

  /**
   * Simulation parameters
   */
  private final Parameters params;
  public static final int NUM_CHECKPOINTS = 10;
  
  private SimulateTrajectory(Parameters params) {
	  this.params = params;
  }
  
  private double[] loadPotential() throws IOException {
    double[] potential = null;
    if (potentialEnergyFile != null) {
      log.info("Loading potential energy landscape from file: " + potentialEnergyFile);
      List<Double> v = new ArrayList<Double>();
      try (BufferedReader reader = Files.newBufferedReader(potentialEnergyFile, Charset.defaultCharset())) {
        String line;
        while ((line = reader.readLine()) != null) {
          v.add(Double.parseDouble(line));
        }
      }
      potential = new double[v.size()];
      for (int i = 0; i < v.size(); i++) {
        potential[i] = v.get(i);
      }
    } else {
      log.info("Creating flat potential energy landscape with length = "+latticeLength+" and value = "+vEff);
      potential = new double[latticeLength];
      Arrays.fill(potential, vEff * params.getBeta());
    }

    return potential;
  }

  public Lattice.BoundaryCondition getBoundaryCondition() {
    return periodic ? Lattice.BoundaryCondition.PERIODIC : Lattice.BoundaryCondition.FIXED;
  }

  private Lattice initLattice() throws IOException {
    log.info("Using " + getBoundaryCondition().toString() + " boundary conditions");
    return new Lattice(loadPotential(), getBoundaryCondition());
  }

  private TransitionManager initTransitionManager(Lattice lattice) {
    return new RemodelerTransitionManager(lattice, params);
  }

  /**
   * The main loop for the simulation Simulates up to tFinal, optionally
   * collecting statistics throughout the simulation
   * 
   * @throws IOException
   */
  private void run() throws IOException {
    log.info("Simulation parameters: " + params.toString());
    Lattice lattice = initLattice();
    TransitionManager manager = initTransitionManager(lattice);
    log.info("Seed = " + seed);
    Random rng = new Random(seed);
    double t = 0;

    log.info("Beginning simulation");
    double u1, u2, tao;
    Collection<Transition> allTransitions = manager.getAllTransitions();
    double nextCheckpoint = tFinal / NUM_CHECKPOINTS;
    try (TrajectoryWriter writer = new TrajectoryWriter(outputFile, lattice.size())) {
      while (t < tFinal) {
        log.debug("Time t = " + t + ", lattice has " + lattice.numObjects() + " objects");
        writer.writeFrame(t, lattice.getAllPositions());

        // Randomly choose a transition to carry out
        u1 = rng.nextDouble();
        Transition transition = TransitionManager.getTransition(allTransitions, u1);

        // Carry out the event
        transition.perform();

        // Update the time
        u2 = rng.nextDouble();
        allTransitions = manager.getAllTransitions();
        tao = -Math.log(u2) / TransitionManager.getRateTotal(allTransitions);
        t += tao;
        
        if (t > nextCheckpoint) {
          log.info(String.format("%2.0f", 100*t/tFinal) + "% complete");
          nextCheckpoint += tFinal / NUM_CHECKPOINTS;
        }
      }
    }

    log.info("Simulation complete");
  }

  /**
   * Parse command-line arguments and run the tool Exit on parameter exceptions
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
	Parameters params = new Parameters();
    SimulateTrajectory app = new SimulateTrajectory(params);
    // Initialize the command-line options parser
    JCommander jc = new JCommander(new Object[] {app, params});
    jc.setProgramName("SimulateTrajectory");

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
