package io.github.shamrice.lawnmower.inventory;

import io.github.shamrice.lawnmower.inventory.lookup.InventoryItemLookUp;

import java.util.*;

public class Inventory {

    private Map<InventoryItemType, List<InventoryItem>> inventoryItemMap;
    private InventoryItemLookUp inventoryItemLookUp;

    public Inventory(InventoryItemLookUp inventoryItemLookUp) {
        this.inventoryItemLookUp = inventoryItemLookUp;
        inventoryItemMap = new HashMap<>();
    }

    /**
     * Adds InventoryItem to inventory. If already exists, increases item count by one.
     * @param inventoryItemType InventoryItem to add to inventory.
     */
    public void addInventoryItem(InventoryItemType inventoryItemType) {

        InventoryItem itemToAdd = inventoryItemLookUp.getItem(inventoryItemType);

        if (itemToAdd != null) {
            if (inventoryItemMap.containsKey(inventoryItemType)) {
                inventoryItemMap.get(inventoryItemType).add(itemToAdd);
            } else {
                List<InventoryItem> itemList = new ArrayList<>();
                itemList.add(itemToAdd);
                inventoryItemMap.put(inventoryItemType, itemList);
            }
        }
    }

    /**
     * Returns InventoryItem from inventory if found, otherwise returns NOT_FOUND item.
     * @param inventoryItemType Item type to search inventory for.
     * @return Item found in inventory or NOT_FOUND item if item does not exist.
     */
    public InventoryItem getInventoryItem(InventoryItemType inventoryItemType) {
        Set<InventoryItemType> itemTypesInInventory = inventoryItemMap.keySet();

        if (itemTypesInInventory.contains(inventoryItemType)) {
            if (inventoryItemMap.get(inventoryItemType).size() > 0) {
                return inventoryItemMap.get(inventoryItemType).get(0);
            }
        }

        return inventoryItemLookUp.getItem(InventoryItemType.NOT_FOUND);
    }

    /**
     * Use inventory item. Decrease amount available by one or remove from inventory if none left.
     * @param itemType Inventory Item Type to be used in the inventory.
     * @return The InventoryItem used. If item does not exist, a NOT_FOUND item is returned.
     */
    public InventoryItem useInventoryItem(InventoryItemType itemType) {
        if (inventoryItemMap.containsKey(itemType) && inventoryItemMap.get(itemType).size() > 0) {

            InventoryItem itemToUse = inventoryItemMap.get(itemType).remove(inventoryItemMap.get(itemType).size());

            if (inventoryItemMap.get(itemType).size() <= 0) {
                inventoryItemMap.remove(itemType);
            }

            return itemToUse;
        }

        return inventoryItemLookUp.getItem(InventoryItemType.NOT_FOUND);
    }

    public List<InventoryItem> getAllInventoryItems() {
        List<InventoryItem> itemsInInventory = new ArrayList<>();

        for (InventoryItemType itemType : inventoryItemMap.keySet()) {
            InventoryItem item = inventoryItemLookUp.getItem(itemType);
            if (item != null) {
                itemsInInventory.add(item);
            }
        }

        return itemsInInventory;
    }
}
