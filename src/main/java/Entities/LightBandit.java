package Entities;

import static Utils.Constants.Directions.LEFT;
import static Utils.Constants.EnemyConstants.*;
import static Utils.HelpMethods.CanMoveHere;
import static Utils.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;
import static Utils.HelpMethods.IsEntityOnFloor;
import static Utils.HelpMethods.IsFloor;

import Main.Game;
public class LightBandit extends Enemy {

	public LightBandit(float x, float y) {
		super(x, y, LIGHT_BANDIT_WIDTH, LIGHT_BANDIT_HEIGHT, LIGHT_BANDIT);
		initHitbox(x, y, (int) (20 * Game.SCALE), (int) (42 * Game.SCALE));
	}
	
	public void update(int[][] lvlData, Player player) {
		updateMove(lvlData, player);
		updateAnimationTick();
	}
	
	public int getWalkDir() {
		return this.walkDir;
	}
	
	public void updateMove(int[][] lvlData, Player player) {
		if (firstUpdate) {
			firstUpdateCheck(lvlData);
		}
		
		if (inAir) {
			updateInAir(lvlData);
		} else {
			switch(enemyState) {
			case IDLE:
				newState(RUN);
				break;
			case RUN:
				if(canSeePlayer(lvlData, player))
					turnTowardsPlayer(player);
				if (isPlayerCloseForAttack(player))
					newState(ATTACK);
				
				move(lvlData);
				break;
			}
		}
	}
}
