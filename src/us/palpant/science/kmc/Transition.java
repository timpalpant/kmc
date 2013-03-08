package us.palpant.science.kmc;

import us.palpant.science.kmc.geometry.Lattice.Coordinate;

public class Transition {

  private final String name;
  private final Condition[] conditions;
  private final Action[] actions;
  private final Coordinate[] upstreamCoordinates;
  private final Coordinate[] downstreamCoordinates;
  private double rate;
  private boolean enabled = true;
  
  public Transition(String name, Condition[] conditions, Action[] actions, double rate) {
    this.name = name;
    this.conditions = conditions;
    this.actions = actions;
    this.rate = rate;
    
    upstreamCoordinates = Contingency.getCoordinates(conditions);
    downstreamCoordinates = Contingency.getCoordinates(actions);
  }
  
  public final Condition[] getConditions() {
    return conditions;
  }
  
  public final Coordinate[] getUpstreamCoordinates() {
    return upstreamCoordinates;
  }

  public final Action[] getActions() {
    return actions;
  }

  public final Coordinate[] getDownstreamCoordinates() {
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
