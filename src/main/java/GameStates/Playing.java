package GameStates;

import static Main.Game.GAME_WIDTH;
import static Main.Game.TILES_IN_WIDTH;
import static Main.Game.TILES_SIZE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import Entities.EnemyManager;
import Entities.Player;
import Levels.LevelManager;
import Main.Game;
import Networking.NetWorker;
import Networking.NpcData;
import Networking.PlayerData;
import Ui.GameOverOverlay;
import Ui.PauseOverlay;
import Utils.LoadSave;

public class Playing extends State implements Statemethods {
	private Player player;

	private LevelManager levelManager;
	private EnemyManager enemyManager;
	private boolean paused = false;
	private PauseOverlay pauseOverlay;
	private GameOverOverlay gameOverOverlay;

	private int xLevelOffset;

	// Level border needed to offset the background when in the first 20% or the
	// last 80%
	private int leftBorder = (int) (0.2 * GAME_WIDTH);
	private int rightBorder = (int) (0.8 * GAME_WIDTH);

	// Entire level width (img width)
	private int lvlTilesWide = LoadSave.GetLevelData()[0].length;

	// Remaining tiles for the level
	private int maxTilesOffset = lvlTilesWide - TILES_IN_WIDTH;
	private int maxLevelOffsetX = maxTilesOffset * TILES_SIZE;

	private BufferedImage backgroundImg, backgroundImgLayer2, backgroundImgLayer3;
	private boolean gameOver;

	private NetWorker server, client;
	private PlayerData playerData;
	private PlayerData tmPlayerData;
	private NpcData tmpNpcData;
	private int playerId;
	private boolean isPlayerTwoConnected;
	private boolean connected = false;

	public Playing(Game game) {
		super(game);
		initClasses();

		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG);
		backgroundImgLayer2 = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG_LAYER_2);
		backgroundImgLayer3 = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG_LAYER_3);
	}

	private void initClasses() {
		initNet();
		// Load the level
		levelManager = new LevelManager(game);

		// Load the enemies
		enemyManager = new EnemyManager(this);

		// Load the player
		player = new Player(200, 100, (int) (56 * game.SCALE), (int) (56 * game.SCALE), this);
		player.loadLevelData(levelManager.getCurrentLeve().getLevelData());
		pauseOverlay = new PauseOverlay(this);
		gameOverOverlay = new GameOverOverlay(this);

	}

	private void initNet() {
		switch (NetworkState.state) {
		case HOST:
			initHost();
			break;
		case JOIN:
			initClient();
			break;
		}
	}

	private void initClient() {
		try {
			client = new NetWorker(InetAddress.getByName("localhost"), 8000, "temp");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		client.getStatus();
		client.startNetWorker();

	}

	private void initHost() {
		server = new NetWorker(8000, "temp");
		server.startNetWorker();

	}

	public void windowFocusLost() {
		player.resetDirBooleans();
	}

	@Override
	public void update() {
		if (!paused && !gameOver) {
			levelManager.update();
			player.update();
			enemyManager.update(levelManager.getCurrentLeve().getLevelData(), player);
			checkCloseToBorder();
		} else {
			pauseOverlay.update();
		}
	}

	// Check when to move the background based on the player position
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

		if (NetworkState.state == NetworkState.JOIN && !connected) {
			initClient();
			if (client != null)
				connected = true;
		}

		if (NetworkState.state == NetworkState.HOST && !connected) {
			initHost();
			if (server != null)
				connected = true;
		}

		playerId = NetworkState.state == NetworkState.HOST ? 1 : 2;
		PlayerData playerData = new PlayerData();
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		drawLayer2(g);
		drawLayer3(g);

		levelManager.draw(g, xLevelOffset);

		// Draw the player
		player.render(g, xLevelOffset, NetworkState.state == NetworkState.HOST ? server : client, playerData, playerId,
				NetworkState.state == NetworkState.HOST ? isPlayerTwoConnected : true);

		// playerTwo
		if (connected) {

			PlayerData playerTwoData = new PlayerData();
			NpcData npcData = new NpcData();

			List<Object> recPackets = NetworkState.state == NetworkState.HOST ? server.getReceivedPackets()
					: client.getReceivedPackets();

			if (!recPackets.isEmpty()) {
//				System.out.println(recPackets);

				for (Object packet : recPackets) {

					if (packet instanceof PlayerData) {

						tmPlayerData = playerTwoData = (PlayerData) packet;
						if (playerTwoData.playerId != playerId)
							isPlayerTwoConnected = true;
						player.renderPlayerTwo(g, maxLevelOffsetX, playerTwoData);

					}
					if (packet instanceof NpcData) {
						tmpNpcData = npcData = (NpcData) packet;
						enemyManager.drawClient(g, xLevelOffset, npcData);
					}
				}

			}
			if (recPackets.isEmpty() && tmPlayerData != null) {
				player.renderPlayerTwo(g, maxLevelOffsetX, tmPlayerData);
			}
		}

		if (NetworkState.state == NetworkState.HOST) {
			// Draw the enemies
			enemyManager.draw(g, xLevelOffset, server, isPlayerTwoConnected);
		}

		if (paused) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
			pauseOverlay.draw(g);
		} else if (gameOver) {
			gameOverOverlay.draw(g);
		}
	}

	public void resetAll() {
		gameOver = false;
		paused = false;
		player.resetAll();
		enemyManager.resetAllEnemies();
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		enemyManager.checkEnemyHit(attackBox);
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

	public void mouseDragged(MouseEvent e) {
		if (!gameOver)
			if (paused)
				pauseOverlay.mouseDragged(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!gameOver) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				player.setAttacking(true);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!gameOver)
			if (paused)
				pauseOverlay.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!gameOver)
			if (paused)
				pauseOverlay.mouseReleased(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!gameOver)
			if (paused)
				pauseOverlay.mouseMoved(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (gameOver)
			gameOverOverlay.keyPressed(e);
		else
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
			case KeyEvent.VK_SHIFT:
				player.setSprint(true);
				break;
			case KeyEvent.VK_ESCAPE:
				paused = !paused;
				break;
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!gameOver)
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				player.setLeft(false);
				break;
			case KeyEvent.VK_D:
				player.setRight(false);
				break;
			case KeyEvent.VK_SHIFT:
				player.setSprint(false);
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
