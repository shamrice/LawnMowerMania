package io.github.shamrice.lawnmower.configuration;

// TODO : build this out.

import io.github.shamrice.lawnmower.inventory.lookup.InventoryItemLookUp;
import org.newdawn.slick.TrueTypeFont;

public class Configuration {

    private InventoryItemLookUp inventoryItemLookUp;
    private TrueTypeFont trueTypeFont;

    public Configuration(InventoryItemLookUp inventoryItemLookUp, TrueTypeFont trueTypeFont) {
        this.inventoryItemLookUp = inventoryItemLookUp;
        this.trueTypeFont = trueTypeFont;
    }

    /**
     *
     * @return Populated InventoryItemLookUp for use in Inventory.
     */
    public InventoryItemLookUp getInventoryItemLookUp() {
        return inventoryItemLookUp;
    }

    public TrueTypeFont getTrueTypeFont() {
        return trueTypeFont;
    }
}
