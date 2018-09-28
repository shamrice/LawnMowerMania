package io.github.shamrice.lawnmower.actors;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

public abstract class Actor {

    private float x;
    private float y;
    private int mapX = 0;
    private int mapY = 0;
    private int previousMapX = 0;
    private int previousMapY = 0;
    private Image spriteImage;
    private ActorType actorType;
    protected boolean isAlive = true;
    private Animation spriteAnimation;
    private int previousSpriteAnimationDuration = 120;

    Actor(ActorType actorType, Image spriteImage, float x, float y) {
        this.actorType = actorType;
        this.spriteImage = spriteImage;
        this.x = x;
        this.y = y;
    }

    Actor(ActorType actorType, Animation spriteAnimation, float x, float y) {
        this.actorType = actorType;
        this.spriteAnimation = spriteAnimation;
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
        setMapXY((int)(x/ 32), (int)(y / 32));

    }

    /**
     *
     * @return get sprite image for actor
     */
    public Image getSpriteImage() {
        return spriteImage;
    }

    public ActorType getActorType() {
        return actorType;
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
        return spriteAnimation;
    }

    public void setSpriteAnimationFrameDuration(int duration) {

        //only update duration if it is different than what is already set.
        if (duration != spriteAnimation.getDuration(0)) {
            previousSpriteAnimationDuration = spriteAnimation.getDuration(0);

            for (int i = 0; i < spriteAnimation.getFrameCount(); i++) {
                spriteAnimation.setDuration(i, duration);
            }
        }
    }

    public int getPreviousSpriteAnimationDuration() {
        return previousSpriteAnimationDuration;
    }
}
