package io.github.shamrice.lawnmower.configuration;

// TODO : configuration builder builds config. Build this out.

import io.github.shamrice.lawnmower.inventory.InventoryItem;
import io.github.shamrice.lawnmower.inventory.InventoryItemType;
import io.github.shamrice.lawnmower.inventory.lookup.InventoryItemLookUp;
import org.newdawn.slick.TrueTypeFont;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationBuilder {

    /**
     * Builds configuration based on configuration files and assets.
     * @return Built configuration.
     */
    public static Configuration buildConfiguration() {
        return new Configuration(
                buildInventoryItemLookup(),
                buildTrueTypeFont()
        );
    }

    private static InventoryItemLookUp buildInventoryItemLookup() {

        Map<InventoryItemType, InventoryItem> inventoryItemMap = new HashMap<>();

        //TODO : build from item configuration instead of hardcoded.
        InventoryItem inventoryItem = new InventoryItem(
                InventoryItemType.NOT_FOUND,
                "NOT FOUND",
                "Item not found",
                0
        );

        inventoryItemMap.put(InventoryItemType.NOT_FOUND, inventoryItem);

        inventoryItem = new InventoryItem(
                InventoryItemType.GRASS_SEED,
                "Grass Seed",
                "Restores dead grass patch",
                1
        );

        inventoryItemMap.put(InventoryItemType.GRASS_SEED, inventoryItem);

        return new InventoryItemLookUp(inventoryItemMap);
    }

    private static TrueTypeFont buildTrueTypeFont() {
        //TODO : build from configuration file.
        java.awt.Font font = new java.awt.Font("Ariel", java.awt.Font.PLAIN, 12);
        return new TrueTypeFont(font, true);
    }
}
