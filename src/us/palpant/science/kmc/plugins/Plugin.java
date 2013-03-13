package us.palpant.science.kmc.plugins;

import java.io.Closeable;
import java.io.IOException;

import org.apache.log4j.Logger;

import us.palpant.Ark;
import us.palpant.science.kmc.geometry.Lattice;

/**
 * A plugin is called once per KMC time step
 * @author timpalpant
 *
 */
public abstract class Plugin implements Closeable {
  
  private static final Logger log = Logger.getLogger(Plugin.class);

  protected final Lattice lattice;
  
  protected Plugin(Lattice lattice) {
    this.lattice = lattice;
  }
  
  /**
   * Factory method that returns a new Plugin
   * @param config the Ark configuration for the Plugin
   * @return a new Plugin that has been initialized with config
   * @throws IOException 
   */
  public static Plugin forConfig(Lattice lattice, Ark config) throws IOException {
    String pluginType = (String) config.get("type");
    log.debug("Initializing plugin of type "+pluginType);
    
    Plugin p;
    switch(pluginType) {
    case "trajectory":
      p = new Trajectory(lattice, config);
      break;
    case "distribution":
      p = new Distribution(lattice, config);
      break;
    case "status":
      p = new Status(lattice, config);
      break;
    case "nobjects":
      p = new NObjects(lattice, config);
      break;
    case "twobody":
      p = new TwoBody(lattice, config);
      break;
    default:
      throw new RuntimeException("Unknown plugin type: "+pluginType);
    }

    return p;
  }
  
  /**
   * Apply the plugin
   * @param lattice
   */
  public abstract void process(double time);
  
}
