package io.github.shamrice.lawnmower.inventory.lookup;

import io.github.shamrice.lawnmower.inventory.InventoryItem;
import io.github.shamrice.lawnmower.inventory.InventoryItemType;

import java.util.Map;

/* TODO : Not sure about the naming of this one... */
public class InventoryItemLookUp {

    private Map<InventoryItemType, InventoryItem> inventoryItemMap;

    /**
     * Used to look up item information while app is running. Item map is created by configuration and set at startup.
     * @param inventoryItemMap Item map with definitions all in game items available.
     */
    public InventoryItemLookUp(Map<InventoryItemType, InventoryItem> inventoryItemMap) {
        this.inventoryItemMap = inventoryItemMap;
    }

    public InventoryItem getItem(InventoryItemType inventoryItemType) {
        return inventoryItemMap.get(inventoryItemType);
    }
}
