package GameStates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import Main.Game;
import Ui.MenuButton;
import Utils.LoadSave;

public class Menu extends State implements Statemethods {

	private MenuButton[] buttons = new MenuButton[3];
	private BufferedImage backgroundImage, backgroundImg2;
	private int menuX, menuY, menuWidth, menuHeight;

	public Menu(Game game) {
		super(game);
		loadButtons();
		loadBackground();
		backgroundImg2 = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG);
	}

	private void loadBackground() {
		backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
		menuWidth = (int) (backgroundImage.getWidth() * 0.95 * Game.SCALE);
		menuHeight = (int) (backgroundImage.getHeight() * 0.95 * Game.SCALE);
		menuX = Game.GAME_WIDTH / 2 - menuWidth / 2;
		menuY = (int) (10 * Game.SCALE);

	}

	private void loadButtons() {
		buttons[0] = new MenuButton(Game.GAME_WIDTH / 2, (int) (110 * Game.SCALE), 0, GameState.PLAYING);
		buttons[1] = new MenuButton(Game.GAME_WIDTH / 2, (int) (175 * Game.SCALE), 1, GameState.JOINING);
		buttons[2] = new MenuButton(Game.GAME_WIDTH / 2, (int) (240 * Game.SCALE), 2, GameState.QUIT);
	}

	@Override
	public void update() {
		for (MenuButton mButton : buttons)
			mButton.update();
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(backgroundImg2, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		g.drawImage(backgroundImage, menuX, menuY, menuWidth, menuHeight, null);
		for (MenuButton mButton : buttons)
			mButton.draw(g);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		for (MenuButton mButton : buttons) {
			if (isIn(e, mButton)) {
				mButton.setMousePressed(true);
				break;
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for (MenuButton mButton : buttons) {
			if (isIn(e, mButton)) {
				if (mButton.isMousePressed())
					mButton.applyGamestate();
				break;
			}
		}
		resetButtons();
	}

	private void resetButtons() {
		for (MenuButton mButton : buttons)
			mButton.resetBooleans();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		for (MenuButton mButton : buttons)
			mButton.setMouseOver(false);

		for (MenuButton mButton : buttons)
			if (isIn(e, mButton)) {
				mButton.setMouseOver(true);
				break;
			}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			GameState.state = GameState.PLAYING;

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
