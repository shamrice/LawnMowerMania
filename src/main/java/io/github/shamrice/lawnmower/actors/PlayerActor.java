package io.github.shamrice.lawnmower.actors;

import org.newdawn.slick.Image;

public class PlayerActor extends Actor {

    private final static float MAX_STAMINA = 100;

    private long score;
    private int mapX = 0;
    private int mapY = 0;
    private Direction direction = Direction.DOWN;
    private float movementSpeed;
    private float stamina = MAX_STAMINA;


    public PlayerActor(Image spriteImage, float x, float y, float movementSpeed) {
        super(ActorType.PLAYER, spriteImage, x, y);
        this.score = 0;
        this.movementSpeed = movementSpeed;
    }

    /**
     *
     * @return Current score value.
     */
    public long getScore() {
        return score;
    }

    /**
     * Update score by the amount specified.
     * @param delta Amount to update score by.
     */
    public void changeScore(int delta) {
        score += delta;
        if (score < 0)
            score = 0;
    }

    public void setMapXY(int x, int y) {
        this.mapX = x;
        this.mapY = y;
    }

    public int getMapX() {
        return mapX;
    }

    public void setMapX(int mapX) {
        this.mapX = mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public void setMapY(int mapY) {
        this.mapY = mapY;
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
