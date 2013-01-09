package us.palpant.science;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class LatticePanel extends JPanel {

	private static final long serialVersionUID = -8011547669904007538L;

	/**
	 * Create the panel.
	 */
	public LatticePanel() {

	}
	
	@Override
	public void paint(Graphics g) {
      super.paint(g);
      //Draws the line
      g.drawOval(0,0,this.getWidth(), this.getHeight());

      //draws filled circle
      g.setColor(Color.red); 
      g.fillOval(0,0,this.getWidth(), this.getHeight());
  }

}
