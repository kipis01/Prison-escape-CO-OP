package Entities;

public abstract class Entity {

	protected float x, y;
	protected int scaleX, scaleY;

	public Entity(float x, float y, int scaleX, int scaleY) {
		this.x = x;
		this.y = y;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

}
