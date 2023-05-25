package Entities.NPC;

import Utils.Coords;

public class PrisonerAI {
    private Prisoner prisoner;
    private Coords destination;

    public PrisonerAI(Prisoner prisoner) {
        this.prisoner = prisoner;
    }

    public void setDestination(Coords destination) {
        this.destination = destination;
    }

    public void update(float deltaTime) {
      /*  if (destination != null) {
            Coords direction = destination.subtract(prisoner.getPosition()).normalize();
            Coords newPosition = prisoner.getPosition().add(direction.multiply(prisoner.getSpeed() * deltaTime));
            prisoner.setPosition(newPosition);
        } */
    }
}
