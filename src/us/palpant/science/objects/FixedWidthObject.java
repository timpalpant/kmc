package us.palpant.science.objects;

import us.palpant.science.Lattice;

/**
 * Represents a symmetric nucleosome on the Lattice
 * 
 * @author palpant
 * 
 */
public class FixedWidthObject extends LatticeObject {

  /**
   * The size of this Nucleosome
   */
  private int size;

  public FixedWidthObject(Lattice lattice, int pos, int size) {
    super(lattice, pos);
    this.size = size;
  }

  @Override
  public int low() {
    return getPos() - size / 2;
  }

  @Override
  public int high() {
    return getPos() + size / 2;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  @Override
  public String toString() {
    return "Nucleosome";
  }

}
