package Entities;

import static Utils.Constants.Directions.RIGHT;
import static Utils.Constants.EnemyConstants.GetMaxHealth;
import static Utils.Constants.EnemyConstants.LIGHT_BANDIT;
import static Utils.Constants.EnemyConstants.LIGHT_BANDIT_DRAWOFFSET_X;
import static Utils.Constants.EnemyConstants.LIGHT_BANDIT_DRAWOFFSET_Y;
import static Utils.Constants.EnemyConstants.LIGHT_BANDIT_HEIGHT;
import static Utils.Constants.EnemyConstants.LIGHT_BANDIT_HEIGHT_DEFAULT;
import static Utils.Constants.EnemyConstants.LIGHT_BANDIT_WIDTH;
import static Utils.Constants.EnemyConstants.LIGHT_BANDIT_WIDTH_DEFAULT;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import GameStates.Playing;
import Networking.NetWorker;
import Networking.NpcData;
import Utils.FlipImage;
import Utils.LoadSave;

public class EnemyManager {

	private Playing playing;
	private BufferedImage[][] lightBanditArr;
	private ArrayList<LightBandit> lbandits = new ArrayList<>();
	private boolean defaultDirection = true;

	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
		addEnemies();
	}

	private void addEnemies() {
		lbandits = LoadSave.GetLightBandits();
	}

	public void update(int[][] lvlData, Player player) {
		for (LightBandit lb : lbandits)
			lb.update(lvlData, player);
	}

	public void draw(Graphics g, int xLevelOffset, NetWorker server, boolean isPlayerTwoConnected) {
		drawLightBandits(g, xLevelOffset, server, isPlayerTwoConnected);
	}

	public void drawClient(Graphics g, int xLevelOffset, NpcData npc) {
		drawLightBanditsClient(g, xLevelOffset, npc);
	}

	private void drawLightBanditsClient(Graphics g, int xLevelOffset, NpcData npc) {

		if (npc.isActive) {
			BufferedImage animation;

			if (npc.direction == RIGHT)
				animation = FlipImage.flipImageHorizontally(lightBanditArr[npc.enemyState][npc.aniIndex]);
			else
				animation = lightBanditArr[npc.enemyState][npc.aniIndex];

			g.drawImage(animation, npc.xLoc - xLevelOffset, npc.yLoc, LIGHT_BANDIT_WIDTH, LIGHT_BANDIT_HEIGHT, null);

			g.setColor(Color.RED);

		}

	}

	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for (LightBandit lb : lbandits)
			if (lb.isActive()) {
				if (attackBox.intersects(lb.getHitbox())) {
					lb.hurt(50); // player damage
					return;
				}
			}
	}

	private void drawLightBandits(Graphics g, int xLevelOffset, NetWorker server, boolean isPlayerTwoConnected) {
		for (LightBandit lb : lbandits) {
			if (lb.isActive()) {
				BufferedImage animation;
				int healthWidth = (int) ((lb.getCurrentHealth() / (float) GetMaxHealth(LIGHT_BANDIT) * 50));
				if (lb.getWalkDir() == RIGHT)
					animation = FlipImage.flipImageHorizontally(lightBanditArr[lb.getEnemyState()][lb.getAniIndex()]);
				else
					animation = lightBanditArr[lb.getEnemyState()][lb.getAniIndex()];
				;

				lb.drawHealthbar(g, xLevelOffset, healthWidth);

				// Debugging
//				lb.drawHitbox(g, xLevelOffset);
//				lb.drawAttackBox(g, xLevelOffset);

				int xLoc = (int) lb.getHitbox().x - LIGHT_BANDIT_DRAWOFFSET_X - xLevelOffset;
				int yLoc = (int) lb.getHitbox().y - LIGHT_BANDIT_DRAWOFFSET_Y;

				g.drawImage(animation, xLoc, yLoc, LIGHT_BANDIT_WIDTH, LIGHT_BANDIT_HEIGHT, null);

				NpcData npc = new NpcData();
				npc.xLoc = xLoc + xLevelOffset;
				npc.yLoc = yLoc;
				npc.enemyState = lb.getEnemyState();
				npc.aniIndex = lb.getAniIndex();
				npc.direction = lb.getWalkDir();
				npc.isActive = lb.isActive();

				try {
					if (isPlayerTwoConnected) {
						server.SendPacket(npc);
					}

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	private void loadEnemyImgs() {
		lightBanditArr = new BufferedImage[5][8];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.LIGHT_BANDIT_SPRITE);
		for (int j = 0; j < lightBanditArr.length; j++)
			for (int i = 0; i < lightBanditArr[j].length; i++)
				lightBanditArr[j][i] = temp.getSubimage(i * LIGHT_BANDIT_WIDTH_DEFAULT, j * LIGHT_BANDIT_HEIGHT_DEFAULT,
						LIGHT_BANDIT_WIDTH_DEFAULT, LIGHT_BANDIT_HEIGHT_DEFAULT);
	}

	public void resetAllEnemies() {
		for (LightBandit lb : lbandits)
			lb.resetEnemy();
	}
}
