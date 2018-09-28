package io.github.shamrice.lawnmower.actors;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

public class PlayerActor extends Actor {

    private final static float MAX_STAMINA = 100;

    private Direction direction = Direction.DOWN;
    private float movementSpeed;
    private float stamina = MAX_STAMINA;

    public PlayerActor(Image spriteImage, float x, float y, float movementSpeed) {
        super(ActorType.PLAYER, spriteImage, x, y);
        this.movementSpeed = movementSpeed;
    }

    public PlayerActor(Animation spriteAnimation, float x, float y, float movementSpeed) {
        super(ActorType.PLAYER, spriteAnimation, x, y);
        this.movementSpeed = movementSpeed;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public float getMovementSpeed() {
        return movementSpeed;
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
