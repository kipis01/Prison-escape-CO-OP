package Utils;

import java.awt.geom.Rectangle2D;

import Ui.GameOverOverlay;

import static Main.Game.TILES_SIZE;
import static Main.Game.GAME_HEIGHT;

public class HelpMethods {
	
	private static GameOverOverlay gameOverOverlay;

	public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
		float topLeftX = x;
		float topLeftY = y;
		float bottomRightX = x + width - 1;
		float bottomRightY = y + height - 1;
		
		return !IsSolid(topLeftX, topLeftY, lvlData) && !IsSolid(bottomRightX, bottomRightY, lvlData)
				&& !IsSolid(bottomRightX, topLeftY, lvlData) && !IsSolid(topLeftX, bottomRightY, lvlData);
	}

	//Check whether it is a tile and inside the game window
	private static boolean IsSolid(float x, float y, int[][] lvlData) {
		int maxWidth = lvlData[0].length * TILES_SIZE;
		if (x < 0 || x >= maxWidth)
			return true;
		if (y < 0 || y >= GAME_HEIGHT)
			return true;

		int xIndex = (int) (x / TILES_SIZE);
		int yIndex = (int) (y / TILES_SIZE);
		
		return IsTileSolid((int) xIndex, (int) yIndex, lvlData);
	}
	
	public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData ) {
		int value = lvlData[yTile][xTile];

		if (value >= 48 || value < 0 || value != 4)
			return true;
		return false;
	}

	public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
		int currentTile = (int) (hitbox.x / TILES_SIZE);
		if (xSpeed > 0) {
			// Right
			int tileXPos = currentTile * TILES_SIZE;
			int xOffset = (int) (TILES_SIZE - hitbox.width);
			return tileXPos + xOffset - 1;
		} else
			// Left
			return currentTile * TILES_SIZE;
	}

	public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
		int currentTile = (int) (hitbox.y / TILES_SIZE);
		if (airSpeed > 0) {
			// Falling - touching floor
			int tileYPos = (currentTile + 1) * TILES_SIZE;
			int yOffset = (int) (TILES_SIZE - hitbox.y % TILES_SIZE);
			return tileYPos - yOffset - 1;
		} else {
			// Jumping
			return currentTile * TILES_SIZE;
		}
	}

	public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		// Check pixel below bottom left and bottom right
		if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
			if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
				return false;
		return true;
	}
	
	public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
		if (xSpeed > 0)
			return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
		else
			return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
	}
	
	public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
		for(int i = 0; i < xEnd - xStart; i++)
			if (IsTileSolid(xStart + i, y, lvlData))
				return false;
		return true;
	}
	
	public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float firstHitbox, 
			Rectangle2D.Float secondHitbox, int yTile) {
		int firstXTile = (int) (firstHitbox.x / TILES_SIZE);
		int secondXTile = (int) (secondHitbox.x / TILES_SIZE);
		
		if (firstXTile > secondXTile)
			return IsAllTilesWalkable(secondXTile, firstXTile, yTile, lvlData);
		else
			return IsAllTilesWalkable(firstXTile, secondXTile, yTile, lvlData);

	}
}
