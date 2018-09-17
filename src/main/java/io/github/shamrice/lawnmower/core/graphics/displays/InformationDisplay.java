package io.github.shamrice.lawnmower.core.graphics.displays;

import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.inventory.InventoryItem;
import io.github.shamrice.lawnmower.state.GameState;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

public class InformationDisplay {

    private TrueTypeFont font;

    public InformationDisplay(TrueTypeFont font) {
        this.font = font;
    }

    public void displayDebug(Graphics g, float delta, PlayerActor player) {
        g.drawString("x: " + player.getX() + " y: " + player.getY() + " delta: " + delta, 100, 1);
        font.drawString(810, 10, "Score: " + player.getScore());
        font.drawString(810, 30, "Grass to cut: " + GameState.getInstance().getMowTilesRemaining());

    }

    public void displayStamina(PlayerActor player) {
        Color staminaTextColor;

        if (player.getStamina() <= 10)
            staminaTextColor = Color.red;
        else if (player.getStamina() > 10 && player.getStamina() <= 50)
            staminaTextColor = Color.yellow;
        else if (player.getStamina() > 50 && player.getStamina() <= 75)
            staminaTextColor = Color.green;
        else
            staminaTextColor = Color.white;

        font.drawString(810, 50,"Stamina: " + (int)player.getStamina() + "/100", staminaTextColor);
    }

    /**
     * Draws inventory information on left side of screen.
     * @param g Graphics object to render text with.
     */
    public void displayInventory(Graphics g) {

        int x = 810;
        int y = 100;

        GameState state = GameState.getInstance();

        //display equipped item (if any)
        String equippedItemName = "None";
        if (state.getEquippedInventoryItem() != null) {
            equippedItemName = state.getEquippedInventoryItem().getName();
        }
        font.drawString(x, y, "Equipped: " + equippedItemName);
        y += 25;

        //type full inventory. (to be replaced with icons.
        font.drawString(x, y, "Inventory:");
        y += 25;
        x += 10;

        for (InventoryItem inventoryItem : state.getInventory().getAllInventoryItems()) {
            font.drawString(x, y, inventoryItem.getName());
            y += 15;
            font.drawString(x, y, inventoryItem.getDescription());
            y += 15;
            font.drawString(x,  y, "Value: " + inventoryItem.getValue());
            y += 15;
            font.drawString(x, y, "Items left: " + state.getInventory().getNumberOfItemsRemaining(inventoryItem.getInventoryItemType()));
            y += 30;
        }
    }
}