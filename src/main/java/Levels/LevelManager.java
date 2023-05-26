package Levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Main.Game;
import Utils.LoadSave;

public class LevelManager {

	private Game game;
	private BufferedImage[] levelSprite;
	private Level levelOne;

	public LevelManager(Game game) {
		this.game = game;
		importOutsideSprites();
		levelOne = new Level(LoadSave.GetLevelData());
	}

	private void importOutsideSprites() {
		BufferedImage imgBufferedImage = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		levelSprite = new BufferedImage[315]; // 21 * 15 (tilesX * tilesY)
		for (int j = 0; j < 15; j++)
			for (int i = 0; i < 21; i++) {
				int index = j * 21 + i;
				levelSprite[index] = imgBufferedImage.getSubimage(i * 24, j * 24, 24, 24);
			}

	}

	public void draw(Graphics g) {

		for (int j = 0; j < Game.TILES_IN_HEIGHT; j++)
			for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
				int index = levelOne.getSpriteIndex(i, j);
				g.drawImage(levelSprite[index], game.TILES_SIZE * i, Game.TILES_SIZE * j, Game.TILES_SIZE,
						Game.TILES_SIZE, null);
			}

	}

	public void update() {

	}

	public Level getCurrentLeve() {
		return levelOne;
	}
}
