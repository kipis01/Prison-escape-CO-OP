package Main;

import static Main.Game.GAME_HEIGHT;
import static Main.Game.GAME_WIDTH;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;

public class GamePanel extends JPanel {

	private MouseInputs mouseInputs;
	private Game game;

	public GamePanel(Game game) {
		this.game = game;
		// KeyboardInputs extends KeyListener
		// Pass this class to Keyboard inputs to allow access to this class
		addKeyListener(new KeyboardInputs(this));

		setPanelSize();

		mouseInputs = new MouseInputs(this);
		addMouseListener(mouseInputs);
		addMouseMotionListener(mouseInputs);
	}

	private void setPanelSize() {
		Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
		setPreferredSize(size);
	}

	public void updateGame() {

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		game.render(g);
	}

	public Game getGame() {
		return game;
	}

}
