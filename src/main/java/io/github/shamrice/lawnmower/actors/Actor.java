package io.github.shamrice.lawnmower.actors;

import org.newdawn.slick.Image;

public abstract class Actor {

    private float x;
    private float y;
    private Image spriteImage;
    private ActorType actorType;

    public Actor(ActorType actorType, Image spriteImage, float x, float y) {
        this.actorType = actorType;
        this.spriteImage = spriteImage;
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
}
