package Entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

	protected float x, y;
	protected int width, height;
	protected Rectangle2D.Float hitbox;

	public Entity(float x, float y, int scaleX, int scaleY) {
		this.x = x;
		this.y = y;
		this.width = scaleX;
		this.height = scaleY;
	}

	protected void drawHitbox(Graphics g) {
		// For debugging the hitbox
		g.setColor(Color.PINK);
		g.drawRect((int) hitbox.x, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}

	protected void initHitbox(float x, float y, float width, float height) {
		hitbox = new Rectangle2D.Float(x, y, width, height);

	}

//	protected void updateHitbox() {
//		hitbox.x = (int) x;
//		hitbox.y = (int) y;
//	}

	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

}
