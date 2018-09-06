package io.github.shamrice.lawnmower.actors;

import org.newdawn.slick.Image;

public abstract class Actor {

    private float x;
    private float y;
    private Image spriteImage;

    public Actor(Image spriteImage, float x, float y) {
        this.spriteImage = spriteImage;
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void updateXY(float deltaX, float deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public Image getSpriteImage() {
        return spriteImage;
    }
}
