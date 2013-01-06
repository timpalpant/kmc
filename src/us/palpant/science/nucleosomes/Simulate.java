package us.palpant.science.nucleosomes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

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

	@Parameter(names = {"-l", "--length"}, description = "Length of the lattice to simulate")
	public int latticeLength = 10000;
	@Parameter(names = {"--potential"}, description = "Input file with potential energy")
	public String potentialEnergyFile;
	@Parameter(names = {"-t", "--time"}, description = "Length of time to simulate")
	public float tFinal = 10000;
	@Parameter(names = {"-p", "--positions"}, description = "Output file with position counts")
	public String positionsFile;
	@Parameter(names = {"-c", "--counts"}, description = "Output file with number of nucleosomes vs. time")
	public String nNucsFile;
	@Parameter(names = "--periodic", description = "Use periodic boundary conditions")
	public boolean periodic = false;
	
	private double[] loadPotential() throws IOException {
		double[] potential = null;
		if (potentialEnergyFile != null) {
			List<Double> v = new ArrayList<Double>();
			try (BufferedReader reader = Files.newBufferedReader(Paths.get(potentialEnergyFile), Charset.defaultCharset())) {
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
			potential = new double[latticeLength];
			Arrays.fill(potential, Parameters.V_EFF);
		}
		
		return potential;
	}
	
	/**
	 * The main loop for the simulation
	 * Simulates up to tFinal, optionally collecting statistics
	 * throughout the simulation
	 * @throws IOException 
	 */
	private void run() throws IOException {
		// Load the potential energy
		log.info("Loading potential energy");
		double[] potential = loadPotential();
		
		// Initialize the lattice
		log.info("Initializing the simulation lattice");
		BoundaryCondition bc = periodic ? BoundaryCondition.PERIODIC : BoundaryCondition.FIXED;
		Lattice lattice = new Lattice(potential, bc);
		TransitionManager manager = new RemodelerTransitionManager(lattice);
		Random rng = new Random();
		double t = 0;
		
		log.info("Beginning simulation");
		double u1, u2, tao;
		int nAdsorptions = 0, nDesorptions = 0;
		int n, prevN = 0;
		int[] counts = new int[potential.length];
		List<Double> times = new ArrayList<Double>();
		List<Integer> nobjects = new ArrayList<Integer>();
		while (t < tFinal) {
			log.debug("Time t = "+t+", lattice has "+lattice.numObjects()+" nucleosomes");
			// Collect stats
			for (LatticeObject object : lattice) {
				counts[lattice.getPosition(object)]++;
			}
			times.add(t);
			n = lattice.numObjects();
			nobjects.add(n);
			if (n > prevN) {
				nAdsorptions++;
			} else if (n < prevN) {
				nDesorptions++;
			}
			prevN = n;
			
			// Randomly choose a transition to carry out
			u1 = rng.nextDouble();
			Transition transition = manager.getTransition(u1);
					
			// Carry out the event
			transition.perform();

			// Update the time
			u2 = rng.nextDouble();
			tao = -Math.log(u2) / manager.getRateTotal();
			t += tao;
		}
		
		// Write the statistics to the output file
		if (positionsFile != null) {
			try (PrintWriter writer = new PrintWriter(positionsFile)) {
				for (int c : counts) {
					writer.println(c);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Plot of # nucleosomes vs. time
		if (counts != null) {
			try (PrintWriter writer = new PrintWriter(nNucsFile)) {
				for (int i = 0; i < nobjects.size(); i++) {
					writer.println(times.get(i)+"\t"+nobjects.get(i));
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("# Adsorptions:\t"+nAdsorptions+"\t("+nAdsorptions/tFinal/potential.length+" adsorptions/min/bp)");
		System.out.println("# Desorptions:\t"+nDesorptions+"\t("+nDesorptions/tFinal/potential.length+" adsorptions/min/bp)");
		
		// Histogram of the fraction of time in each # nucleosomes
		int maxNumObjects = Collections.max(nobjects);
		double[] hist = new double[maxNumObjects+1];
		for (int i = 0; i < nobjects.size()-1; i++) {
			double dt = times.get(i+1) - times.get(i);
			hist[nobjects.get(i)] += dt;
		}
		System.out.println("% of time spent with k objects:");
		double meanNumNucleosomes = 0;
		for (int i = 0; i <= maxNumObjects; i++) {
			double fraction = hist[i]/tFinal;
			meanNumNucleosomes += fraction*i;
			System.out.println(i+"\t"+fraction);
		}
		System.out.println("E[k]:\t"+meanNumNucleosomes);
		System.out.println("<p>:\t"+meanNumNucleosomes*Parameters.NUC_SIZE / potential.length);
		
		// Print the final state of the lattice
		boolean first = true;
		System.out.print("Final state:\t");
		for (LatticeObject object : lattice) {
			if (!first) {
				System.out.print(",");
			}
			System.out.print(lattice.getPosition(object));
			first = false;
		}
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
