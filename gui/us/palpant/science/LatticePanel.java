package us.palpant.science;

import java.awt.Graphics;

import javax.swing.JPanel;

public class LatticePanel extends JPanel {

  private static final long serialVersionUID = -8011547669904007538L;
  
  private final int numGridPoints;
  private int[] positions;

  /**
   * Create the panel.
   */
  public LatticePanel(int numGridPoints) {
    this.numGridPoints = numGridPoints;
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Graphics2D g2d = (Graphics2D) g;
    int height = getHeight();
    int width = getWidth();
    int nucDrawWidth = Parameters.NUC_SIZE * width / numGridPoints;
    for (int p : positions) {
      int x = p * width / numGridPoints - nucDrawWidth/2;
      //g.drawOval(x, 0, nucDrawWidth, height);
      g.fillOval(x, 0, nucDrawWidth, height);
      // Ellipse2D.Double ellipse = new Ellipse2D.Double(x, 0, nucDrawWidth, height);
       //g2d.fill(ellipse);
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
