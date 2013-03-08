package us.palpant.science.statmech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import us.palpant.cmd.PathConverter;
import us.palpant.cmd.ReadablePathValidator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Command-line interface to the kinetic nucleosome simulator
 * 
 * @author timpalpant
 * 
 */
public class DynaPro {

  private static final Logger log = Logger.getLogger(DynaPro.class);

  @Parameter(names = { "-i", "--potential" }, description = "Input file with potential energy landscape", 
             converter = PathConverter.class, validateWith = ReadablePathValidator.class)
  private Path potentialEnergyFile;
  @Parameter(names = { "-l", "--length" }, description = "Length of lattice to simulate, if potential is not provided")
  private int latticeLength = 5000;
  @Parameter(names = { "-v", "--veff" }, description = "If potential is not provided, initialize flat potential with this value")
  private double vEff = 0;
  @Parameter(names = { "--nuc-size" }, description = "Nucleosome size (bp)")
  private int nucSize = 147;
  @Parameter(names = { "-o", "--output" }, description = "Output trajectory with Lattice configuration at each timestep", 
             converter = PathConverter.class, required = true)
  private Path outputFile;
  
  private DynaPro() { }
  
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
      double total = 0;
      for (int i = 0; i < v.size(); i++) {
        potential[i] = v.get(i);
        total += potential[i];
      }
      log.info("Mean of potential landscape = "+total/potential.length);
    } else {
      log.info("Creating flat potential energy landscape with length = "+latticeLength+" and value = "+vEff);
      potential = new double[latticeLength];
      Arrays.fill(potential, vEff);
    }

    return potential;
  }
  
  public static double[] dynapro(double[] potential, int n) {
    double[] forward = new double[potential.length];
    for (int i = n; i < potential.length; i++) {
      double factor = 1 + Math.exp(forward[i-n] - forward[i-1] - potential[i-n]);
      forward[i] = forward[i-1] + Math.log(factor);
    }
    
    double[] backward = new double[potential.length];
    for (int i = potential.length-n-1; i > 0; i--) {
      double factor = 1 + Math.exp(backward[i+n] - backward[i+1] - potential[i-1]);
      backward[i] = backward[i+1] + Math.log(factor);
    }
    
    double[] p = new double[potential.length];
    for (int i = 0; i < potential.length-n; i++) {
      p[i] = Math.exp(forward[i] - potential[i] + backward[i+n] - backward[1]);
    }
    
    return shift(p,n/2);
  }
  
  public static double[] shift(double[] a, int n) {   
    double[] shifted = new double[a.length];
    for (int i = 0; i < n; i++) {
      shifted[i] = a[a.length-n+i];
    }
    for (int i = n; i < shifted.length; i++) {
      shifted[i] = a[i-n];
    }
    return shifted;
  }

  /**
   * Calculate equilibrium nucleosome positions
   * according to DynaPro algorithm
   * 
   * @throws IOException
   */
  private void run() throws IOException {
    double[] potential = loadPotential();
    
    log.info("Computing equilibrium probability distribution");
    double[] p = dynapro(potential, nucSize);
    
    log.info("Writing equilibrium probability distribution to: "+outputFile);
    try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile, Charset.defaultCharset()))) {
      for (int i = 0; i < p.length; i++) {
        writer.println(i + "\t" + p[i]);
      }
    }
  }

  /**
   * Parse command-line arguments and run the tool Exit on parameter exceptions
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    DynaPro app = new DynaPro();
    // Initialize the command-line options parser
    JCommander jc = new JCommander(app);
    jc.setProgramName("DynaPro");

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
