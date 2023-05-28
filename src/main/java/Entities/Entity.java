package Entities;

import static Main.Game.SCALE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

	protected float x, y;
	protected int width, height;
	protected Rectangle2D.Float hitbox;
	protected Rectangle2D.Float healthbar;
	protected int aniTick, aniIndex;
	protected int state;
	protected float airSpeed;
	protected boolean inAir = true;
	protected int maxHealth;
	protected int currentHealth;
	protected Rectangle2D.Float attackBox;
	protected float walkSpeed;
	
	public Entity(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	//Draws the hitbox over the entity (for debugging purposes)
	protected void drawHitbox(Graphics g, int xLevelOffset) {
		g.setColor(Color.BLACK);
		g.drawRect((int) hitbox.x - xLevelOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}
	
	protected void drawHealthbar(Graphics g, int xLevelOffset, int healtWidth) {
		g.setColor(Color.black);
		g.fillRect((int) hitbox.x - xLevelOffset, (int) hitbox.y - 30, 54, 9);
		
		g.setColor(Color.red);
		g.fillRect((int) hitbox.x + 2 - xLevelOffset, (int) hitbox.y - 28, healtWidth, 5);
	}
	
	protected void drawAttackBox(Graphics g, int xLevelOffset) {
		g.setColor(Color.RED);
		g.drawRect((int) attackBox.x - xLevelOffset, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
	}
	
	//Creates the hitbox for any type of entity
	protected void initHitbox(float width, float height) {
		hitbox = new Rectangle2D.Float(x, y, width * SCALE, height * SCALE);
	}
	
	protected void initHealthbar(float width, float height) {
		healthbar = new Rectangle2D.Float(x, y, width * SCALE, height * SCALE);
	}

	//Getter for the hitbox
	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}
	
	public int getEnemyState() {
		return state;
	}
	
	public int getAniIndex() {
		return aniIndex;
	}
}
