package io.github.shamrice.lawnmower.configuration.actors;

import io.github.shamrice.lawnmower.actors.ActorType;
import org.newdawn.slick.Animation;

public class ActorConfiguration {

    private ActorType actorType;
    private Animation animation;
    private int defaultFrameDuration;
    private int runningFrameDuration;
    private int health;
    private float movementSpeed;
    private float movementSpeedMultiplier;
    private boolean isBoundaryBlocked;

    public ActorConfiguration(ActorType actorType, Animation animation, int defaultFrameDuration,
                              int runningFrameDuration, int health, float movementSpeed,
                              float movementSpeedMultiplier, boolean isBoundaryBlocked) {
        this.actorType = actorType;
        this.animation = animation;
        this.defaultFrameDuration = defaultFrameDuration;
        this.runningFrameDuration = runningFrameDuration;
        this.health = health;
        this.movementSpeed = movementSpeed;
        this.movementSpeedMultiplier = movementSpeedMultiplier;
        this.isBoundaryBlocked = isBoundaryBlocked;
    }

    public ActorType getActorType() {
        return actorType;
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getDefaultFrameDuration() {
        return defaultFrameDuration;
    }

    public int getRunningFrameDuration() {
        return runningFrameDuration;
    }

    public int getHealth() {
        return health;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public float getMovementSpeedMultiplier() {
        return movementSpeedMultiplier;
    }

    public boolean isBoundaryBlocked() {
        return isBoundaryBlocked;
    }

}
