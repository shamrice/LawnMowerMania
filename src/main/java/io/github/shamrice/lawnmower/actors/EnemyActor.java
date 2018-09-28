package io.github.shamrice.lawnmower.actors;

import io.github.shamrice.lawnmower.configuration.actors.ActorConfiguration;

public class EnemyActor extends Actor {

    private int currentHealth;

    public EnemyActor(ActorConfiguration configuration, float x, float y) {
        super(configuration, x, y);
        this.currentHealth = configuration.getHealth();
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

}
