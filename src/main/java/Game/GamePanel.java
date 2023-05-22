package Game;

import java.awt.Graphics;

import javax.swing.JPanel;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;

public class GamePanel extends JPanel {

	private MouseInputs mouseInputs;
	private int xDelta = 100, yDelta = 100;

	public GamePanel() {
		// KeyboardInputs extends KeyListener
		// Pass this class to Keyboard inputs to allow access to this class
		addKeyListener(new KeyboardInputs(this));

		mouseInputs = new MouseInputs(this);
		addMouseListener(mouseInputs);
		addMouseMotionListener(mouseInputs);
	}

	public void changeXDelta(int value) {
		this.xDelta += value;
		// repaint the componenet to see changes
		repaint();
	}

	public void changeYDelta(int value) {
		this.yDelta += value;
		repaint();
	}

	public void setRectPos(int x, int y) {
		this.xDelta = x;
		this.yDelta = y;
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.fillRect(xDelta, yDelta, 200, 50);
	}
}
