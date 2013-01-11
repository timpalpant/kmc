package us.palpant.science;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import us.palpant.science.objects.LatticeObject;

/**
 * Model for the lattice of objects
 * 
 * @author palpant
 * 
 */
public class Lattice implements Iterable<LatticeObject> {
  
  private static final Logger log = Logger.getLogger(Lattice.class);

  /**
   * Potential energy for each point on the lattice
   */
  private final double[] potential;
  /**
   * Boundary condition to use
   */
  private final BoundaryCondition bc;
  /**
   * Holds the set of LatticeObjects currently on the Lattice, sorted by
   * position
   */
  private List<LatticeObject> objects = new ArrayList<>();

  /**
   * Create a new Lattice of a given length
   * 
   * @param length
   *          the number of points on the Lattice
   */
  public Lattice(int length) {
    this(new double[length]);
  }

  /**
   * Create a new Lattice with the given potential
   * 
   * @param potential
   *          the potential energy for each point in the Lattice
   */
  public Lattice(final double[] potential) {
    this(potential, BoundaryCondition.FIXED);
  }

  public Lattice(final double[] potential, BoundaryCondition bc) {
    this.potential = potential;
    this.bc = bc;
  }

  @Override
  public Iterator<LatticeObject> iterator() {
    return objects.iterator();
  }
  
  public void sort() {
    log.debug("Sorting Lattice");
    Collections.sort(objects);
  }

  /**
   * @return the first object in the Lattice
   */
  public LatticeObject first() {
    if (objects.size() > 0) {
      return objects.get(0);
    }
    
    return null;
  }

  /**
   * @return the last object in the Lattice
   */
  public LatticeObject last() {
    if (objects.size() > 0) {
      return objects.get(objects.size()-1);
    }
    
    return null;
  }

  /**
   * Add a LatticeObject to the Lattice
   * 
   * @param object
   *          the LatticeObject to add
   */
  public void addObject(LatticeObject object) {
    objects.add(object);
    sort();
  }

  /**
   * Remove a LatticeObject from the lattice
   * 
   * @param object
   *          the LatticeObject to remove
   */
  public void removeObject(LatticeObject object) {
    objects.remove(object);
  }

  /**
   * Get the potential at position pos
   * 
   * @param pos
   *          the position in the lattice
   * @return the potential for position pos
   */
  public double getPotential(int pos) {
    return potential[getPeriodicWrap(pos)];
  }

  /**
   * @return the positions of all objects on the Lattice
   */
  public int[] getAllPositions() {
    int[] allPositions = new int[numObjects()];
    int i = 0;
    for (LatticeObject o : this) {
      allPositions[i] = o.getPos();
      i++;
    }

    return allPositions;
  }

  /**
   * @return the number of Lattice positions
   */
  public int size() {
    return potential.length;
  }

  /**
   * @return the number of objects on the Lattice
   */
  public int numObjects() {
    return objects.size();
  }

  /**
   * @return the boundary conditions for this Lattice
   */
  public BoundaryCondition getBoundaryCondition() {
    return bc;
  }

  /**
   * @param pos
   *          a position in the Lattice
   * @return the periodic-wrapped index of pos
   */
  public int getPeriodicWrap(int pos) {
    if (bc == BoundaryCondition.PERIODIC) {
      if (pos < 0) {
        // Recurse until pos is within [0, size())
        return getPeriodicWrap(size() + pos);
      } else if (pos >= size()) {
        return pos % size();
      }
    }

    return pos;
  }

  /**
   * @author timpalpant
   * 
   */
  public enum BoundaryCondition {
    PERIODIC, FIXED;
  }

}
