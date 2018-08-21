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

}
