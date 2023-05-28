package Entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static Utils.Constants.Directions.*;

import GameStates.Playing;
import Utils.FlipImage;
import Utils.LoadSave;
import static Utils.Constants.EnemyConstants.*;

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
		for(LightBandit lb : lbandits)
			lb.update(lvlData, player);
	}
	
	public void draw(Graphics g, int xLevelOffset) {
		drawLightBandits(g, xLevelOffset);
	}
	
	private void drawLightBandits(Graphics g, int xLevelOffset) {
		for(LightBandit lb : lbandits) {
			BufferedImage animation;
			
			if (lb.getWalkDir() == RIGHT)
				animation = FlipImage.flipImageHorizontally(lightBanditArr[lb.getEnemyState()][lb.getAniIndex()]);
			else
				animation = lightBanditArr[lb.getEnemyState()][lb.getAniIndex()];
			
			g.drawImage(animation, (int) lb.getHitbox().x - LIGHT_BANDIT_DRAWOFFSET_X - xLevelOffset, (int) lb.getHitbox().y - LIGHT_BANDIT_DRAWOFFSET_Y, LIGHT_BANDIT_WIDTH, LIGHT_BANDIT_HEIGHT, null);
			
			g.setColor(Color.RED);
			g.drawRect((int) lb.getHitbox().x - xLevelOffset, (int) lb.getHitbox().y, (int) lb.getHitbox().width, (int) lb.getHitbox().height);
		}
	}

	private void loadEnemyImgs() {
		lightBanditArr = new BufferedImage[4][8];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.LIGHT_BANDIT_SPRITE);
		for (int j = 0; j < lightBanditArr.length; j++) 
			for (int i = 0; i < lightBanditArr[j].length; i++) 
				lightBanditArr[j][i] = temp.getSubimage(i * LIGHT_BANDIT_WIDTH_DEFAULT, j * LIGHT_BANDIT_HEIGHT_DEFAULT, LIGHT_BANDIT_WIDTH_DEFAULT , LIGHT_BANDIT_HEIGHT_DEFAULT);
	}
}
