package io.github.shamrice.lawnmower.configuration;

// TODO : build this out.

import io.github.shamrice.lawnmower.actors.ActorType;
import io.github.shamrice.lawnmower.configuration.actors.ActorConfiguration;
import io.github.shamrice.lawnmower.configuration.levels.LevelConfiguration;
import io.github.shamrice.lawnmower.inventory.lookup.InventoryItemLookUp;
import org.newdawn.slick.TrueTypeFont;

import java.util.List;
import java.util.Map;

public class Configuration {

    private InventoryItemLookUp inventoryItemLookUp;
    private TrueTypeFont trueTypeFont;
    private Map<ActorType, ActorConfiguration> actorConfigurationMap;
    private List<LevelConfiguration> levelConfiguration;

    public Configuration(InventoryItemLookUp inventoryItemLookUp, TrueTypeFont trueTypeFont,
                         Map<ActorType, ActorConfiguration> actorConfigurationMap,
                         List<LevelConfiguration> levelConfiguration) {

        this.inventoryItemLookUp = inventoryItemLookUp;
        this.trueTypeFont = trueTypeFont;
        this.actorConfigurationMap = actorConfigurationMap;
        this.levelConfiguration = levelConfiguration;
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

    public LevelConfiguration getLevelConfiguration(int levelNumber) {

        if (levelNumber > levelConfiguration.size())
            levelNumber = 0;

        return levelConfiguration.get(levelNumber);
    }
}
