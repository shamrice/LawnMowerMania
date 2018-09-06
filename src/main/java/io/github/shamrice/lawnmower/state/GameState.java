package io.github.shamrice.lawnmower.state;

import org.newdawn.slick.tiled.TiledMap;

public class GameState {

    private static GameState instance = null;

    private boolean isRunning = false;
    private int currentLevel = 1;
    private TiledMap currentTiledMap;
    private int mowTilesRemaining;

    private GameState() {}

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }

        return instance;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
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
}
