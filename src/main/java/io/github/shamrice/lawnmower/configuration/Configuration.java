package io.github.shamrice.lawnmower.configuration;

// TODO : build this out.

import io.github.shamrice.lawnmower.inventory.lookup.InventoryItemLookUp;

import java.util.HashMap;

public class Configuration {

    private InventoryItemLookUp inventoryItemLookUp;

    public Configuration(InventoryItemLookUp inventoryItemLookUp) {
        this.inventoryItemLookUp = inventoryItemLookUp;
    }

    /**
     *
     * @return Populated InventoryItemLookUp for use in Inventory.
     */
    public InventoryItemLookUp getInventoryItemLookUp() {
        return inventoryItemLookUp;
    }
}
