package Entities.NPC;

import Entities.Player;
import Levels.LevelManager;
import Main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PrisonGuard {
	private int movementType;
	private float x, y; // Position variables
	private float speed = 2.0f; // Movement speed
	private boolean jumping = false; // Flag to track if the guard is jumping
	private float jumpForce = -6.0f; // The force to apply when jumping
	private float gravity = 0.4f; // The gravity value to simulate downward movement
	private float yVelocity = 0.0f; // The vertical velocity of the guard
	private int width; // Width of the guard
	private int height; // Height of the guard

	private static BufferedImage enemyImage;
	private LevelManager levelManager;
	private Player player; // Reference to the player object
	private static final int TYPE_HORIZONTAL = 1;
	private static final int TYPE_VERTICAL = 2;
	private int maxHP; // Maximum hit points of the guard
	private int currentHP; // Current hit points of the guard

	// Type 1 movement variables
	private boolean movingRight = true; // Flag to track horizontal movement direction

	// Type 2 movement variables
	private Rectangle2D.Float hitbox; // Hitbox for c
	private boolean movingUp = true; // Flag to track vertical movement direction
	private float prevY;
	private float prevX;

	public void setPlayer(Player player) {
		this.player = player;
	}

	// Rest of the code...

	public PrisonGuard(float x, float y, int width, int height, int movementType) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.hitbox = new Rectangle2D.Float(x, y, width, height);
		this.movementType = movementType;
		try {
			enemyImage = ImageIO.read(new File("C:/Users/maris/IdeaProjects/Prison-escape-CO-OP/src/main/resources/download.webp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.maxHP = maxHP;
		this.currentHP = maxHP;
	}
	public void setLevelManager(LevelManager levelManager) {
		this.levelManager = levelManager;
	}
	private void updateHitbox() {
		// Calculate the difference between current position and previous position
		float dx = x - prevX;
		float dy = y - prevY;

		// Update hitbox position based on the difference
		hitbox.x += dx;
		hitbox.y += dy;
	}


	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}
	public void update() {
		move();
		updateHitbox();
		checkPlayerCollision(); // Check for collision with the player
		attackPlayerIfClose(); //
		applyGravity();
	}

	public void render(Graphics g, int xLevelOffset) {
		g.drawImage(enemyImage, (int) (x - xLevelOffset), (int) y, width, height, null);

		g.setColor(Color.RED);
		g.drawRect((int) (x - xLevelOffset), (int) y, width, height);

		g.setColor(Color.WHITE);
		g.drawString("Prison Guard", (int) (x - xLevelOffset), (int) y - 10);
	}

	private long lastMoveTime; // Variable to store the last move time

	private void move() {
		// Check if enough time has passed to move
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastMoveTime >= 100) { // Move every 3 seconds
			// Update movement based on the movement type
			switch (movementType) {
				case 1: // Type 1 movement: Move horizontally back and forth
					moveHorizontally();
					break;

				case 2: // Type 2 movement: Move vertically up and down
					moveVertically();
					break;

				default:
					// Default movement logic: Move horizontally
					moveHorizontally();
					break;
			}

			lastMoveTime = currentTime; // Update last move time
		}
	}

	private void moveHorizontally() {
		if (movingRight) {
			prevX = x; // Store the previous position
			x += speed + (Math.random() * 2.0f); // Move right with random speed variation
		} else {
			prevX = x; // Store the previous position
			x -= speed + (Math.random() * 2.0f); // Move left with random speed variation
		}

		// Rest of the code...
	}

	private void moveVertically() {
		if (movingUp) {
			prevY = y; // Store the previous position
			y -= speed + (Math.random() * 2.0f); // Move up with random speed variation
		} else {
			prevY = y; // Store the previous position
			y += speed + (Math.random() * 2.0f); // Move down with random speed variation
		}
	}





	private void applyGravity() {
		// Apply gravity to the guard's vertical velocity
		yVelocity += gravity;
		// Limit the maximum vertical velocity to prevent excessive falling speed
		yVelocity = Math.min(yVelocity, 6.0f);

		// Update the guard's y-position based on the vertical velocity
		y += yVelocity;
	}
	private void attackPlayerIfClose() {
		if (player != null) {
			float distanceX = Math.abs(player.getX() - x);
			float distanceY = Math.abs(player.getY() - y);

			// Adjust the attack threshold as needed
			float attackThreshold = 50.0f;

			if (distanceX < attackThreshold && distanceY < attackThreshold) {
				// Player is within attack range, initiate attack
				player.takeDamage();
			}
		}
	}

	private void initiateAttack() {
		// Perform the necessary actions to simulate an attack on the player
		// For example, you can play an attack animation or perform other gameplay-related logic
		// In this case, we'll simply print a message to the console
		System.out.println("Guard initiated an attack on the player!");
	}

	private void checkPlayerCollision() {
		if (player != null && hitbox.intersects(player.getHitbox())) {
			// Handle collision with the player
			player.takeDamage();
		}
	}
	private void attackPlayer() {
		if (player != null) {
			player.takeDamage();
		}
	}
	public void takeDamage(int damage) {
		currentHP -= damage;

		if (currentHP <= 0) {
			// Guard is defeated
			currentHP = 0; // Ensure HP doesn't go below 0
			System.out.println("Guard defeated. Current HP: " + currentHP);
			// Implement guard defeat logic here
		} else {
			System.out.println("Guard took damage. Current HP: " + currentHP);
		}
	}
	public boolean isHit(Rectangle2D.Float hitbox) {
		return this.hitbox.intersects(hitbox);
	}

	public void loadLevelData(int[][] levelData) {
		// Calculate the guard's position in the level data grid
		int gridX = (int) (x / Game.TILES_SIZE);
		int gridY = (int) (y / Game.TILES_SIZE);

		// Check if the guard is standing on a solid tile
		if (isSolidTile(levelData, gridX, gridY + 1)) {
			// Reset vertical velocity
			yVelocity = 0;
			// Snap guard's position to the top of the tile
			y = gridY * Game.TILES_SIZE;
			jumping = false;
		}

		// Store the level data for future reference, if needed
		// ...
	}

	public void checkTileCollision(int[][] levelData) {
		// Calculate the guard's position in the level data grid
		int gridX = (int) (x / Game.TILES_SIZE);
		int gridY = (int) (y / Game.TILES_SIZE);

		// Check if the guard is standing on a solid tile
		if (isSolidTile(levelData, gridX, gridY + 1)) {
			// Reset vertical velocity
			yVelocity = 0;
			// Snap guard's position to the top of the tile
			y = gridY * Game.TILES_SIZE;
			jumping = false;
		}
	}

	private boolean isSolidTile(int[][] levelData, int x, int y) {
		if (x < 0 || x >= levelData[0].length || y < 0 || y >= levelData.length) {
			return false; // Return false for out-of-bounds coordinates
		}

		int tile = levelData[y][x];
		// Assuming tile value 1 represents solid tile
		return tile == 1;
	}


}
