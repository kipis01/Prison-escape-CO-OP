package Levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Main.Game;
import Utils.LoadSave;

public class LevelManager {

	private Game game;
	private BufferedImage[] levelSprite;

	public LevelManager(Game game) {
		this.game = game;
//		levelSprite = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		importOutsideSprites();
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
		g.drawImage(levelSprite[48], 0, 0, null);
	}

	public void update() {

	}
}
