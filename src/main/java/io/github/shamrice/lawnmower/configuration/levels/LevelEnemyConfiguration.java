package io.github.shamrice.lawnmower.configuration.levels;

import io.github.shamrice.lawnmower.actors.ActorType;

public class LevelEnemyConfiguration {

    private ActorType actorType;
    private float x;
    private float y;

    public LevelEnemyConfiguration(ActorType actorType, float x, float y) {
        this.actorType = actorType;
        this.x = x;
        this.y = y;
    }

    public ActorType getActorType() {
        return actorType;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
