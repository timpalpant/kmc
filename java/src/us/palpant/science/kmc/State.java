package us.palpant.science.kmc;

import java.util.HashMap;
import java.util.Map;

/**
 * A dynamic enumeration of States
 * 
 * @author palpant
 * 
 */
public class State {

  /**
   * All the states that have been created
   */
  private static final Map<String, State> states = new HashMap<>();
  private static int nStates = 0;
  public static final State EMPTY = State.forName("empty");
  public static final State STERIC = State.forName("steric");

  private final int id;
  private final String name;

  private State(String name) {
    id = nStates++;
    this.name = name;
  }

  /**
   * Factory method to get a State by name
   * 
   * @param name
   * @return
   */
  public static State forName(String name) {
    if (!states.containsKey(name)) {
      states.put(name, new State(name));
    }

    return states.get(name);
  }

  public static int getNumStates() {
    return nStates;
  }
  
  public int getId() {
    return id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof State) {
      return ((State) other).id == id;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    return name;
  }

}
