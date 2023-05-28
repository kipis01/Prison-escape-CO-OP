package Entities;

import static Main.Game.SCALE;
import static Utils.Constants.PlayerConstants.ATTACK;
import static Utils.Constants.PlayerConstants.FALL;
import static Utils.Constants.PlayerConstants.GetSpriteAmount;
import static Utils.Constants.PlayerConstants.IDLE;
import static Utils.Constants.PlayerConstants.JUMP;
import static Utils.Constants.PlayerConstants.RUN;
import static Utils.HelpMethods.CanMoveHere;
import static Utils.HelpMethods.GetEntityXPosNextToWall;
import static Utils.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;
import static Utils.HelpMethods.IsEntityOnFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import GameStates.Playing;
import static Utils.Constants.Directions.*;
import static Utils.Constants.PlayerConstants.*;
import static Utils.HelpMethods.*;
import static Utils.Constants.*;
import static Utils.Constants.EnemyConstants.DEATH;
import static Utils.Constants.EnemyConstants.HIT;
import static Main.Game.SCALE;
import Networking.NetWorker;
import Networking.PlayerData;
import Utils.FlipImage;
import Utils.LoadSave;

public class Player extends Entity {

	// Arrays
	private BufferedImage[][] animations;
	private int[][] lvlData;
  
	//Animations
	private boolean moving = false, attacking = false;
	private boolean left, right, jump, knockback, sprintRight, sprintLeft;
	
	//Player position
	private float xDrawOffset = 18 * SCALE;
	private float yDrawOffset = 24 * SCALE;
	private boolean defaultDirection = true;

	// Player physics
	private float playerSprintSpeed = 1.2f * SCALE;
	private float jumpSpeed = -2f * SCALE;
	private float knockbackSpeed = -0.5f * SCALE;
	private float fallSpeedAfterCollision = 0.5f * SCALE;
	
	// Player status
	private int healthBarLevelWidth = 50;
	private int healthWidth = healthBarLevelWidth;
	private boolean attackChecked;
	private Playing playing;

	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		loadAnimations();
		this.state = IDLE;
		this.maxHealth = 100;
		this.currentHealth = maxHealth;
		this.walkSpeed = SCALE * 0.6f;
		initHitbox(18, 31);
		initHealthbar(18, 31);
		initAttackBox();
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (20 * SCALE), (int) (20 * SCALE));
	}

	public void update() {
		updateHealthBar();
		
		if (currentHealth <= 0) {
			if (state != DEAD) {
				state = DEAD;
				aniTick = 0;
				aniIndex = 0;
				//TODO: Not working at the moment
//				playing.setPlayerDying(true);
			} else if (aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
				playing.setGameOver(true);
			} else
				updateAnimationTick();
			return;
		}
		updateAttackBox();
		updatePos();

		if (attacking)
			checkAttack();

		updateAnimationTick();
		setAnimation();
	}

	private void checkAttack() {
		if (attackChecked || aniIndex != 3 )
			return;
		attackChecked = true;
		playing.checkEnemyHit(attackBox);

	}

	private void updateAttackBox() {
		if (right) {
			attackBox.x = hitbox.x + hitbox.width + 1;
		} else if (left) {
			attackBox.x = hitbox.x - hitbox.width - 1;
		}
		attackBox.y = hitbox.y + (SCALE * 5);
	}

	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarLevelWidth);
	}

	public void render(Graphics g, int levelOffset, NetWorker client, PlayerData playerData, int playerId,
			boolean isPlayerTwoConnected) {
		BufferedImage animation;

		if (!defaultDirection && state != DEAD)
			animation = FlipImage.flipImageHorizontally(animations[state][aniIndex]);
		else
			animation = animations[state][aniIndex];

		//g.drawImage(animation, (int) (hitbox.x - xDrawOffset) - levelOffset, (int) (hitbox.y - yDrawOffset), width,
	//			height, null);
		
		drawHealthbar(g, levelOffset, healthWidth);
		
		// Debugging
//		drawHitbox(g, levelOffset);
//		drawAttackBox(g, levelOffset);
	
		int xLoc = (int) (hitbox.x - xDrawOffset) - levelOffset;
		int yLoc = (int) (hitbox.y - yDrawOffset);

		g.drawImage(animation, xLoc, yLoc, width, height, null);

		playerData.xLoc = xLoc + levelOffset;
		playerData.yLoc = yLoc;
		playerData.playerId = playerId;
		playerData.playerAction = state;
		playerData.aniIndex = aniIndex;
		playerData.defaultDirection = defaultDirection;

		try {
			if (isPlayerTwoConnected) {
				client.SendPacket(playerData);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		//drawHitbox(g, levelOffset);
		//drawAttackBox(g, levelOffset);
	}

	public void renderPlayerTwo(Graphics g, int levelOffset, PlayerData playerData) {
		BufferedImage animation;

		if (!playerData.defaultDirection)
			animation = FlipImage.flipImageHorizontally(animations[playerData.playerAction][playerData.aniIndex]);
		else
			animation = animations[playerData.playerAction][playerData.aniIndex];

		g.drawImage(animation, playerData.xLoc, playerData.yLoc, width, height, null);
	}

	protected void drawAttackBox(Graphics g, int levelOffsetX) {
		g.setColor(Color.red);
		g.drawRect((int) attackBox.x - levelOffsetX, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
	}

public void addKnockback(int value) {
		float xSpeed = 30f;
		if (value == RIGHT) {
			knockback();
			updateXPos(xSpeed);
		} else if (value == LEFT) {
			knockback();
			updateXPos(-xSpeed);
		}
}

	public void changeHealth(int value) {
		currentHealth += value;
		
		if (currentHealth <= 0)

		if(currentHealth <= 0) {
			currentHealth = 0;
		} else if (currentHealth >= maxHealth)
			currentHealth = maxHealth;
	}

	private void loadAnimations() {
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

		animations = new BufferedImage[7][8];

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
		sprintRight = false;
		sprintLeft = false;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}	protected float fallSpeed;

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(state)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
			}
		}
	}

	private void setAnimation() {
		int startAni = state;

		if (moving)
			state = RUN;
		else
			state = IDLE;

		if (inAir) {
			if (airSpeed < 0)
				state = JUMP;
			else
				state = FALL;
		}

		if (attacking) {
			state = ATTACK;
			if(startAni != ATTACK) {
				aniIndex = 2;
				aniTick = 0;
				return;
			}
		}
		
		if (startAni != state)
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

		// Right and Left movement
		if (left && !sprintLeft) {
			xSpeed -= walkSpeed;
			resetSprint();
			defaultDirection = false;
		}

		if (right && !sprintRight) {
			xSpeed = walkSpeed;
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
	
		if (knockback)
			knockback();
		
		//Up and down movement

		// Up and down movement
		if (!inAir)
			if (!IsEntityOnFloor(hitbox, lvlData))
				inAir = true;

		if (inAir) {
			if (hitbox.y > 750) {
				playing.setGameOver(true);
			}
			
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += GRAVITY;
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
	
	private void knockback() {
		if (inAir)
			return;
		inAir = true;
		airSpeed = knockbackSpeed;
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

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}
	public void setSprint (boolean sprint) {
		if (defaultDirection == true) {
			this.sprintRight = sprint;
		} else
			this.sprintLeft = sprint;
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
		state = IDLE;
		currentHealth = maxHealth;

		hitbox.x = x;
		hitbox.y = y;

		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}

}
