package us.palpant.science.kmc;

import org.apache.log4j.Logger;

public class Transition {
  
  private static final Logger log = Logger.getLogger(Transition.class);

  private final String name;
  private final Condition[] conditions;
  private final Action[] actions;
  private final int[] upstreamCoordinates;
  private final int[] downstreamCoordinates;
  private double rate;
  private boolean enabled = true;
  
  public Transition(String name, Condition[] conditions, Action[] actions, double rate) {
    this.name = name;
    this.conditions = conditions;
    this.actions = actions;
    this.rate = rate;
    
    upstreamCoordinates = Contingency.getCoordinates(conditions);
    if (upstreamCoordinates.length != conditions.length) {
      log.warn("Multiple conditions on the same coordinate detected: "+toString());
    }
    downstreamCoordinates = Contingency.getCoordinates(actions);
    if (upstreamCoordinates.length != conditions.length) {
      log.warn("Multiple actions on the same coordinate detected: "+toString());
    }
  }
  
  public Transition(Condition[] conditions, Action[] actions, double rate) {
    this(null, conditions, actions, rate);
  }
  
  public final Condition[] getConditions() {
    return conditions;
  }
  
  public final int[] getUpstreamCoordinates() {
    return upstreamCoordinates;
  }

  public final Action[] getActions() {
    return actions;
  }

  public final int[] getDownstreamCoordinates() {
    return downstreamCoordinates;
  }

  public final double getRate() {
    if (!enabled) {
      return 0;
    }
    return rate;
  }
  
  public final void setRate(double rate) {
    this.rate = rate;
  }
  
  public final boolean isEnabled() {
    return enabled;
  }
  
  public final void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  public final String getName() {
    return name;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(name);
    sb.append(" conditions: ");
    for (Condition c : conditions) {
      sb.append(c).append(", ");
    }
    sb.append("actions: ");
    for (Action a : actions) {
      sb.append(a).append(", ");
    }
    sb.append("rate = ").append(getRate());
    return sb.toString();
  }

}
