package Entities.NPC;

import Utils.Coords;

public class UnstablePrisonerAI {
    private UnstablePrisoner unstablePrisoner;
    private Coords destination;

    public UnstablePrisonerAI(UnstablePrisoner unstablePrisoner) {
        this.unstablePrisoner = unstablePrisoner;
    }

    public void setDestination(Coords destination) {
        this.destination = destination;
    }

    public void update(float deltaTime) {
     /*   if (destination != null) {
            Coords direction = destination.subtract(unstablePrisoner.getPosition()).normalize();
            Coords newPosition = unstablePrisoner.getPosition().add(direction.multiply(unstablePrisoner.getSpeed() * deltaTime));
            unstablePrisoner.setPosition(newPosition);
        }
        unstablePrisoner.performBehavior(); // Perform the behavior (e.g., explode, cause damage, etc.)

      */
    }
}
