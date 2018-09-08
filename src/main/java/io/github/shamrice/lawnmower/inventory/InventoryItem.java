package io.github.shamrice.lawnmower.inventory;

public class InventoryItem {

    private String name;
    private String description;
    private int value;
    private InventoryItemType inventoryItemType;

    public InventoryItem(InventoryItemType inventoryItemType, String name, String description, int value) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.inventoryItemType = inventoryItemType;
    }

    /**
     * Name of inventory item.
     * @return Name of inventory item.
     */
    public String getName() {
        return name;
    }

    /**
     * Description of inventory item.
     * @return Description of inventory item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Value of inventory item
     * @return Numeric value of inventory item.
     */
    public int getValue() {
        return value;
    }

    /**
     * Type that item is.
     * @return InventoryItemType of item.
     */
    public InventoryItemType getInventoryItemType() {
        return inventoryItemType;
    }
}
