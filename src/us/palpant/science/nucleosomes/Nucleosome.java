package us.palpant.science.nucleosomes;

/**
 * Represents a nucleosome on the Lattice
 * @author palpant
 *
 */
public class Nucleosome implements LatticeObject {
	
	/**
	 * The size of this Nucleosome
	 */
	private int size;
	
	public Nucleosome(int size) {
		this.setSize(size);
	}
	
	public Nucleosome() {
		this(Parameters.NUC_SIZE);
	}

	@Override
	public int low(int pos) {
		return pos - size/2;
	}

	@Override
	public int high(int pos) {
		return pos + size/2;
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
	
	public static class Factory implements LatticeObjectFactory {
		public Nucleosome newInstance() {
			return new Nucleosome();
		}
	}

}
