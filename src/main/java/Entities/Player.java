package Entities;

import Levels.LevelManager;
import Main.Game;
import Utils.FlipImage;
import Utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import GameStates.Playing;

import static Utils.Constants.PlayerConstants.*;
import static Utils.HelpMethods.*;
import static Main.Game.SCALE;

public class Player extends Entity {

	//Arrays
	private BufferedImage[][] animations;
	private int[][] lvlData;
	
	//Animations
	private int aniTick, aniIndex, aniSpeed = 20;
	private int playerAction = IDLE;
	private boolean moving = false, attacking = false;
	private boolean left, up, right, down, jump, sprintRight, sprintLeft;
	
	//Player position
	private float xDrawOffset = 18 * SCALE;
	private float yDrawOffset = 24 * SCALE;
	private boolean defaultDirection = true;

	// Player physics
	private float playerSpeed = 0.6f * SCALE;
	private float playerSprintSpeed = 1.2f * SCALE;
	private float airSpeed = 0f;
	private float gravity = 0.04f * SCALE;
	private float jumpSpeed = -2f * SCALE;
	private float fallSpeedAfterCollision = 0.5f * SCALE;
	private boolean inAir = true;
	
	// Player status
	private float healthBarScale = 1f;
	
	private BufferedImage healthBarBG;
	private BufferedImage healthBarOutline;
	private BufferedImage healthBarLevel;
	
	private int healthBarBGWidth = (int) (271 * healthBarScale);
	private int healthBarBGHeight = (int) (36 * healthBarScale);
	private int healthBarBGX = (int) (40 * healthBarScale);
	private int healthBarBGY = (int) (40 * healthBarScale);
	
	private int healthBarOutlineWidth = (int) (280 * healthBarScale);
	private int healthBarOutlineHeight = (int) (53 * healthBarScale);
	private int healthBarOutlineX = (int) (32 * healthBarScale);
	private int healthBarOutlineY = (int) (31 * healthBarScale);
	
	private int healthBarLevelWidth = (int) (266 * healthBarScale);
	private int healthBarLevelHeight = (int) (29 * healthBarScale);
	private int healthBarLevelX = (int) (42 * healthBarScale);
	private int healthBarLevelY = (int) (42 * healthBarScale);
	
	private int maxHealth = 100;
	private int currentHealth = maxHealth;
	private int healthWidth = healthBarLevelWidth;
	
	// Attack Hitbox
	private Rectangle2D.Float attackBox;
	
