package us.palpant.science.kmc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import us.palpant.Ark;
import us.palpant.ArkException;
import us.palpant.science.kmc.geometry.Lattice;
import us.palpant.science.kmc.geometry.Lattice.BoundaryCondition;
import us.palpant.science.kmc.plugins.Plugin;

/**
 * Main application
 * 
 * @author timpalpant
 * 
 */
public class Main {

  private static final Logger log = Logger.getLogger(Main.class);
  
  private final Ark config;

  public Main(Ark config) {
    this.config = config;
    log.debug("Configuration:\n" + config);
  }
  
  public Lattice initLattice() {
    int length = Integer.parseInt((String)config.get("lattice.length"));
    BoundaryCondition bc = BoundaryCondition.forName((String)config.get("lattice.bc"));
    Lattice lattice = new Lattice(length, bc);
    lattice.fill(State.EMPTY);
    return lattice;
  }
  
  private Ark getParticles() {
	  return (Ark) config.get("particles");
	}
  
  public Transition[] initTransitions(Lattice lattice) {
    log.info("Initializing the transitions");
    List<Transition> transitions = new ArrayList<>();
    for (Entry<String,Object> keypair : getParticles()) {
      State state = State.forName(keypair.getKey());
      Ark stateConfig = (Ark) keypair.getValue();
      Particle particle = new Particle(lattice, state, stateConfig);
      transitions.addAll(particle.getTransitions());
    }
    
    return transitions.toArray(new Transition[transitions.size()]);
  }
  
  public List<Plugin> initPlugins(Lattice lattice) throws IOException {
    log.info("Initializing plugins");
    Ark app = getApp();
    String[] pluginNames = (String[]) app.get("plugins");
    List<Plugin> plugins = new ArrayList<>();
    for (String name : pluginNames) {
      Ark pluginCfg = (Ark) app.get(name);
      Plugin p = Plugin.forConfig(lattice, pluginCfg);
      plugins.add(p);
    }
    
    return plugins;
  }
  
  public Ark getApp() {
    if (!config.has("app")) {
      throw new ArkException("app is not specified!");
    }
    String appName = (String) config.get("app");
    if (!config.has(appName)) {
      throw new ArkException("app "+appName+" does not have any configuration!");
    }
    return (Ark) config.get(appName);
  }
  
  public KineticMonteCarlo initApp(TransitionManager manager, List<Plugin> plugins) {
    KineticMonteCarlo kmc = new KineticMonteCarlo(manager, plugins);
    
    Ark app = getApp();
    if (app.has("seed")) {
      long seed = Long.parseLong((String)app.get("seed"));
      kmc.setSeed(seed);
    }
    if (app.has("last_time")) {
      double lastTime = Double.parseDouble((String)app.get("last_time"));
      kmc.setTFinal(lastTime);
    }
    
    return kmc;
  }
  
  public void run() throws IOException {
    Lattice lattice = initLattice();
    Transition[] transitions = initTransitions(lattice);
    TransitionManager manager = new TransitionManager(lattice, transitions);
    List<Plugin> plugins = initPlugins(lattice);
    KineticMonteCarlo kmc = initApp(manager, plugins);
    
    kmc.run();
    
    for (Plugin p : plugins) {
      p.close();
    }
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.err.println("USAGE: us.palpant.science.kmc.Main [--include config.ark] [--cfg PARAM=VALUE]");
      System.exit(2);
    }
    
    log.info("Loading configuration");
    Ark config = Ark.fromArgv(args);
    log.debug("Initializing application");
    Main app = new Main(config);
    app.run();
  }

}
