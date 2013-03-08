package us.palpant.science.kmc.plugins;

import java.io.IOException;

import org.apache.log4j.Logger;

import us.palpant.science.kmc.config.Ark;
import us.palpant.science.kmc.geometry.Lattice;

public class Status extends Plugin {
  
  private static final Logger log = Logger.getLogger(Status.class);

  private final long first;
  private final long stride;
  
  private long currentStep = 0; 
  
  public Status(Lattice lattice, Ark config) throws IOException {
    super(lattice);
    
    if (config.has("first")) {
      first = Integer.parseInt((String) config.get("first"));
    } else {
      first = 0;
    }
    
    if (config.has("stride")) {
      stride = Integer.parseInt((String) config.get("stride"));
    } else {
      stride = 1;
    }
  }

  @Override
  public void process(double time) {
    currentStep++;
    if ((currentStep > first) && ((currentStep-first) % stride == 0)) {
      if (log.isInfoEnabled()) {
        log.info(time+" ("+currentStep+" steps)");
      }
    }
  }

  @Override
  public void close() throws IOException { }

}