	private boolean attackChecked;
	private Playing playing;
	
	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		loadAnimations();
		initHitbox(x, y, 18 * SCALE, 31 * SCALE);
		initAttackBox();
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int)(20 * SCALE), (int)(20 * SCALE));
	}

	public void update() {
		if (currentHealth <- 0) {
			playing.setGameOver(true);
			return;
		}
		
		updateHealthBar();
		updateAttackBox();
		updatePos();
		
		if(attacking)
			checkAttack();
		
		updateAnimationTick();
		setAnimation();
	}

	private void checkAttack() {
		if (attackChecked || aniIndex != 1)
			return;
		attackChecked = true;
		playing.checkEnemyHit(attackBox);
		
	}

	private void updateAttackBox() {
		if(right) {
			attackBox.x = hitbox.x + hitbox.width + 1;
		} else if (left) {
			attackBox.x = hitbox.x - hitbox.width - 1;
		}
		attackBox.y = hitbox.y + (SCALE * 5);
	}

	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarLevelWidth);
	}

	public void render(Graphics g, int levelOffset) {
		BufferedImage animation;

		if (!defaultDirection)
			animation = FlipImage.flipImageHorizontally(animations[playerAction][aniIndex]);
		else
			animation = animations[playerAction][aniIndex];

		g.drawImage(animation, (int) (hitbox.x - xDrawOffset) - levelOffset, (int) (hitbox.y - yDrawOffset), width,
				height, null);
		drawHitbox(g, levelOffset);
		drawAttackBox(g, levelOffset);
		
		drawUi(g);
	}

	private void drawAttackBox(Graphics g, int levelOffsetX) {
		g.setColor(Color.red);
		g.drawRect((int) attackBox.x - levelOffsetX, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
	}

	private void drawUi(Graphics g) {
		g.setColor(Color.gray);
		g.fillRect(28, 28, healthBarLevelWidth + 4, healthBarLevelHeight + 4);
		g.setColor(Color.red);
		g.fillRect(30, 30, healthWidth, healthBarLevelHeight);
		
		//TODO: Implement health change with the images
//		BufferedImage croppedImage = healthBarLevel.getSubimage(0, 0, healthBarLevelWidth - healthWidth, healthBarLevelHeight);
		
//		g.drawImage(healthBarBG, healthBarBGX, healthBarBGY, healthBarBGWidth, healthBarBGHeight, null);
//		g.drawImage(croppedImage, healthBarLevelX, healthBarLevelY, healthBarLevelWidth, healthBarLevelHeight, null);
//		g.drawImage(healthBarOutline, healthBarOutlineX, healthBarOutlineY, healthBarOutlineWidth, healthBarOutlineHeight, null);
	}
	
	public void changeHealth(int value) {
		currentHealth += value;
		
		if(currentHealth <= 0) {
			currentHealth = 0;
			//gameover();
		} else if (currentHealth >= maxHealth)
			currentHealth = maxHealth;
	}

	
	private void loadAnimations() {
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

		animations = new BufferedImage[7][10];

		for (int j = 0; j < animations.length; j++)
			for (int i = 0; i < animations.length; i++)
				animations[j][i] = img.getSubimage(i * 56, j * 56, 56, 56);

		healthBarBG = LoadSave.GetSpriteAtlas(LoadSave.HEALTH_BAR_BG);
		healthBarLevel = LoadSave.GetSpriteAtlas(LoadSave.HEALTH_BAR);
		healthBarOutline = LoadSave.GetSpriteAtlas(LoadSave.HEALTH_BAR_OUTLINE);
		
	}

	public void loadLevelData(int[][] lvlData) {
		this.lvlData = lvlData;
	}

	public void resetDirBooleans() {
		left = false;
		right = false;
		up = false;
		down = false;
		sprintRight = false;
		sprintLeft = false;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= aniSpeed) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(playerAction)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
			}
		}
	}
	
	private void setAnimation() {
		int startAni = playerAction;

		if (moving)
			playerAction = RUN;
		else
			playerAction = IDLE;

		if (inAir) {
			if (airSpeed < 0)
				playerAction = JUMP;
			else
				playerAction = FALL;
		}

		if (attacking) {
			playerAction = ATTACK;
			if(startAni != ATTACK) {
				aniIndex = 2;
				aniTick = 0;
				return;
			}
		}
		
		if (startAni != playerAction)
			resetAniTick();
	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}

	private void updatePos() {
		moving = false;
		
		if (jump)
			jump();

		if (!inAir)
			if ((!left && !right) || (left && right))
				return;

		float xSpeed = 0;

		//Right and Left movement
		if (left && !sprintLeft) {
			xSpeed -= playerSpeed;
			resetSprint();
			defaultDirection = false;
		}

		if (right && !sprintRight) {
			xSpeed = playerSpeed;
			resetSprint();
			defaultDirection = true;
		}
		
		if (left && sprintLeft) {
			xSpeed -= playerSprintSpeed;
			sprintRight = false;
			defaultDirection = false;
		}
		
		if (right && sprintRight) {
			xSpeed = playerSprintSpeed;
			sprintLeft = false;
			defaultDirection = true;
		}
	
		//Up and down movement
		
		if (!inAir)
			if (!IsEntityOnFloor(hitbox, lvlData))
				inAir = true;

		if (inAir) {
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += gravity;
				updateXPos(xSpeed);
			} else {
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}

		} else
			updateXPos(xSpeed);
		moving = true;
	}

	private void jump() {
		if (inAir)
			return;
		inAir = true;
		airSpeed = jumpSpeed;
	}

	public void resetSprint() {
		sprintLeft = false;
		sprintRight = false;
	}
	
	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
	}

	private void updateXPos(float xSpeed) {
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
		}
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}
	
	public void setSprint (boolean sprint) {
		if (defaultDirection == true) {
			this.sprintRight = sprint;
		} else this.sprintLeft = sprint;
	}
	
	public void setJump(boolean jump) {
		this.jump = jump;
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		playerAction = IDLE;
		currentHealth = maxHealth;
		
		hitbox.x = x;
		hitbox.y = y;
		
		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}

}
