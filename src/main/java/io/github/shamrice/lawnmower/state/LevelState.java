package io.github.shamrice.lawnmower.state;

import io.github.shamrice.lawnmower.actors.Actor;
import org.newdawn.slick.tiled.TiledMap;

import java.util.List;

public class LevelState {

    private static LevelState instance = null;

    private int currentLevel = 1;
    private TiledMap currentTiledMap;
    private int mowTilesRemaining;
    private List<Actor> currentActors;

    public static LevelState getInstance() {
        if (instance == null) {
            instance = new LevelState();
        }

        return instance;
    }


    public int getCurrentLevel() {
        return currentLevel;
    }

    public TiledMap getCurrentTiledMap() {
        return currentTiledMap;
    }

    public void setCurrentTiledMap(int level, TiledMap currentTiledMap) {
        this.currentLevel = level;
        this.currentTiledMap = currentTiledMap;
    }

    public int getMowTilesRemaining() {
        return mowTilesRemaining;
    }

    public void setMowTilesRemaining(int mowTilesRemaining) {
        this.mowTilesRemaining = mowTilesRemaining;
    }

    public void decreaseMowTilesRemaining() {
        mowTilesRemaining--;
    }

    public List<Actor> getCurrentActors() {
        return currentActors;
    }

    public void setCurrentActors(List<Actor> currentActors) {
        this.currentActors = currentActors;
    }
}
