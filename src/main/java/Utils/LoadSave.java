package Utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entities.LightBandit;
import Main.Game;

import static Utils.Constants.EnemyConstants.LIGHT_BANDIT;

public class LoadSave {

	public static final String PLAYER_ATLAS = "player_sprites.png";
	public static final String LIGHT_BANDIT_SPRITE = "LightBandit.png";
	public static final String HEAVY_BANDIT_SPRITE = "HeavyBandit.png";
	public static final String LEVEL_ATLAS = "oak_woods_tileset.png";
	public static final String LEVEL_ONE_DATA = "level_one_data.png";
	public static final String MENU_BUTTONS = "button_atlas.png";
	public static final String MENU_BACKGROUND = "menu_background.png";
	public static final String PAUSE_BACKGROUND = "pause_menu.png";
	public static final String SOUND_BUTTONS = "sound_button.png";
	public static final String URM_BUTTONS = "urm_buttons.png";
	public static final String VOLUME_BUTTONS = "volume_buttons.png";
	public static final String MENU_BACKGROUND_IMG = "background_layer_1.png";
	public static final String PLAYING_BACKGROUND_IMG = "background_layer_1.png";
	public static final String PLAYING_BACKGROUND_IMG_LAYER_2 = "background_layer_2.png";
	public static final String PLAYING_BACKGROUND_IMG_LAYER_3 = "background_layer_3.png";

	// Get the image file of an entity
	public static BufferedImage GetSpriteAtlas(String fileName) {
		BufferedImage img = null;
		InputStream is = LoadSave.class.getResourceAsStream("/" + fileName); // Input stream

		try {
			img = ImageIO.read(is);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return img;
	}

	public static ArrayList<LightBandit> GetLightBandits() {
		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
		ArrayList<LightBandit> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
	
				if (value == LIGHT_BANDIT)
					list.add(new LightBandit(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
			}
		}
		return list;
	}
	
	public static int[][] GetLevelData() {
		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
		int[][] lvlData = new int[img.getHeight()][img.getWidth()];

		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getRed();
				if (value > 21)
					value = 0;

				lvlData[j][i] = color.getRed();
			}

		return lvlData;
	}
}
