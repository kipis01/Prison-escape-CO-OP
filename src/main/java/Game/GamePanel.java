package Game;

import static Utils.Constants.Directions.DOWN;
import static Utils.Constants.Directions.LEFT;
import static Utils.Constants.Directions.RIGHT;
import static Utils.Constants.Directions.UP;
import static Utils.Constants.PlayerConstants.GetSpriteAmount;
import static Utils.Constants.PlayerConstants.IDLE;
import static Utils.Constants.PlayerConstants.RUN;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;

public class GamePanel extends JPanel {

	private MouseInputs mouseInputs;
	private float xDelta = 100, yDelta = 100;
	private BufferedImage img;
	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 28;
	private int playerAction = IDLE;
	private int playerDir = -1;
	private boolean moving = false;

	public GamePanel() {

		importImg();
		loadAnimations();

		// KeyboardInputs extends KeyListener
		// Pass this class to Keyboard inputs to allow access to this class
		addKeyListener(new KeyboardInputs(this));

		setPanelSize();

		mouseInputs = new MouseInputs(this);
		addMouseListener(mouseInputs);
		addMouseMotionListener(mouseInputs);
	}

	private void loadAnimations() {
		animations = new BufferedImage[7][10];

		for (int j = 0; j < animations.length; j++)
			for (int i = 0; i < animations.length; i++)
				animations[j][i] = img.getSubimage(i * 50, j * 37, 50, 37);

	}

	private void importImg() {
		InputStream is = getClass().getResourceAsStream("/player_sprites.png"); // Input stream

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

	}

	private void setPanelSize() {
		Dimension size = new Dimension(1280, 800);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
	}

	public void setDirection(int direction) {
		this.playerDir = direction;
		this.moving = true;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= aniSpeed) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(playerAction))
				aniIndex = 0;
		}

	}

	private void setAnimation() {
		if (moving)
			playerAction = RUN;
		else
			playerAction = IDLE;

	}

	private void updatePos() {
		if (moving) {
			switch (playerDir) {
			case LEFT:
				xDelta -= 3;
				break;
			case UP:
				yDelta -= 3;
				break;
			case RIGHT:
				xDelta += 3;
				break;
			case DOWN:
				yDelta += 3;
				break;

			default:
				break;
			}
		}

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		updateAnimationTick();

		setAnimation();
		updatePos();

		// Buffered images can be drawn by sections not entire image

		g.drawImage(animations[playerAction][aniIndex], (int) xDelta, (int) yDelta, 256, 160, null);

	}

}
