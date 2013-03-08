package us.palpant.science.kmc;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import us.palpant.science.kmc.plugins.Plugin;

public class KineticMonteCarlo {
  
  private static final Logger log = Logger.getLogger(KineticMonteCarlo.class);
  public static final int PROGRESS = 100_000;
  
  private final TransitionManager manager;
  private final List<Plugin> plugins;
  private double t, tFinal;
  
  private Random rng = new Random();
  
  public KineticMonteCarlo(TransitionManager manager, List<Plugin> plugins) {
    this.manager = manager;
    this.plugins = plugins;
  }
  
  public void run() {
    log.info("Beginning simulation");
    while (t < tFinal) {
      for (Plugin p : plugins) {
        p.process(t);
      }
      
      double r1 = rng.nextDouble();
      Transition transition = manager.getTransition(r1);
      manager.perform(transition);
      
      double r2 = rng.nextDouble();
      double dt = -Math.log(r2) / manager.getKTotal();
      t += dt;
    }
    log.info("Simulation complete");
  }

  public final double getTFinal() {
    return tFinal;
  }

  public final void setTFinal(double tFinal) {
    log.info("Setting tFinal = "+tFinal);
    this.tFinal = tFinal;
  }
  
  public final void setSeed(long seed) {
    log.info("Setting seed for RNG = "+seed);
    rng.setSeed(seed);
  }
  
}
