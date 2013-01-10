package us.palpant.science.objects;

/**
 * Represents a symmetric nucleosome on the Lattice
 * 
 * @author palpant
 * 
 */
public class FixedWidthObject implements LatticeObject {

  /**
   * The size of this Nucleosome
   */
  private int size;

  public FixedWidthObject(int size) {
    this.setSize(size);
  }

  @Override
  public int low(int pos) {
    return pos - size / 2;
  }

  @Override
  public int high(int pos) {
    return pos + size / 2;
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
