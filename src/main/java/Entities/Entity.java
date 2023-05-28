package Entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

	protected float x, y;
	protected int width, height;
	protected Rectangle2D.Float hitbox;

	public Entity(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	//Draws the hitbox over the entity (for debugging purposes)
	protected void drawHitbox(Graphics g, int xLevelOffset) {
		g.setColor(Color.PINK);
		g.drawRect((int) hitbox.x - xLevelOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}

	//Creates the hitbox for any type of entity
	protected void initHitbox(float x, float y, float width, float height) {
		hitbox = new Rectangle2D.Float(x, y, width, height);
	}

	//Getter for the hitbox
	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}
}
