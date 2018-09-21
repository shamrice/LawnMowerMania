package io.github.shamrice.lawnmower.inventory;

import io.github.shamrice.lawnmower.inventory.lookup.InventoryItemLookUp;

import java.util.*;

public class Inventory {

    private Map<InventoryItemType, List<InventoryItem>> inventoryItemMap;
    private InventoryItem equippedInventoryItem;
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
     * @return The InventoryItem used. If item does not exist, null is returned.
     */
    public InventoryItem useInventoryItem(InventoryItemType itemType) {
        if (inventoryItemMap.containsKey(itemType) && inventoryItemMap.get(itemType).size() > 0) {

            List<InventoryItem> itemsToUse = inventoryItemMap.get(itemType);
            InventoryItem itemToUse = itemsToUse.remove(0);

            if (inventoryItemMap.get(itemType).size() <= 0) {
                inventoryItemMap.remove(itemType);
            }

            return itemToUse;
        }

        return null;
    }

    /**
     * Gets a unique list of inventory items currently in inventory.
     * @return unique list of inventory items in inventory.
     */
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

    /**
     * Gets number of inventory items remaining of the type supplied.
     * @param inventoryItemType InventoryItemType to check remaining count for
     * @return the number of inventory items left.
     */
    public int getNumberOfItemsRemaining(InventoryItemType inventoryItemType) {

        List<InventoryItem> items = inventoryItemMap.get(inventoryItemType);
        if (items != null) {
            return items.size();
        }

        else return 0;
    }

    public InventoryItem getEquippedInventoryItem() {
        return equippedInventoryItem;
    }

    public void setEquippedInventoryItem(InventoryItem equippedInventoryItem) {
        this.equippedInventoryItem = equippedInventoryItem;
    }
}
