package Entities.NPC;

import Entities.Entity;

public class Stabber extends Entity {
    private final int defaultMaxHP = 100;
    private final boolean invulnerable = false;
    private final float defaultSpeed = 100;

    public Stabber(String name, Integer ID) {
        super(name, ID);
    }

    public Stabber(String name, Integer ID, Integer maxHP, float speed) {
        super(name, ID, maxHP, speed);
    }

    public void stab() {
        // Implement the logic for the stab attack
        // You can update health of the target or perform any other desired actions
    }
}
