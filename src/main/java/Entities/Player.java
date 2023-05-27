package Entities;

import Entities.NPC.PrisonGuard;
import Levels.LevelManager;
import Main.Game;
import Utils.FlipImage;
import Utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Utils.Constants.PlayerConstants.*;
import static Utils.HelpMethods.*;

public class Player extends Entity {

	private BufferedImage[][] animations;

	private int aniTick, aniIndex, aniSpeed = 20;
	private int playerAction = IDLE;
	private boolean moving = false, attacking = false;
	private boolean left, up, right, down, jump;
	private float playerSpeed = 1.0f * Game.SCALE;
	private int[][] lvlData;
	private float xDrawOffset = 18 * Game.SCALE;
	private float yDrawOffset = 24 * Game.SCALE;

	private boolean defaultDirection = true;

	// Jumping / Gravity
	private float airSpeed = 0f;
	private float gravity = 0.04f * Game.SCALE;
	private float jumpSpeed = -2f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
	private boolean inAir = true;
	private int health; // Player's health

	private LevelManager levelManager;
	private PrisonGuard guard;
	public Player(float x, float y, int width, int height, LevelManager levelManager) {
		super(x, y, width, height);
		loadAnimations();
		initHitbox(x, y, 18 * Game.SCALE, (int) 31 * Game.SCALE);
		this.health = 100; // Initialize health to 100
		this.levelManager = levelManager; // Set the levelManager instance
	}



	public void update() {
		updatePos();
		updateAnimationTick();
		setAnimation();
		if (attacking && guard != null && guard.isHit(hitbox)) {
			guard.takeDamage(10); // Inflict 10 damage on the guard
		}
		if (guard != null && hitbox.intersects(guard.getHitbox())) {
			System.out.println("Player collided with PrisonGuard!");
			// Perform actions when player collides with the guard
		}
	}



	public void render(Graphics g, int levelOffset) {
		BufferedImage animation;

		if (!defaultDirection)
			animation = FlipImage.flipImageHorizontally(animations[playerAction][aniIndex]);
		else
			animation = animations[playerAction][aniIndex];

		g.drawImage(animation, (int) (hitbox.x - xDrawOffset) - levelOffset, (int) (hitbox.y - yDrawOffset), width,
				height, null);
		drawHitbox(g);

	}
	public void takeDamage() {
		health -= 10; // Reduce health by 10 when taking damage
		System.out.println("Player took damage. Current health: " + health);
		if (health <= 0) {
			// Player is defeated
			System.out.println("Player is defeated. Game over!");
			// Implement game over logic here
		}
	}
	private void loadAnimations() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

		animations = new BufferedImage[7][10];

		for (int j = 0; j < animations.length; j++)
			for (int i = 0; i < animations.length; i++)
				animations[j][i] = img.getSubimage(i * 56, j * 56, 56, 56);

	}

	public void loadLevelData(int[][] lvlData) {
		this.lvlData = lvlData;
	}

	public void resetDirBooleans() {
		left = false;
		right = false;
		up = false;
		down = false;
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
			}

		}

	}
	public void setGuard(PrisonGuard guard) {
		this.guard = guard;
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

		if (attacking)
			playerAction = ATTACK;

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

//		if (!left && !right && !inAir)
//			return;

		if (!inAir)
			if ((!left && !right) || (left && right))
				return;

		float xSpeed = 0;

		if (left) {
			xSpeed -= playerSpeed;
			defaultDirection = false;
		}

		if (right) {
			xSpeed += playerSpeed;
			defaultDirection = true;
		}

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

	public void setJump(boolean jump) {
		this.jump = jump;
	}
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

}
