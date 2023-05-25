package Entities.NPC;

import Entities.Entity;
import Utils.Rectangle;
import Utils.Coords;
import java.util.Random;

public class Prisoner extends Entity {
    private final int defaultMaxHP;
    private final boolean invulnerable = false;
    private final float defaultSpeed = 100;
    private final Rectangle hitbox = new Rectangle(new Coords(0, 10), new Coords(3, 0));
    private PrisonerAI ai;

    public Prisoner(String name, Integer ID) {
        super(name, ID);
        defaultMaxHP = generateRandomMaxHP();
        ai = new PrisonerAI(this);
    }

    public Prisoner(String name, Integer ID, Integer maxHP, float speed) {
        super(name, ID, maxHP, speed);
        defaultMaxHP = generateRandomMaxHP();
        ai = new PrisonerAI(this);
    }

    private int generateRandomMaxHP() {
        Random random = new Random();
        return random.nextInt(21) + 80; // Generates a random number between 80 and 100 (inclusive)
    }
}
