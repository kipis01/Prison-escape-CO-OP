package Entities;

import Utils.Coords;
import Utils.Rectangle;

public class Entity {
    private String name;
    private Integer ID;
    private final Integer defaultMaxHP = 100;
    private Integer maxHP, HP;
    private final float defaultSpeed = 100;
    private float speed;
    private boolean alive = true;
    private final boolean invulnerable = false;
    private Coords position = new Coords(0, 0);
    private final Rectangle hitbox = new Rectangle(new Coords(0, 10), new Coords(3, 0));

    public Entity (String Name, Integer ID){
        this(Name, ID, 100, 1);
    }

    public Entity(String Name, Integer ID, Integer MaxHP, float Speed){
        this.name = Name;
        this.ID = ID;
        this.maxHP = this.HP = MaxHP;
        this.speed = Speed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getID() {
        return ID;
    }

    public Integer getDefaultMaxHP() {
        return defaultMaxHP;
    }

    public Integer getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(Integer maxHP) {
        this.maxHP = maxHP;
    }

    public Integer getHP() {
        return HP;
    }

    public void setHP(Integer HP) {
        this.HP = HP;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public Coords getPosition() {
        return position;
    }

    public void setPosition(Coords position) {
        this.position = position;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public float getDefaultSpeed() {
        return defaultSpeed;
    }

    public void kill() { alive = false; HP = 0; }

    public void resetMaxHP() { maxHP = defaultMaxHP; }

    /**
     * Applies damage to the entity, kills it if HP <= 0
     * @param damage damage dealt in hp
     * @return current HP
     */
    public int damage(int damage) {
        if (invulnerable) return HP;
        HP -= Math.abs(damage);
        if (HP <= 0){ HP = 0; kill(); }
        return HP;
    }

    /**
     * Applies health in hp to the entity
     * @param hp hit points added to health
     * @return current HP
     */
    public int heal(int hp) {
        if (invulnerable) return HP;
        HP += Math.abs(hp);
        if (HP > maxHP) HP = maxHP;
        return HP;
    }
}
