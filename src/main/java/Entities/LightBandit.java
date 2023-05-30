package Entities;

import static Main.Game.SCALE;
import static Utils.Constants.Directions.LEFT;
import static Utils.Constants.Directions.RIGHT;
import static Utils.Constants.EnemyConstants.ATTACK;
import static Utils.Constants.EnemyConstants.HIT;
import static Utils.Constants.EnemyConstants.IDLE;
import static Utils.Constants.EnemyConstants.LIGHT_BANDIT;
import static Utils.Constants.EnemyConstants.LIGHT_BANDIT_HEIGHT;
import static Utils.Constants.EnemyConstants.LIGHT_BANDIT_WIDTH;
import static Utils.Constants.EnemyConstants.RUN;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class LightBandit extends Enemy implements Serializable {

	public LightBandit(float x, float y) {
		super(x, y, LIGHT_BANDIT_WIDTH, LIGHT_BANDIT_HEIGHT, LIGHT_BANDIT);
		initHitbox(20, 41);
		initAttackBox();
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (20 * SCALE), (int) (20 * SCALE));
	}

	public void update(int[][] lvlData, Player player) {
		updateBehaviour(lvlData, player);
		updateAnimationTick();
		updateAttackBox();
	}

	private void updateAttackBox() {
		if (walkDir == RIGHT) {
			attackBox.x = hitbox.x + hitbox.width + 1;
		} else if (walkDir == LEFT) {
			attackBox.x = hitbox.x - hitbox.width - 1;
		}
		attackBox.y = hitbox.y + (SCALE * 5);
	}

	public int getWalkDir() {
		return this.walkDir;
	}

	public int getCurrentHealth() {
		return this.currentHealth;
	}

	public void drawAttackBox(Graphics g, int xLevelOffset) {
		g.setColor(Color.blue);
		g.drawRect((int) attackBox.x - xLevelOffset, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
	}

	public void updateBehaviour(int[][] lvlData, Player player) {
		if (firstUpdate) {
			firstUpdateCheck(lvlData);
		}

		if (inAir) {
			updateInAir(lvlData);
		} else {
			switch (state) {
			case IDLE:
				newState(RUN);
				break;
			case RUN:
				if (canSeePlayer(lvlData, player)) {
					turnTowardsPlayer(player);
					if (isPlayerCloseForAttack(player))
						newState(ATTACK);
				}
				move(lvlData);
				break;
			case ATTACK:
				if (aniIndex == 0)
					attackChecked = false;
				if (aniIndex == 4 && !attackChecked)
					checkPlayerHit(attackBox, player);
				break;
			case HIT:
				break;
			}
		}
	}
}
