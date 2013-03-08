package us.palpant.science.kmc.geometry;

import java.util.Iterator;

import us.palpant.science.kmc.State;

/**
 * An array of States and the corresponding Coordinates
 * @author palpant
 *
 */
public class Lattice implements Iterable<Lattice.Coordinate> {
  
  private final Coordinate[][][] coordinates;
  private final State[][][] states;
  private final BoundaryCondition bc;
  
  public Lattice(int sizeX, int sizeY, int sizeZ, BoundaryCondition bc) {
    this.bc = bc;
    coordinates = new Coordinate[sizeX][sizeY][sizeZ];
    for (int i = 0; i < coordinates.length; i++) {
      for (int j = 0; j < coordinates[i].length; j++) {
        for (int k = 0; k < coordinates[i][j].length; k++) {
          coordinates[i][j][k] = new Coordinate(this, i, j, k);
        }
      }
    }
    states = new State[sizeX][sizeY][sizeZ];
  }
  
  public Lattice(int sizeX, int sizeY, int sizeZ) {
    this(sizeX, sizeY, sizeZ, BoundaryCondition.FIXED);
  }
  
  public Lattice(int sizeX, int sizeY, BoundaryCondition bc) {
    this(sizeX, sizeY, 1, bc);
  }
  
  public Lattice(int sizeX, int sizeY) {
    this(sizeX, sizeY, BoundaryCondition.FIXED);
  }
  
  public Lattice(int sizeX, BoundaryCondition bc) {
    this(sizeX, 1, bc);
  }
  
  public Lattice(int sizeX) {
    this(sizeX, BoundaryCondition.FIXED);
  }
  
  /* (non-Javadoc)
   * @see us.palpant.science.kmc.geometry.Geometry#iterator()
   */
  @Override
  public Iterator<Lattice.Coordinate> iterator() {
    return new LatticeIterator();
  }
  
  public Coordinate coordinate(int x, int y, int z) {
    return coordinates[x][y][z];
  }
  
  public Coordinate coordinate(int x, int y) {
    return coordinate(x, y, 0);
  }
  
  public Coordinate coordinate(int x) {
    return coordinate(x, 0);
  }
  
  public State get(Coordinate coord) {
    return states[coord.getX()][coord.getY()][coord.getZ()];
  }
  
  public void set(Coordinate coord, State s) {
    states[coord.getX()][coord.getY()][coord.getZ()] = s;
  }
  
  /**
   * Set all coordinates in the Lattice to a specific State
   * @param s the state to set the lattice to
   */
  public void fill(State s) {
    for (int i = 0; i < states.length; i++) {
      for (int j = 0; j < states[i].length; j++) {
        for (int k = 0; k < states[i][j].length; k++) {
          states[i][j][k] = s;
        }
      }
    }
  }
  
  public final int sizeX() {
    return states.length;
  }
  
  public final int sizeY() {
    return states[0].length;
  }
  
  public final int sizeZ() {
    return states[0][0].length;
  }
  
  /**
   * @return the bc
   */
  public BoundaryCondition getBc() {
    return bc;
  }

  public final int numSites() {
    return sizeX() * sizeY() * sizeZ();
  }
  
  public final int count(State s) {
    int count = 0;
    for (Coordinate c : this) {
      if (get(c) == s) {
        count++;
      }
    }
    return count;
  }

  /**
   * A Coordinate in the Lattice
   * @author palpant
   *
   */
  public class Coordinate {

    private final Lattice lattice;
    private final int x, y, z;
    
    private Coordinate(Lattice lattice, int x, int y, int z) {
      this.lattice = lattice;
      this.x = x;
      this.y = y;
      this.z = z;
    }
    
    private Coordinate(Lattice lattice, int x, int y) {
      this(lattice, x, y, 0);
    }
    
    private Coordinate(Lattice lattice, int x) {
      this(lattice, x, 0);
    }
    
    public final Lattice getLattice() {
      return lattice;
    }
    
    public final State getState() {
      return lattice.get(this);
    }

    public final int getX() {
      return x;
    }

    public final int getY() {
      return y;
    }

    public final int getZ() {
      return z;
    }
    
    @Override
    public boolean equals(Object o) {
      if (o instanceof Coordinate) {
        Coordinate c = (Coordinate) o;
        return c.x == x && c.y == y && c.z == z;
      }
      
      return false;
    }
    
    @Override
    public String toString() {
      return x+","+y+","+z;
    }
    
  }
  
  public class LatticeIterator implements Iterator<Coordinate> {

    int x, y, z;
    
    @Override
    public boolean hasNext() {
      return x < sizeX() && y < sizeY() && z < sizeZ();
    }

    @Override
    public Coordinate next() {
      Coordinate c = coordinate(x, y, z);
      
      z++;
      y += z / sizeZ();
      z %= sizeZ();
      x += y / sizeY();
      y %= sizeY();
      
      return c;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Cannot remove lattice sites");
    }
    
  }
  
  public enum BoundaryCondition {
    PERIODIC, FIXED;
  }
  
}
