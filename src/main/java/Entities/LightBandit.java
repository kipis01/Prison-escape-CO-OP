package Entities;

import static Main.Game.SCALE;
import static Utils.Constants.Directions.LEFT;
import static Utils.Constants.EnemyConstants.*;
import static Utils.HelpMethods.CanMoveHere;
import static Utils.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;
import static Utils.HelpMethods.IsEntityOnFloor;
import static Utils.HelpMethods.IsFloor;
import static Utils.Constants.Directions.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import Main.Game;
public class LightBandit extends Enemy {

	public LightBandit(float x, float y) {
		super(x, y, LIGHT_BANDIT_WIDTH, LIGHT_BANDIT_HEIGHT, LIGHT_BANDIT);
		initHitbox(20, 41);
		initAttackBox();
	}
	
	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int)(20 * SCALE), (int)(20 * SCALE));
	}

	public void update(int[][] lvlData, Player player) {
		updateBehaviour(lvlData, player);
		updateAnimationTick();
		updateAttackBox();
	}
	
	private void updateAttackBox() {
		if(walkDir == RIGHT) {
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
	
	public void updateBehaviour(int[][] lvlData, Player player) {
		if (firstUpdate) {
			firstUpdateCheck(lvlData);
		}
		
		if (inAir) {
			updateInAir(lvlData);
		} else {
			switch(state) {
			case IDLE:
				newState(RUN);
				break;
			case RUN:
				if(canSeePlayer(lvlData, player)) {
					turnTowardsPlayer(player);
					if (isPlayerCloseForAttack(player))
						newState(ATTACK);
				}
				move(lvlData);
				break;
			case ATTACK:
				if(aniIndex == 0)
					attackChecked = false;
				if(aniIndex == 4 && !attackChecked)
					checkPlayerHit(attackBox, player);
				break;
			case HIT:
				break;
			}
		}
	}
}
