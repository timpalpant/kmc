package us.palpant.science;

import java.awt.Graphics;

import javax.swing.JPanel;

public class LatticePanel extends JPanel {

  private static final long serialVersionUID = -8011547669904007538L;
  
  private final int latticeSize;
  private final int objectSize;
  private int[] positions;

  /**
   * Create the panel.
   */
  public LatticePanel(int latticeSize, int objectSize) {
    this.latticeSize = latticeSize;
    this.objectSize = objectSize;
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Graphics2D g2d = (Graphics2D) g;
    int height = getHeight();
    int width = getWidth();
    int nucDrawWidth = objectSize * width / latticeSize;
    for (int p : positions) {
      int x = p * width / latticeSize - nucDrawWidth/2;
      g.fillOval(x, 0, nucDrawWidth, height);
    }
  }

  public int[] getPositions() {
    return positions;
  }

  public void setPositions(int[] positions) {
    this.positions = positions;
    repaint();
  }

}
