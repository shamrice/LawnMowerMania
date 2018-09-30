package io.github.shamrice.lawnmower.configuration;

// TODO : configuration builder builds config. Build this out.

import io.github.shamrice.lawnmower.actors.ActorType;
import io.github.shamrice.lawnmower.configuration.actors.ActorConfiguration;
import io.github.shamrice.lawnmower.configuration.levels.LevelConfiguration;
import io.github.shamrice.lawnmower.configuration.levels.LevelEnemyConfiguration;
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
    private static Properties levelProperties = new Properties();

    //TODO : use relative path.
    private static String configurationLocation = "/home/erik/Documents/github/lawnMowerMania/LawnMowerMania/src/main/resources/config/";
    private static String configFileName = "config.properties";
    private static String levelConfigFileName = "level.properties";
    private static String assetsLocation;
    private static String mapsLocation;

    /**
     * Builds configuration based on configuration files and assets.
     * @return Built configuration.
     */
    public static Configuration buildConfiguration() throws IOException, SlickException {

        logger.info("Building configuration using file: " + configurationLocation + configFileName);
        logger.info("Building level configuration using file: " + configurationLocation + levelConfigFileName);

        File configFile = new File(configurationLocation + configFileName);
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

        configInput = new FileInputStream(new File(configurationLocation + levelConfigFileName).getPath());
        levelProperties.load(configInput);
        configInput.close();

        return new Configuration(
                buildInventoryItemLookup(),
                buildTrueTypeFont(),
                buildActorConfigurationMap(),
                buildLevelConfiguration()
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
            logger.info("Successfully built actor configuration for " + actorName);
        }

        logger.info("Successfully built actor configuration map.");
        return actorConfigurationMap;
    }

    private static List<LevelConfiguration> buildLevelConfiguration() {

        List<LevelConfiguration> levelConfigurations = new LinkedList<>();

        int numLevels = Integer.parseInt(levelProperties.getProperty("level.count"));

        //for the amount of levels configured. read the level properties for each
        for (int i = 0; i < numLevels; i++) {
            String filename = mapsLocation + levelProperties.getProperty("level." + i + ".map.filename");
            List<LevelEnemyConfiguration> levelEnemyConfigurations = new ArrayList<>();

            int numEnemies = Integer.parseInt(levelProperties.getProperty("level." + i + ".enemy.count"));

            //for number of enemies in level, get their information.
            for (int e = 0; e < numEnemies; e++) {
                LevelEnemyConfiguration levelEnemyConfiguration = new LevelEnemyConfiguration(
                        ActorType.valueOf(levelProperties.getProperty("level." + i + ".enemy." + e + ".type").toUpperCase()),
                        Float.parseFloat(levelProperties.getProperty("level." + i + ".enemy." + e + ".x")),
                        Float.parseFloat(levelProperties.getProperty("level." + i + ".enemy." + e + ".y"))
                );
                levelEnemyConfigurations.add(levelEnemyConfiguration);
            }

            levelConfigurations.add(new LevelConfiguration(filename, levelEnemyConfigurations));
        }

        return levelConfigurations;
    }


}
