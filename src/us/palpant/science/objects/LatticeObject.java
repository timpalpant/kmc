package us.palpant.science.objects;

public interface LatticeObject {
  /**
   * The lowest occupied position by this LatticeObject, if it is at position
   * pos
   * 
   * @param pos
   *          the position of the LatticeObject
   * @return the lowest position that is occupied by this LatticeObject
   */
  int low(int pos);

  /**
   * The highest occupied position by this LatticeObject, if it is at position
   * pos
   * 
   * @param pos
   *          the position of the LatticeObject
   * @return the highest position that is occupied by this LatticeObject
   */
  int high(int pos);
}
