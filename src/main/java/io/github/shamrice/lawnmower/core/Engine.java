package io.github.shamrice.lawnmower.core;

import io.github.shamrice.lawnmower.actors.ActorType;
import io.github.shamrice.lawnmower.actors.Direction;
import io.github.shamrice.lawnmower.actors.EnemyActor;
import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.configuration.Configuration;
import io.github.shamrice.lawnmower.configuration.ConfigurationBuilder;
import io.github.shamrice.lawnmower.core.collision.CollisionHandler;
import io.github.shamrice.lawnmower.inventory.Inventory;
import io.github.shamrice.lawnmower.inventory.InventoryItem;
import io.github.shamrice.lawnmower.inventory.InventoryItemType;
import io.github.shamrice.lawnmower.state.GameState;
import org.apache.log4j.Logger;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Engine extends BasicGame {

    private final static Logger logger = Logger.getLogger(Engine.class);
    private final static float STEP = 1; //32;

    // TODO : change this to auto configure.
    private final static String ASSET_LOCATION = "/home/erik/Documents/github/lawnMowerMania/LawnMowerMania/src/main/resources/assets/";
    private final static String MAPS_LOCATION = "/home/erik/Documents/github/lawnMowerMania/LawnMowerMania/src/main/resources/maps/";

    private float delta;
    private boolean isMouseButtonDown = false;
    private CollisionHandler collisionHandler;
    private PlayerActor player;
    private EnemyActor enemyActor; //TODO : will be moved into it's own manager and will be a list for # of enemies in level.
    private Inventory inventory;
    private TrueTypeFont FONT;

    List<Rectangle> inventoryHitBoxList = new ArrayList<>(); // TODO : add sprites to inventory and use box as click area.
    private InventoryItem equippedInventoryItem = null;

    private String debugMessage = "";


    public Engine() {
        super("Lawn Mower Mania");
    }

    @Override
    public void init(GameContainer container) throws SlickException {

        Configuration configuration = ConfigurationBuilder.buildConfiguration();


        //TODO : move to configuration
        java.awt.Font font = new java.awt.Font("Ariel", java.awt.Font.PLAIN, 12);
        FONT = new TrueTypeFont(font, true);

        // TODO : assets should be based on config and built/displayed per level.
        Image playerImage = new Image(ASSET_LOCATION + "lawnmower1.png");
        player = new PlayerActor(playerImage, 64, 32, STEP);

        // TODO : assets should be based on config and built/displayed per level.
        Image beeImage = new Image(ASSET_LOCATION + "bee.png");
        enemyActor = new EnemyActor(ActorType.BEE, beeImage, 716, 256, 100, 0.25f);


        // TODO : assets should be based on config and built/displayed per level.
        TiledMap map = new TiledMap(MAPS_LOCATION + "test3.tmx");

        collisionHandler = new CollisionHandler();

        int collisionEntries = collisionHandler.setUpCollisionMap(map);

        inventory = new Inventory(configuration.getInventoryItemLookUp());

        GameState.getInstance().setConfiguration(configuration);
        GameState.getInstance().setCurrentTiledMap(1, map);
        GameState.getInstance().setMowTilesRemaining((map.getWidth() * map.getHeight()) - collisionEntries);
        GameState.getInstance().setRunning(true);


        // TODO : inventory will eventually be updated via the shop before a level.
        inventory.addInventoryItem(InventoryItemType.GRASS_SEED);
        inventory.addInventoryItem(InventoryItemType.GRASS_SEED);
        inventory.addInventoryItem(InventoryItemType.NOT_FOUND);

        logger.info("Init complete.");
    }

    @Override
    public void update(GameContainer container, int delta) {

        float playerSpeed = player.getMovementSpeed();

        Input userInput = container.getInput();

        if (userInput.isKeyDown(Input.KEY_Q) || userInput.isKeyDown(Input.KEY_ESCAPE)) {
            GameState.getInstance().setRunning(false);
        }

        /* arrow keys */
        if (userInput.isKeyPressed(Input.KEY_LEFT)) {
            player.setDirection(Direction.LEFT);
        }

        if (userInput.isKeyPressed(Input.KEY_RIGHT)) {
            player.setDirection(Direction.RIGHT);
        }

        if (userInput.isKeyPressed(Input.KEY_UP)) {
            player.setDirection(Direction.UP);
        }

        if (userInput.isKeyPressed(Input.KEY_DOWN)) {
            player.setDirection(Direction.DOWN);
        }

        if (userInput.isKeyDown(Input.KEY_SPACE)) {
            if (player.useStamina(1)) {
                playerSpeed *= 1.75;
            }
        } else if (!userInput.isKeyDown(Input.KEY_SPACE)) {
            player.recoverStamina(0.2f);
        }

        float tempStepX = 0;
        float tempStepY = 0;

        switch (player.getDirection()) {
            case LEFT:
                tempStepX = -playerSpeed;
                break;
            case RIGHT:
                tempStepX = playerSpeed;
                break;
            case UP:
                tempStepY = -playerSpeed;
                break;
            case DOWN:
                tempStepY = playerSpeed;
                break;
        }

        if (!collisionHandler.checkCollision(player, tempStepX, tempStepY)) {
            player.updateXY(tempStepX, tempStepY);
        }

        this.delta = delta;

        if (GameState.getInstance().getMowTilesRemaining() <= 0) {
            logger.info("MOW COMPLETE");
            container.exit();
        }

        if (!GameState.getInstance().isRunning())
            container.exit();

        //TODO : move into it's own manager for handling enemy movements
        moveEnemies();

        //TODO : will have list of enemies to iterate through and check collisions between.
        if (collisionHandler.checkCollisionBetweenActors(player, enemyActor)) {
            logger.info("GAME OVER. You have been killed by a " + enemyActor.getActorType().name());
            GameState.getInstance().setRunning(false);
        }
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {

        //TODO : image assets need to be built by configuration not hard coded.

        GameState.getInstance().getCurrentTiledMap().render(0,0);


        //test.drawString(810, 30, "HELLO");

        displayInventory(g, 100);

        g.drawString("x: " + player.getX() + " y: " + player.getY() + " delta: " + delta, 100, 1);
        FONT.drawString(810, 10, "Score: " + player.getScore());
        FONT.drawString(810, 30, "Grass to cut: " + GameState.getInstance().getMowTilesRemaining());

        Color staminaTextColor = Color.green;

        if (player.getStamina() <= 10)
            staminaTextColor = Color.red;
        else if (player.getStamina() > 10 && player.getStamina() <= 50)
            staminaTextColor = Color.yellow;
        else if (player.getStamina() > 50 && player.getStamina() <= 75)
            staminaTextColor = Color.green;
        else
            staminaTextColor = Color.white;

        FONT.drawString(810, 50,"Stamina: " + (int)player.getStamina() + "/100", staminaTextColor);

        g.drawImage(player.getSpriteImage(), player.getX(), player.getY());
        g.drawImage(enemyActor.getSpriteImage(), enemyActor.getX(), enemyActor.getY());


    }


    @Override
    public void mousePressed(int button, int x, int y) {
        if (button == 0) {
            isMouseButtonDown = true;
        }

        //TODO : this should check to make sure the user is pressing on the inventory item in the inventory.
        equippedInventoryItem = inventory.useInventoryItem(InventoryItemType.GRASS_SEED);
        logger.debug("Mouse button " + button + " pressed at " + x + ", " + y);
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        if (button == 0) {
            isMouseButtonDown = false;
        }

        if (equippedInventoryItem != null) {
            if (collisionHandler.checkMouseCollision(equippedInventoryItem.getInventoryItemType(), x, y)) {
                logger.info("Used item " + equippedInventoryItem.getName() + " at " + x + ", " + y);
                equippedInventoryItem = null;
            }
        }

        logger.debug("Mouse button " + button + " released at " + x + ", " + y);
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        logger.debug("CLICKED:"+x+","+y+" "+clickCount);
    }


    /**
     * Draws inventory information on left side of screen.
     * @param g Graphics object to render text with.
     */
    private void displayInventory(Graphics g, int y) {

        int x = 810;

        //display equipped item (if any)
        String equippedItemName = "None";
        if (equippedInventoryItem != null) {
            equippedItemName = equippedInventoryItem.getName();
        }
        FONT.drawString(x, y, "Equipped: " + equippedItemName);
        y += 25;

        //type full inventory. (to be replaced with icons.
        FONT.drawString(x, y, "Inventory:");
        y += 25;
        x += 10;

        for (InventoryItem inventoryItem : inventory.getAllInventoryItems()) {
            FONT.drawString(x, y, inventoryItem.getName());
            y += 15;
            FONT.drawString(x, y, inventoryItem.getDescription());
            y += 15;
            FONT.drawString(x,  y, "Value: " + inventoryItem.getValue());
            y += 15;
            FONT.drawString(x, y, "Items left: " + inventory.getNumberOfItemsRemaining(inventoryItem.getInventoryItemType()));
            y += 30;
        }
    }

    //TODO : will be moved into it's own enemy manager. This is temp for debug.
    private void moveEnemies() {

        float deltaX = 0;
        float deltaY = 0;

        if (player.getX() > enemyActor.getX()) {
            deltaX = enemyActor.getMovementSpeed();
        } else {
            deltaX = -enemyActor.getMovementSpeed();
        }

        if (player.getY() > enemyActor.getY()) {
            deltaY = enemyActor.getMovementSpeed();
        } else {
            deltaY = -enemyActor.getMovementSpeed();
        }

        enemyActor.updateXY(deltaX, deltaY);
    }
}
