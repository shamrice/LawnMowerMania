package io.github.shamrice.lawnmower.actors;

import io.github.shamrice.lawnmower.configuration.actors.ActorConfiguration;

public class PlayerActor extends Actor {

    private final static float MAX_STAMINA = 100;

    private Direction direction = Direction.DOWN;
    private float stamina = MAX_STAMINA;

    public PlayerActor(ActorConfiguration configuration, float x, float y) {
        super(configuration, x, y);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean useStamina(float amount) {
        stamina -= amount;

        if (stamina <= 0) {
            stamina = 0;
            return false;
        } else {
            return true;
        }
    }

    public void recoverStamina(float amount) {
        stamina += amount;
        if (stamina > MAX_STAMINA)
            stamina = MAX_STAMINA;
    }

    public float getStamina() {
        return stamina;
    }

}
