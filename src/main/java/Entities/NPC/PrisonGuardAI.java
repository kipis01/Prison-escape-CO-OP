package Entities.NPC;

import Utils.Coords;

public class PrisonGuardAI {
    private PrisonGuard prisonGuard;
    private Coords destination;

    public PrisonGuardAI(PrisonGuard prisonGuard) {
        this.prisonGuard = prisonGuard;
    }

    public void setDestination(Coords destination) {
        this.destination = destination;
    }

    public void update(float deltaTime) {
       /* if (destination != null) {
            Coords direction = destination.subtract(prisonGuard.getPosition()).normalize();
            Coords newPosition = prisonGuard.getPosition().add(direction.multiply(prisonGuard.getSpeed() * deltaTime));
            prisonGuard.setPosition(newPosition);
        } */
    }
}
