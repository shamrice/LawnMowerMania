package io.github.shamrice.lawnmower.configuration;

// TODO : build this out.

import io.github.shamrice.lawnmower.inventory.lookup.InventoryItemLookUp;

import java.util.HashMap;

public class Configuration {

    private InventoryItemLookUp inventoryItemLookUp;

    public Configuration() {
        //TODO : just a place holder....
        inventoryItemLookUp = new InventoryItemLookUp(new HashMap<>());
    }

    /**
     *
     * @return Populated InventoryItemLookUp for use in Inventory.
     */
    public InventoryItemLookUp getInventoryItemLookUp() {
        return inventoryItemLookUp;
    }
}
