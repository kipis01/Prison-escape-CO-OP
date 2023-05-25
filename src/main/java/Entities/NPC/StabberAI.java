package Entities.NPC;

import Utils.Coords;

public class StabberAI {
    private Stabber stabber;
    private Coords destination;

    public StabberAI(Stabber stabber) {
        this.stabber = stabber;
    }

    public void setDestination(Coords destination) {
        this.destination = destination;
    }

    public void update(float deltaTime) {
       /* if (destination != null) {
            Coords direction = destination.subtract(stabber.getPosition()).normalize();
            Coords newPosition = stabber.getPosition().add(direction.multiply(stabber.getSpeed() * deltaTime));
            stabber.setPosition(newPosition);
        }
        stabber.performAttack(); // Perform the attack behavior*/
    }
}
