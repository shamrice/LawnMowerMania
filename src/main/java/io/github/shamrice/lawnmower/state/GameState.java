package io.github.shamrice.lawnmower.state;

import io.github.shamrice.lawnmower.configuration.Configuration;
import io.github.shamrice.lawnmower.core.graphics.Panel;
import io.github.shamrice.lawnmower.inventory.Inventory;


public class GameState {

    private static GameState instance = null;

    private boolean isRunning = false;
    private Configuration configuration;
    private Inventory inventory;
    private Panel currentPanel;

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

    public Panel getCurrentPanel() {
        return currentPanel;
    }

    public void setCurrentPanel(Panel currentPanel) {
        this.currentPanel = currentPanel;
    }

}
