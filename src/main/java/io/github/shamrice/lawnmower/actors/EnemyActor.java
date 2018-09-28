package io.github.shamrice.lawnmower.actors;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

public class EnemyActor extends Actor {

    private int currentHealth;
    private float movementSpeed;

    public EnemyActor(ActorType actorType, Image spriteImage, float x, float y, int currentHealth, float movementSpeed) {
        super(actorType, spriteImage, x, y);
        this.currentHealth = currentHealth;
        this.movementSpeed = movementSpeed;
    }

    public EnemyActor(ActorType actorType, Animation spriteAnimation, float x, float y, int currentHealth, float movementSpeed) {
        super(actorType, spriteAnimation, x, y);
        this.currentHealth = currentHealth;
        this.movementSpeed = movementSpeed;
    }

    /**
     * Damages enemy by the amount passed to the value parameter.
     * If enemy's health goes to or below zero, enemy is killed.
     * @param value The amount of health to decrease from the enemy.
     */
    public void damage(int value) {
        currentHealth -= value;

        if (currentHealth <= 0) {
            isAlive = false;
        }
    }



    /**
     * Gets speed of which enemy should move in the level.
     * @return The amount of distance an enemy should move per frame.
     */
    public float getMovementSpeed() {
        return movementSpeed;
    }
}
