package us.palpant.science.kmc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import us.palpant.Ark;
import us.palpant.ArkException;
import us.palpant.science.kmc.geometry.Lattice;

/**
 * A particle on the lattice with a certain characteristic width,
 * that can adsorb, desorb, diffuse, grow, shrink, and hop
 * @author timpalpant
 *
 */
public class Particle {
  
  private static final Logger log = Logger.getLogger(Particle.class);

  private final Lattice lattice;
  private final State state;
  private final Type type;
  private final int size;
  private List<Transition> transitions = new ArrayList<>();

  public Particle(Lattice lattice, State state, Type type, int size) {
    this.lattice = lattice;
    this.state = state;
    this.type = type;
    this.size = size;
  }

  public Particle(Lattice lattice, State state, Ark config) {
    this.lattice = lattice;
    this.state = state;
    size = Integer.parseInt((String) config.get("size"));
    type = Type.forName((String) config.get("type"));
    if (type == Type.DYNAMIC_WIDTH) {
      double rate = Double.parseDouble((String)config.get("rate"));
      transitions.addAll(getSizeTransitions(rate));
    }
    
    String[] tNames = (String[]) config.get("transitions");
    for (String tName : tNames) {
      Ark tConfig = (Ark) config.get(tName);
      String tType = (String) tConfig.get("type");
      double rate = Double.parseDouble((String)tConfig.get("rate"));
      switch (tType) {
      case "adsorption":
        transitions.addAll(getAdsorptionTransitions(rate));
        break;
      case "desorption":
        transitions.addAll(getDesorptionTransitions(rate));
        break;
      case "diffusion":
        transitions.addAll(getDiffusionTransitions(rate));
        break;
      case "hop":
        int step = Integer.parseInt((String)tConfig.get("step"));
        transitions.addAll(getHopTransitions(rate, step));
        break;
      default:
        throw new ArkException("Unknown particle transition type: "+tType);
      }
    }
    
    log.debug("Initialized particle "+state+" with "+transitions.size()+" transitions");
  }

  public List<Transition> getTransitions() {
    return Collections.unmodifiableList(transitions);
  }
  
  /**
   * @return the state
   */
  public State getState() {
    return state;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * @return the size
   */
  public int getSize() {
    return size;
  }

  @Override
  public String toString() {
    return state.toString();
  }
  
  public List<Transition> getSizeTransitions(double rate) {
    throw new RuntimeException("Dynamic width objects are not yet implemented");
  }
  
  public List<Transition> getAdsorptionTransitions(double rate) {
    List<Transition> transitions = new ArrayList<>();

    for (int i = 0; i < lattice.size()-size; i++) {
      List<Condition> conditionsList = new ArrayList<>();
      for (int j = 0; j < size; j++) {
        conditionsList.add(new Condition(i+j, State.EMPTY));
      }
      Condition[] conditions = conditionsList.toArray(new Condition[conditionsList.size()]);
      Action[] actions = new Action[size];
      actions[0] = new Action(i, state);
      for (int j = 1; j < size; j++) {
          actions[j] = new Action(i+j, State.STERIC);
      }
      transitions.add(new Transition("adsorption", conditions, actions, rate));
    }
    
    return transitions;
  }

  public List<Transition> getDesorptionTransitions(double rate) {
    List<Transition> transitions = new ArrayList<>();

    for (int i = 0; i < lattice.size()-size; i++) {
      Condition[] conditions = new Condition[1];
      conditions[0] = new Condition(i, state);
      Action[] actions = new Action[size];
      for (int j = 0; j < size; j++) {
          actions[j] = new Action(i+j, State.EMPTY);
      }
      transitions.add(new Transition("desorption", conditions, actions, rate));
    }
    
    return transitions;
  }

  public List<Transition> getDiffusionTransitions(double rate) {
    List<Transition> transitions = new ArrayList<>();

    // Diffusion to the right
    for (int i = 0; i < lattice.size()-size-1; i++) {
      Condition[] conditions = new Condition[2];
      conditions[0] = new Condition(i, state);
      conditions[1] = new Condition(i+size+1, State.EMPTY);
      Action[] actions = new Action[3];
      actions[0] = new Action(i, State.EMPTY);
      actions[1] = new Action(i+1, state);
      actions[2] = new Action(i+size, State.STERIC);
      transitions.add(new Transition("diffusion", conditions, actions, rate));
    }
    
    // Diffusion to the left
    for (int i = 1; i < lattice.size()-size; i++) {
      Condition[] conditions = new Condition[2];
      conditions[0] = new Condition(i, state);
      conditions[1] = new Condition(i-1, State.EMPTY);
      Action[] actions = new Action[3];
      actions[0] = new Action(i, State.STERIC);
      actions[1] = new Action(i-1, state);
      actions[2] = new Action(i+size-1, State.EMPTY);
      transitions.add(new Transition("diffusion", conditions, actions, rate));
    }
    
    return transitions;
  }
  
  public List<Transition> getHopTransitions(double rate, int step) {
    List<Transition> transitions = new ArrayList<>();
    
    return transitions;
  }

  public enum Type {
    FIXED_WIDTH("fixedWidth"), 
    DYNAMIC_WIDTH("dynamicWidth");

    private final String name;

    private Type(String name) {
      this.name = name;
    }

    public static Type forName(String name) {
      for (Type t : Type.values()) {
        if (t.getName().equals(name)) {
          return t;
        }
      }

      return null;
    }

    public String getName() {
      return name;
    }
  }

}
