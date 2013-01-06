package us.palpant.science.nucleosomes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * Model for the lattice of objects
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
	 * Stores the position of LatticeObjects currently on the Lattice
	 */
	private Map<LatticeObject,Integer> positions = new HashMap<LatticeObject,Integer>();
	/**
	 * Holds the set of LatticeObjects currently on the Lattice, sorted by position
	 */
	private TreeSet<LatticeObject> objects = new TreeSet<LatticeObject>(new PositionComparator());
	
	/**
	 * Create a new Lattice with the given potential
	 * @param potential the potential energy for each point in the Lattice
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
	
	public LatticeObject first() {
		return objects.first();
	}
	
	public LatticeObject last() {
		return objects.last();
	}
	
	public LatticeObject lower(LatticeObject object) {
		return objects.lower(object);
	}
	
	public LatticeObject higher(LatticeObject object) {
		return objects.higher(object);
	}
	
	public LatticeObject floor(LatticeObject object) {
		return objects.floor(object);
	}
	
	public LatticeObject ceiling(LatticeObject object) {
		return objects.ceiling(object);
	}

	/**
	 * Add a LatticeObject to the Lattice
	 * @param object the LatticeObject to add
	 */
	public void addObject(LatticeObject object, int pos) {
		positions.put(object, getPeriodicWrap(pos));
		objects.add(object);
		checkCollisions(object);
	}
	
	/**
	 * Remove a LatticeObject from the lattice
	 * @param object the LatticeObject to remove
	 */
	public void removeObject(LatticeObject object) {
		objects.remove(object);
		positions.remove(object);
	}
	
	/**
	 * Get the potential at position i
	 * @param i the position in the lattice
	 * @return the potential for position i
	 */
	public double potential(int i) {
		return potential[i];
	}
	
	/**
	 * Get the position of a LatticeObject on the Lattice
	 * @param object the object of interest
	 * @return the position of the object on the lattice
	 */
	public int getPosition(LatticeObject object) {
		return positions.get(object);
	}
	
	/**
	 * Update the position of a LatticeObject in the Lattice
	 * @param object the LatticeObject to move
	 * @param pos the new position of the LatticeObject in the Lattice
	 * @throws NoSuchElementException if the object is not already in the Lattice. 
	 * 				 Use addObject() to add a new LatticeObject to the Lattice
	 */
	public void setPosition(LatticeObject object, int pos) throws NoSuchElementException {
		if (!objects.contains(object)) {
			throw new NoSuchElementException("Cannot update the position of a nucleosome not in the Lattice!");
		}
		
		// Check for collisions in the new location
		objects.remove(object);
		positions.put(object, getPeriodicWrap(pos));
		objects.add(object);
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
	 * @param pos a position in the Lattice
	 * @return the periodic-wrapped index of pos
	 */
	public int getPeriodicWrap(int pos) {
		if (pos < 0) {
			return size() + pos;
		} else if (pos >= size()) {
			return pos % size();
		}
		
		return pos;
	}
	
	/**
	 * Check for collisions involving a LatticeObject
	 * @param object the reference LatticeObject
	 * @return true if there are collisions involving this LatticeObject
	 */
	private boolean checkCollisions(LatticeObject object) {
		int pos = getPosition(object);
		int low = object.low(pos);
		int high = object.high(pos);
		
		// Only need to check closest LatticeObject on either side of pos
		LatticeObject lower = objects.lower(object);
		if (lower != null && lower.high(getPosition(lower)) >= low) {
			log.warn("Collision detected between "+object+" and "+lower);
			return true;
		}
		LatticeObject higher = objects.higher(object);
		if (higher != null && higher.low(getPosition(higher)) <= high) {
			log.warn("Collision detected between "+object+" and "+higher);
			return true;
		}
		// If periodic BoundaryConditions and the new object is on the end, check wrapped collision
		if (getBoundaryCondition() == BoundaryCondition.PERIODIC) {
			if (low < 0) {
				LatticeObject last = objects.last();
				if (object != last) {
					if (last.high(getPosition(last)) >= getPeriodicWrap(low)) {
						log.warn("Periodic collision detected between "+object+" and "+last);
						return true;
					}
				}
			} 
			if (high >= size()) {
				LatticeObject first = objects.first();
				if (object != first) {
					if (getPeriodicWrap(high) >= first.low(getPosition(first))) {
						log.warn("Periodic collision detected between "+object+" and "+first);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private class PositionComparator implements Comparator<LatticeObject> {

		@Override
		public int compare(LatticeObject n1, LatticeObject n2) {
			if (!positions.containsKey(n1)) {
				throw new IllegalArgumentException("Cannot compare position of nucleosome "+n1+" not in lattice");
			}
			if (!positions.containsKey(n2)) {
				throw new IllegalArgumentException("Cannot compare position of nucleosome "+n2+" not in lattice");
			}

			int pos1 = positions.get(n1);
			int pos2 = positions.get(n2);
			return Integer.compare(pos1, pos2);
		}
		
	}

} 
