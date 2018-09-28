package io.github.shamrice.lawnmower.configuration;

// TODO : configuration builder builds config. Build this out.

import io.github.shamrice.lawnmower.actors.ActorType;
import io.github.shamrice.lawnmower.configuration.actors.ActorConfiguration;
import io.github.shamrice.lawnmower.inventory.InventoryItem;
import io.github.shamrice.lawnmower.inventory.InventoryItemType;
import io.github.shamrice.lawnmower.inventory.lookup.InventoryItemLookUp;
import org.apache.log4j.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ConfigurationBuilder {

    private final static Logger logger = Logger.getLogger(ConfigurationBuilder.class);

    private static Properties configProperties = new Properties();

    //TODO : use relative path.
    private static String configurationLocation = "/home/erik/Documents/github/lawnMowerMania/LawnMowerMania/src/main/resources/config/config.properties";
    private static String assetsLocation;
    private static String mapsLocation;

    /**
     * Builds configuration based on configuration files and assets.
     * @return Built configuration.
     */
    public static Configuration buildConfiguration() throws IOException, SlickException {

        logger.info("Building configuration using file: " + configurationLocation);



        File configFile = new File(configurationLocation);
        String configFilePath = configFile.getPath();

        if (!configFile.exists() || configFile.isDirectory()) {
            logger.error("Cannot find configuration file in path specified.");
            throw new IOException("Cannot find configuration file in path specified.");
        }

        InputStream configInput = new FileInputStream(configFilePath);
        configProperties.load(configInput);
        configInput.close();

        assetsLocation = configProperties.getProperty("assets.directory");
        mapsLocation = configProperties.getProperty("maps.directory");

        return new Configuration(
                buildInventoryItemLookup(),
                buildTrueTypeFont(),
                buildActorConfigurationMap()
        );
    }

    /**
     * Builds configuration based on configuration file supplied.
     * @param configurationLocationToUse Configuration file to use to configure application
     * @return Built configuration
     */
    //TODO : Add command line option to pass config file.
    public static Configuration buildConfiguration(String configurationLocationToUse)
            throws IOException, SlickException {

        configurationLocation = configurationLocationToUse;
        return buildConfiguration();
    }

    private static InventoryItemLookUp buildInventoryItemLookup() {

        Map<InventoryItemType, InventoryItem> inventoryItemMap = new HashMap<>();

        InventoryItem inventoryItem = new InventoryItem(
                InventoryItemType.NOT_FOUND,
                configProperties.getProperty("inventory.notFound.name"),
                configProperties.getProperty("inventory.notFound.description"),
                Integer.parseInt(configProperties.getProperty("inventory.notFound.value"))
        );

        inventoryItemMap.put(InventoryItemType.NOT_FOUND, inventoryItem);

        inventoryItem = new InventoryItem(
                InventoryItemType.GRASS_SEED,
                configProperties.getProperty("inventory.grassSeed.name"),
                configProperties.getProperty("inventory.grassSeed.description"),
                Integer.parseInt(configProperties.getProperty("inventory.grassSeed.value"))
        );

        inventoryItemMap.put(InventoryItemType.GRASS_SEED, inventoryItem);

        logger.info("InventoryItemLookUp Successfully built.");
        return new InventoryItemLookUp(inventoryItemMap);
    }

    private static TrueTypeFont buildTrueTypeFont() {
        //TODO : build from configuration file.
        java.awt.Font font = new java.awt.Font(
                configProperties.getProperty("font.name"),
                java.awt.Font.PLAIN,
                Integer.parseInt(configProperties.getProperty("font.size"))
        );

        logger.info("TrueType Font Successfully built.");
        return new TrueTypeFont(font, true);
    }

    private static Map<ActorType, ActorConfiguration> buildActorConfigurationMap() throws SlickException {

        Map<ActorType, ActorConfiguration> actorConfigurationMap = new HashMap<>();

        //get actor names to configure from config
        String[] actorsToConfigure = configProperties.getProperty("actors.configured").split(",");

        //loop through each actor and configure them.
        for (String actorName : actorsToConfigure) {

            List<Image> actorAnimationImages = new LinkedList<>();
            String actorAnimationImageFiles[] = configProperties.getProperty(actorName + ".images").split(",");

            for (String filename : actorAnimationImageFiles) {
                actorAnimationImages.add(new Image(assetsLocation + filename));
            }

            Animation actorAnimation = new Animation(
                    actorAnimationImages.toArray(new Image[0]),
                    Integer.parseInt(configProperties.getProperty(actorName + ".images.frame.duration.default"))
            );

            ActorType actorType = ActorType.valueOf(actorName.toUpperCase());

            ActorConfiguration actorConfiguration = new ActorConfiguration(
                    actorType,
                    actorAnimation,
                    Integer.parseInt(configProperties.getProperty(actorName + ".images.frame.duration.default")),
                    Integer.parseInt(configProperties.getProperty(actorName + ".images.frame.duration.running")),
                    Integer.parseInt(configProperties.getProperty(actorName + ".health")),
                    Float.parseFloat(configProperties.getProperty(actorName + ".movement.speed")),
                    Float.parseFloat(configProperties.getProperty(actorName + ".movement.speed.multiplier")),
                    Boolean.parseBoolean(configProperties.getProperty(actorName + ".movement.boundaryBlocked"))
            );

            actorConfigurationMap.put(actorType, actorConfiguration);
            logger.info("Successfully built enemy configuration for " + actorName);
        }

        logger.info("Successfully built enemy configuration map.");
        return actorConfigurationMap;
    }


}
