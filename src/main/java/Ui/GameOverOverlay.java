package Ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import GameStates.GameState;
import GameStates.Playing;
import Main.Game;

public class GameOverOverlay {

	private Playing playing;
	
	public GameOverOverlay(Playing playing) {
		this.playing = playing;
	}
	
	public void draw(Graphics g) {
		Color myWhite = new Color(255, 255, 255); // Color white
		
		g.setColor(new Color(0, 0, 0, 230));
		g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
		
		g.setColor(myWhite);
		g.fillRect(400, 200, Game.GAME_WIDTH / 2, Game.GAME_HEIGHT / 2);
	
		g.setColor(Color.black);

		g.drawString("Game Over",Game.GAME_WIDTH /2 - 40, 300);
		g.drawString("Press esc to enter Main Menu!", Game.GAME_WIDTH / 2 - 90, 500);
	}
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			playing.resetAll();
			GameState.state = GameState.MENU;
		}
	}
}
