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
import java.util.ArrayList;
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
	private Player player2 = null;

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
	private boolean playerDying;
	private NetWorker server, client;
	private PlayerData playerData;
	private PlayerData tmPlayerData;
	private List<NpcData> tmpNpcData;
	private int playerId;
	private boolean isPlayerTwoConnected;
	private boolean connected = false;
	private int tmpUpdateCheck = 0;

	public Playing(Game game) {
		super(game);
		initClasses();

		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG);
		backgroundImgLayer2 = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG_LAYER_2);
		backgroundImgLayer3 = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG_LAYER_3);
	}

	private void initClasses() {

		// Load the level
		levelManager = new LevelManager(game);

		// Load the enemies
		enemyManager = new EnemyManager(this);

		// Load the player
		player = new Player(200, 100, (int) (56 * game.SCALE), (int) (56 * game.SCALE), this);
		player.loadLevelData(levelManager.getCurrentLevel().getLevelData());

		player2 = new Player(200, 100, (int) (56 * game.SCALE), (int) (56 * game.SCALE), this);
		player2.loadLevelData(levelManager.getCurrentLevel().getLevelData());

		pauseOverlay = new PauseOverlay(this);
		gameOverOverlay = new GameOverOverlay(this);

	}

	private void initClient() {
		try {
			client = new NetWorker(InetAddress.getByName("localhost"), 8000, "temp");
		} catch (UnknownHostException e) {
			e.printStackTrace();
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
		tmpUpdateCheck++;
		if (paused)
			pauseOverlay.update();
		else if (gameOver) {
//			gameOverOverlay.update();
		} else if (playerDying) {
			player.update();
		} else {
			levelManager.update();
			player.update();
			if (NetworkState.state == NetworkState.HOST) {
				if (tmpUpdateCheck == 1)
					enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
				if (tmpUpdateCheck == 2)
					enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player2);
			}

			checkCloseToBorder();
		}
		if (tmpUpdateCheck == 2)
			tmpUpdateCheck = 0;

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

		if (!connected)
			makeConnection();

		PlayerData playerData = new PlayerData();
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		drawLayer2(g);
		drawLayer3(g);

		levelManager.draw(g, xLevelOffset);

		// Draw the host player
		player.render(g, xLevelOffset, NetworkState.state == NetworkState.HOST ? server : client, playerData, 1,
				NetworkState.state == NetworkState.HOST ? isPlayerTwoConnected : true);

		// playerTwo
		if (connected && player2 != null)
			drawPlayerTwo(g);

		if (NetworkState.state == NetworkState.HOST)
			enemyManager.draw(g, xLevelOffset, server, isPlayerTwoConnected);

		if (paused) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
			pauseOverlay.draw(g);
		} else if (gameOver) {
			gameOverOverlay.draw(g);
		}
	}

	private void makeConnection() {
		if (NetworkState.state == NetworkState.JOIN) {
			initClient();
			if (client != null)
				connected = true;
		}

		if (NetworkState.state == NetworkState.HOST) {
			initHost();
			if (server != null)
				connected = true;
		}

	}

	public void resetAll() {
		gameOver = false;
		paused = false;
		player.resetAll();
		enemyManager.resetAllEnemies();
	}

	public void drawPlayerTwo(Graphics g) {
		Boolean playerDrawn = false;
		Boolean npcDrawn = false;
		PlayerData playerTwoData = new PlayerData();
		List<NpcData> npcData = new ArrayList<>();

		List<Object> recPackets = NetworkState.state == NetworkState.HOST ? server.getReceivedPackets()
				: client.getReceivedPackets();

		if (!recPackets.isEmpty()) {
//			System.out.println(recPackets);

			for (Object packet : recPackets) {

				if (packet instanceof PlayerData) {

					tmPlayerData = playerTwoData = (PlayerData) packet;

					if (playerTwoData.playerId != playerId)
						isPlayerTwoConnected = true;

					player2.renderPlayerTwo(g, xLevelOffset, playerTwoData);

					playerDrawn = true;

				}
				if (packet instanceof List) {

					tmpNpcData = npcData = (List<NpcData>) packet;
					enemyManager.drawClient(g, xLevelOffset, npcData);
					npcDrawn = true;
				}
			}

		}
		if (!playerDrawn && tmPlayerData != null)
			player2.renderPlayerTwo(g, xLevelOffset, tmPlayerData);
		if (!npcDrawn && tmpNpcData != null)
			enemyManager.drawClient(g, xLevelOffset, tmpNpcData);
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

	public void setPlayerDying(boolean playerDying) {
		this.playerDying = playerDying;
	}

}
