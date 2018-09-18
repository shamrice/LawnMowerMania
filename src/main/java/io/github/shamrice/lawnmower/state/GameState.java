package io.github.shamrice.lawnmower.state;

import io.github.shamrice.lawnmower.actors.Actor;
import io.github.shamrice.lawnmower.configuration.Configuration;
import io.github.shamrice.lawnmower.core.graphics.Panel;
import io.github.shamrice.lawnmower.inventory.Inventory;
import io.github.shamrice.lawnmower.inventory.InventoryItem;
import org.newdawn.slick.tiled.TiledMap;

import java.util.List;

public class GameState {

    private static GameState instance = null;

    private boolean isRunning = false;
    private int currentLevel = 1;
    private TiledMap currentTiledMap;
    private int mowTilesRemaining;
    private Configuration configuration;
    private Inventory inventory;
    private InventoryItem equippedInventoryItem;
    private Panel currentPanel;
    private List<Actor> currentActors;

    private GameState() {}

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }

        return instance;
    }

    /**
     * State of game.
     * @return If game is currently running or not.
     */
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

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public InventoryItem getEquippedInventoryItem() {
        return equippedInventoryItem;
    }

    public void setEquippedInventoryItem(InventoryItem equippedInventoryItem) {
        this.equippedInventoryItem = equippedInventoryItem;
    }

    public Panel getCurrentPanel() {
        return currentPanel;
    }

    public void setCurrentPanel(Panel currentPanel) {
        this.currentPanel = currentPanel;
    }

    public List<Actor> getCurrentActors() {
        return currentActors;
    }

    public void setCurrentActors(List<Actor> currentActors) {
        this.currentActors = currentActors;
    }
}
