package us.palpant.science.kmc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import us.palpant.science.kmc.config.Ark;
import us.palpant.science.kmc.config.ArkException;
import us.palpant.science.kmc.geometry.Lattice;
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
    String[] dims = (String[]) config.get("geometry.dimensions");
    Lattice lattice;
    switch (dims.length) {
    case 1:
      lattice = new Lattice(Integer.parseInt(dims[0]));
      break;
    case 2:
      lattice = new Lattice(Integer.parseInt(dims[0]), 
                            Integer.parseInt(dims[1]));
      break;
    case 3:
      lattice = new Lattice(Integer.parseInt(dims[0]), 
                            Integer.parseInt(dims[1]), 
                            Integer.parseInt(dims[2]));
      break;
    default:
      throw new RuntimeException("Lattice only supports 1, 2, or 3 dimensions");
    }
    
    lattice.fill(State.EMPTY);
    return lattice;
  }
  
  private Ark getStates() {
	  return (Ark) config.get("states");
	}
  
  public Transition[] initTransitions(Lattice lattice) {
    log.info("Initializing the transitions");
    List<Transition> transitions = new ArrayList<>();
    for (Entry<String,Object> keypair : getStates()) {
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
    Main app = new Main(config);
    app.run();
  }

}
