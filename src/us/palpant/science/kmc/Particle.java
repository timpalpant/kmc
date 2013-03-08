package us.palpant.science.kmc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import us.palpant.science.kmc.config.Ark;
import us.palpant.science.kmc.config.ArkException;
import us.palpant.science.kmc.geometry.Lattice;

public class Particle {
  
  private static final Logger log = Logger.getLogger(Particle.class);

  private final State state;
  private final Type type;
  private final int size;
  private List<Transition> transitions = new ArrayList<>();

  public Particle(State state, Type type, int size) {
    this.state = state;
    this.type = type;
    this.size = size;
  }

  public Particle(Lattice lattice, State state, Ark config) {
    this.state = state;
    size = Integer.parseInt((String) config.get("size"));
    type = Type.forName((String) config.get("type"));
    if (type == Type.DYNAMIC_WIDTH) {
      transitions.addAll(getSizeTransitions(lattice, state, size, 0));
    }
    
    String[] tNames = (String[]) config.get("transitions");
    for (String tName : tNames) {
      Ark tConfig = (Ark) config.get(tName);
      String tType = (String) tConfig.get("type");
      double rate = Double.parseDouble((String)tConfig.get("rate"));
      switch (tType) {
      case "adsorption":
        transitions.addAll(getAdsorptionTransitions(lattice, state, size, rate));
        break;
      case "desorption":
        transitions.addAll(getDesorptionTransitions(lattice, state, size, rate));
        break;
      case "diffusion":
        transitions.addAll(getDiffusionTransitions(lattice, state, size, rate));
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
  
  public static List<Transition> getSizeTransitions(Lattice lattice, State state, int size, double rate) {
    List<Transition> transitions = new ArrayList<>();
    
    return transitions;
  }
  
  public static List<Transition> getAdsorptionTransitions(Lattice lattice, State state, int size, double rate) {
    List<Transition> transitions = new ArrayList<>();

    for (int i = 0; i < lattice.sizeX()-size; i++) {
      List<Condition> conditionsList = new ArrayList<>();
      for (int j = 0; j < size; j++) {
        conditionsList.add(new Condition(lattice.coordinate(i+j), State.EMPTY));
      }
      Condition[] conditions = conditionsList.toArray(new Condition[conditionsList.size()]);
      Action[] actions = new Action[size];
      actions[0] = new Action(lattice.coordinate(i), state);
      for (int j = 1; j < size; j++) {
          actions[j] = new Action(lattice.coordinate(i+j), State.STERIC);
      }
      transitions.add(new Transition("adsorption", conditions, actions, rate));
    }
    
    return transitions;
  }

  public static List<Transition> getDesorptionTransitions(Lattice lattice, State state, int size, double rate) {
    List<Transition> transitions = new ArrayList<>();

    for (int i = 0; i < lattice.sizeX()-size; i++) {
      Condition[] conditions = new Condition[1];
      conditions[0] = new Condition(lattice.coordinate(i), state);
      Action[] actions = new Action[size];
      for (int j = 0; j < size; j++) {
          actions[j] = new Action(lattice.coordinate(i+j), State.EMPTY);
      }
      transitions.add(new Transition("desorption", conditions, actions, rate));
    }
    
    return transitions;
  }

  public static List<Transition> getDiffusionTransitions(Lattice lattice, State state, int size, double rate) {
    List<Transition> transitions = new ArrayList<>();

    // Diffusion to the right
    for (int i = 0; i < lattice.sizeX()-size-1; i++) {
      Condition[] conditions = new Condition[2];
      conditions[0] = new Condition(lattice.coordinate(i), state);
      conditions[1] = new Condition(lattice.coordinate(i+size+1), State.EMPTY);
      Action[] actions = new Action[3];
      actions[0] = new Action(lattice.coordinate(i), State.EMPTY);
      actions[1] = new Action(lattice.coordinate(i+1), state);
      actions[2] = new Action(lattice.coordinate(i+size), State.STERIC);
      transitions.add(new Transition("diffusion", conditions, actions, rate));
    }
    
    // Diffusion to the left
    for (int i = 1; i < lattice.sizeX()-size; i++) {
      Condition[] conditions = new Condition[2];
      conditions[0] = new Condition(lattice.coordinate(i), state);
      conditions[1] = new Condition(lattice.coordinate(i-1), State.EMPTY);
      Action[] actions = new Action[3];
      actions[0] = new Action(lattice.coordinate(i), State.STERIC);
      actions[1] = new Action(lattice.coordinate(i-1), state);
      actions[2] = new Action(lattice.coordinate(i+size-1), State.EMPTY);
      transitions.add(new Transition("diffusion", conditions, actions, rate));
    }
    
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
