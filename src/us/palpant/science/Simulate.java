package us.palpant.science;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import us.palpant.science.transitions.Transition;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Command-line interface to the kinetic nucleosome simulator
 * @author timpalpant
 *
 */
public class Simulate {
	
	private static final Logger log = Logger.getLogger(Simulate.class);

	@Parameter(names = {"-i", "--potential"}, description = "Input file with potential energy", converter = PathConverter.class, validateWith = ReadablePathValidator.class)
	public Path potentialEnergyFile;
	@Parameter(names = {"-l", "--length"}, description = "Length of lattice to simulate, if potential is not provided")
	public int latticeLength = 5000;
	@Parameter(names = {"--veff"}, description = "If potential is not provided, initialize flat potential with this value")
	public double vEff = 0;
	@Parameter(names = {"-t", "--time"}, description = "Length of time to simulate", required = true)
	public double tFinal;
	@Parameter(names = "--periodic", description = "Use periodic boundary conditions")
	public boolean periodic = false;
	@Parameter(names = {"--seed"}, description = "Seed for random number generator")
	public long seed = 123456789;
	@Parameter(names = {"-o", "--output"}, description = "Output file with Lattice configuration at each timestep", converter = PathConverter.class, required = true)
	public Path outputFile;
	
	private double[] loadPotential() throws IOException {
		double[] potential = null;
		if (potentialEnergyFile != null) {
			log.info("Loading potential energy landscape from file: "+potentialEnergyFile);
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
			log.info("Initializing flat potential energy landscape with value = "+vEff);
			potential = new double[latticeLength];
			Arrays.fill(potential, vEff);
		}
		
		return potential;
	}
	
	public BoundaryCondition getBoundaryCondition() {
		return periodic ? BoundaryCondition.PERIODIC : BoundaryCondition.FIXED;
	}
	
	public Lattice initLattice() throws IOException {
		log.info("Using "+getBoundaryCondition().toString()+" boundary conditions");
		return new Lattice(loadPotential(), getBoundaryCondition());
	}
	
	public TransitionManager initTransitionManager(Lattice lattice) {
		return new RemodelerTransitionManager(lattice);
	}
	
	/**
	 * The main loop for the simulation
	 * Simulates up to tFinal, optionally collecting statistics
	 * throughout the simulation
	 * @throws IOException 
	 */
	private void run() throws IOException {
		Lattice lattice = initLattice();
		TransitionManager manager = initTransitionManager(lattice);
		Random rng = new Random(seed);
		double t = 0;
		
		log.info("Beginning simulation");
		double u1, u2, tao;
		Collection<Transition> allTransitions = manager.getAllTransitions();
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile, Charset.defaultCharset()))) {
			while (t < tFinal) {
				log.debug("Time t = "+t+", lattice has "+lattice.numObjects()+" objects");
				writer.println(t+"\t"+StringUtils.join(lattice.getAllPositions(), ","));
				
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
			}
		}
		
		log.info("Simulation complete");
	}
	
	/**
	 * Parse command-line arguments and run the tool
	 * Exit on parameter exceptions
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Simulate app = new Simulate();
		// Initialize the command-line options parser
		JCommander jc = new JCommander(app);
		jc.setProgramName("kineticNucleosomes");

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
