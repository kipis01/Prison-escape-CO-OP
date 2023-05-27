package GameStates;

import Entities.NPC.PrisonGuard;
import Entities.Player;
import Levels.LevelManager;
import Main.Game;
import Ui.PauseOverlay;
import Utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Playing extends State implements Statemethods {
	private Player player;

	private LevelManager levelManager;
	private boolean paused = false;
	private PauseOverlay pauseOverlay;

	private int xLevelOffset;
	private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
	private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
	private int lvlTilesWide = LoadSave.GetLevelData()[0].length;
	private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
	private int maxLevelOffsetX = maxTilesOffset * Game.TILES_SIZE;
	private List<PrisonGuard> prisonGuards;

	private BufferedImage backgroundImg, backgroundImgLayer2, backgroundImgLayer3;

	public Playing(Game game) {
		super(game);
		initClasses();

		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG);
		backgroundImgLayer2 = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG_LAYER_2);
		backgroundImgLayer3 = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG_LAYER_3);
	}

	private void initClasses() {
			levelManager = new LevelManager(game);
			player = new Player(100, 100, (int) (56 * game.SCALE), (int) (56 * game.SCALE), levelManager);
			// ...

			// Initialize the PrisonGuard instance

		player.loadLevelData(levelManager.getCurrentLeve().getLevelData());
		prisonGuards = new ArrayList<>();
		pauseOverlay = new PauseOverlay(this);
		spawnPrisonGuards();
	}

	public void windowFocusLost() {
		player.resetDirBooleans();
	}
	private void spawnPrisonGuards() {
		// Create and add PrisonGuard objects to the list
		PrisonGuard guard1 = new PrisonGuard(200, 200, 32, 64, 1); // Type 1 movement

		guard1.setLevelManager(levelManager); // Assign the levelManager
		prisonGuards.add(guard1);

		PrisonGuard guard2 = new PrisonGuard(300, 400, 32, 64, 2); // Type 2 movement
		guard2.setLevelManager(levelManager); // Assign the levelManager
		prisonGuards.add(guard2);

		// Assign the levelManager to all remaining PrisonGuard instances
		for (PrisonGuard guard : prisonGuards) {
			guard.setLevelManager(levelManager);
		}
	}
	@Override
	public void update() {
		if (!paused) {
			levelManager.update();
			player.update();

			for (PrisonGuard guard : prisonGuards) {
				guard.update();
				guard.checkTileCollision(levelManager.getCurrentLeve().getLevelData());
			}


			checkCloseToBorder();
		} else {
			pauseOverlay.update();
		}
	}


	private void checkCloseToBorder() {
		int playerX = (int) player.getHitbox().x;
		int diff = playerX - xLevelOffset;

		if (diff > rightBorder)
			xLevelOffset += diff - rightBorder;
		else if (diff < leftBorder)
			xLevelOffset += diff - leftBorder;
		if (xLevelOffset > maxLevelOffsetX)
			xLevelOffset = maxLevelOffsetX;
		else if (xLevelOffset < 0)
			xLevelOffset = 0;
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

		drawLayer2(g);
		drawLayer3(g);

		levelManager.draw(g, xLevelOffset);

		// Render the player
		player.render(g, xLevelOffset);
		// Render the PrisonGuard
		// Render the PrisonGuard
		for (PrisonGuard guard : prisonGuards) {
			guard.render(g, xLevelOffset);
		}



		if (paused) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
			pauseOverlay.draw(g);
		}
	}



	private void drawLayer2(Graphics g) {
		for (int i = 0; i < 3; i++) {
			g.drawImage(backgroundImgLayer2, 0 + i * Game.GAME_WIDTH - (int) (xLevelOffset * 0.3), 0, Game.GAME_WIDTH,
					Game.GAME_HEIGHT, null);
		}

	}

	private void drawLayer3(Graphics g) {
		for (int i = 0; i < 3; i++) {
			g.drawImage(backgroundImgLayer3, 0 + i * Game.GAME_WIDTH - (int) (xLevelOffset * 0.6), 0, Game.GAME_WIDTH,
					Game.GAME_HEIGHT, null);
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			player.setAttacking(true);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (paused)
			pauseOverlay.mousePressed(e);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (paused)
			pauseOverlay.mouseReleased(e);

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (paused)
			pauseOverlay.mouseMoved(e);

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A:
			player.setLeft(true);
			break;
		case KeyEvent.VK_D:
			player.setRight(true);
			break;
		case KeyEvent.VK_SPACE:
			player.setJump(true);
			break;
		case KeyEvent.VK_ESCAPE:
			paused = !paused;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A:
			player.setLeft(false);
			break;
		case KeyEvent.VK_D:
			player.setRight(false);
			break;
		case KeyEvent.VK_SPACE:
			player.setJump(false);
			break;

		}

	}

	public void unpauseGame() {
		paused = false;
	}

	public Player getPlayer() {
		return player;
	}

}
