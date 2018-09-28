package io.github.shamrice.lawnmower.actors;

import io.github.shamrice.lawnmower.common.Constants;
import io.github.shamrice.lawnmower.configuration.actors.ActorConfiguration;
import org.newdawn.slick.Animation;

public abstract class Actor {

    private float x;
    private float y;
    private int mapX = 0;
    private int mapY = 0;
    private int previousMapX = 0;
    private int previousMapY = 0;
    protected boolean isAlive = true;
    private int previousSpriteAnimationDuration = 120;
    private ActorConfiguration actorConfiguration;

    Actor(ActorConfiguration actorConfiguration, float x, float y) {
        this.actorConfiguration = actorConfiguration;
        this.x = x;
        this.y = y;
    }

    /**
     * Get screen X position of sprite.
     * @return x screen position.
     */
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    /**
     * Get screen Y position of sprite
     * @return y screen position.
     */
    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    /**
     * Update X, Y screen coordinates.
     * @param deltaX Amount to change X position by
     * @param deltaY Amount to change Y position by
     */
    public void updateXY(float deltaX, float deltaY) {
        x += deltaX;
        y += deltaY;
        setMapXY((int)(x / Constants.SPRITE_WIDTH), (int)(y / Constants.SPRITE_HEIGHT));
    }

    public ActorType getActorType() {
        return actorConfiguration.getActorType();
    }

    /**
     * Gets the living status of an enemy.
     * @return Returns true if alive otherwise, will return false.
     */
    public boolean isAlive() {
        return isAlive;
    }

    public void setMapXY(int x, int y) {
        this.previousMapX = this.mapX;
        this.previousMapY = this.mapY;
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


    public int getPreviousMapX() {
        return previousMapX;
    }

    public int getPreviousMapY() {
        return previousMapY;
    }

    public Animation getSpriteAnimation() {
        return actorConfiguration.getAnimation();
    }

    public void setSpriteAnimationFrameDuration(int duration) {

        //only update duration if it is different than what is already set.
        if (duration != actorConfiguration.getAnimation().getDuration(0)) {
            previousSpriteAnimationDuration = actorConfiguration.getAnimation().getDuration(0);

            for (int i = 0; i < actorConfiguration.getAnimation().getFrameCount(); i++) {
                actorConfiguration.getAnimation().setDuration(i, duration);
            }
        }
    }

    public int getPreviousSpriteAnimationDuration() {
        return previousSpriteAnimationDuration;
    }

    public boolean isBlockedByLevelBoundaries() {
        return actorConfiguration.isBoundaryBlocked();
    }

    /**
     * Gets speed of which enemy should move in the level.
     * @return The amount of distance an enemy should move per frame.
     */
    public float getMovementSpeed() {
        return actorConfiguration.getMovementSpeed();
    }

    public float getMovementSpeedMultiplier() {
        return actorConfiguration.getMovementSpeedMultiplier();
    }

    public int getDefaultFrameDuration() {
        return actorConfiguration.getDefaultFrameDuration();
    }

    public int getRunningFrameDuration() {
        return actorConfiguration.getRunningFrameDuration();
    }
}
