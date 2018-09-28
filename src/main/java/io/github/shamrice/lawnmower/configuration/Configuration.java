package io.github.shamrice.lawnmower.configuration;

// TODO : build this out.

import io.github.shamrice.lawnmower.actors.ActorType;
import io.github.shamrice.lawnmower.configuration.actors.ActorConfiguration;
import io.github.shamrice.lawnmower.inventory.lookup.InventoryItemLookUp;
import org.newdawn.slick.TrueTypeFont;

import java.util.Map;

public class Configuration {

    private InventoryItemLookUp inventoryItemLookUp;
    private TrueTypeFont trueTypeFont;
    private Map<ActorType, ActorConfiguration> actorConfigurationMap;

    public Configuration(InventoryItemLookUp inventoryItemLookUp, TrueTypeFont trueTypeFont,
                         Map<ActorType, ActorConfiguration> actorConfigurationMap) {

        this.inventoryItemLookUp = inventoryItemLookUp;
        this.trueTypeFont = trueTypeFont;
        this.actorConfigurationMap = actorConfigurationMap;
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

    public ActorConfiguration getActorConfiguration(ActorType actorType) {
        return actorConfigurationMap.get(actorType);
    }
}
