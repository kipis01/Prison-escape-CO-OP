package Main;

import Entities.NPC.PrisonGuard;
import GameStates.GameState;
import GameStates.Menu;
import GameStates.Playing;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Game implements Runnable {

	private GameWindow gameWindow;
	private GamePanel gamePanel;
	private Thread gameThread;
	private final int FPS_SET = 120;
	private final int UPS_SET = 200;

	private Playing playing;
	private Menu menu;
	private List<PrisonGuard> enemies;



	public final static int TILES_DEFAULT_SIZE = 24;
	public final static float SCALE = 2.5f;
	public final static int TILES_IN_WIDTH = 26;
	public final static int TILES_IN_HEIGHT = 14;
	public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
	public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
	public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

	public Game() {
		initClasses();
		gamePanel = new GamePanel(this);
		gameWindow = new GameWindow(gamePanel);
		gamePanel.requestFocus(); // Enable inputs

		startGameLoop();
	}


	private void initClasses() {
		menu = new Menu(this);
		playing = new Playing(this);
		enemies = new ArrayList<>();
	}




	private void startGameLoop() {
		gameThread = new Thread(this);
		gameThread.start();
		PrisonGuard enemy = new PrisonGuard(0, 0,100,100, 1); // Set the initial position of the enemy
		enemies.add(enemy);
	}

	private void update() {
		switch (GameState.state) {
			case MENU:
				menu.update();
				break;
			case PLAYING:
				playing.update();
				updateEnemies();
				break;
			case OPTIONS:
			case QUIT:
				System.exit(0);
				break;
			default:
				break;
		}
	}

	private void updateEnemies() {
		for (PrisonGuard enemy : enemies) {
			enemy.update();
		}
	}


	public void render(Graphics g) {
		switch (GameState.state) {
			case MENU:
				menu.draw(g);
				break;
			case PLAYING:
				playing.draw(g);
				break;
			default:
				break;
		}
	}

	@Override
	public void run() {

		double timePerFrame = 1000000000.0 / FPS_SET; // Nanoseconds
		double timePerUpdate = 1000000000.0 / UPS_SET; // Nanoseconds

		long previousTime = System.nanoTime();

		int frames = 0;
		int updates = 0;
		long lastCheck = System.currentTimeMillis();

		double deltaU = 0;
		double deltaF = 0;

		while (true) {
			long currentTime = System.nanoTime();

			deltaU += (currentTime - previousTime) / timePerUpdate;
			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;

			if (deltaU >= 1) {
				update();
				updates++;
				deltaU--;
			}

			if (deltaF >= 1) {
				gamePanel.repaint();
				frames++;
				deltaF--;
			}

			// Fps counter
			if (System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();
				System.out.println("FPS: " + frames + " | UPS: " + updates);
				frames = 0;
				updates = 0;
			}
		}

	}

	public void windowFocusLost() {
		if (GameState.state == GameState.PLAYING)
			playing.getPlayer().resetDirBooleans();
	}

	public Menu getMenu() {
		return menu;
	}

	public Playing getPlaying() {
		return playing;
	}
}
