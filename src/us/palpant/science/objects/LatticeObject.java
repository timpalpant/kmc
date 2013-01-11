package us.palpant.science.objects;

import us.palpant.science.Lattice;

public abstract class LatticeObject implements Comparable<LatticeObject> {
  
  /**
   * The lattice this object belongs to
   */
  private final Lattice lattice;
  /**
   * This object's position in the lattice
   */
  private int pos;
  
  public LatticeObject(Lattice lattice, int pos) {
    this.lattice = lattice;
    this.pos = pos;
  }
  
  /**
   * @return the pos
   */
  public int getPos() {
    return pos;
  }

  /**
   * @param pos the pos to set
   */
  public void setPos(int pos) {
    this.pos = pos;
  }
  
  /**
   * @return the lattice
   */
  public Lattice getLattice() {
    return lattice;
  }

  @Override
  public int compareTo(LatticeObject o) {
    return Integer.compare(pos, o.getPos());
  }

  /**
   * The lowest occupied position by this LatticeObject, if it is at position
   * pos
   * 
   * @param pos
   *          the position of the LatticeObject
   * @return the lowest position that is occupied by this LatticeObject
   */
  public abstract int low();

  /**
   * The highest occupied position by this LatticeObject, if it is at position
   * pos
   * 
   * @param pos
   *          the position of the LatticeObject
   * @return the highest position that is occupied by this LatticeObject
   */
  public abstract int high();
}
