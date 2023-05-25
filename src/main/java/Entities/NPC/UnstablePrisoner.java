package Entities.NPC;

import Entities.Entity;
import Utils.Coords;
import Utils.Rectangle;

public class UnstablePrisoner extends Entity {
    private final Integer defaultMaxHP = 100;
    private final boolean invulnerable = false;
    private final float defaultSpeed = 100;
    private final Rectangle hitbox = new Rectangle(new Coords(0, 10), new Coords(3, 0));
    private UnstablePrisonerAI ai;

    public UnstablePrisoner(String Name, Integer ID) {
        super(Name, ID);
        ai = new UnstablePrisonerAI(this);
    }

    public UnstablePrisoner(String Name, Integer ID, Integer MaxHP, float Speed) {
        super(Name, ID, MaxHP, Speed);
        ai = new UnstablePrisonerAI(this);
    }

    // Other methods specific to the UnstablePrisoner class
}
