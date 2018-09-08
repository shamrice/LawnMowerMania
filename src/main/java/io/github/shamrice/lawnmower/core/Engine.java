package io.github.shamrice.lawnmower.core;

import io.github.shamrice.lawnmower.actors.ActorType;
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
    private final static float STEP = 32;

    // TODO : change this to auto configure.
    private final static String ASSET_LOCATION = "/home/erik/Documents/github/lawnMowerMania/LawnMowerMania/src/main/resources/assets/";
    private final static String MAPS_LOCATION = "/home/erik/Documents/github/lawnMowerMania/LawnMowerMania/src/main/resources/maps/";

    private float delta;
    private boolean isMouseButtonDown = false;
    private CollisionHandler collisionHandler;
    private PlayerActor player;
    private EnemyActor enemyActor; //TODO : will be moved into it's own manager and will be a list for # of enemies in level.
    private Inventory inventory;

    List<Rectangle> inventoryHitBoxList = new ArrayList<>(); // TODO : add sprites to inventory and use box as click area.
    private InventoryItem equippedInventoryItem = null;

    private String debugMessage = "";


    public Engine() {
        super("Lawn Mower Mania");
    }

    @Override
    public void init(GameContainer container) throws SlickException {

        Configuration configuration = ConfigurationBuilder.buildConfiguration();

        // TODO : assets should be based on config and built/displayed per level.
        Image playerImage = new Image(ASSET_LOCATION + "lawnmower1.png");
        player = new PlayerActor(playerImage, 64, 32);

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

        //TODO : decide between key press and isKeyDown game play.
        //TODO : if isKeyDown, regulate player speed.


        //TODO : check move the update xy to the end and check collision based on key pressed
        //TODO : instead of having almost the same code on every if block.

        Input userInput = container.getInput();
        if (userInput.isKeyDown(Input.KEY_Q) || userInput.isKeyDown(Input.KEY_ESCAPE)) {
            GameState.getInstance().setRunning(false);
        }

        if (userInput.isKeyPressed(Input.KEY_LEFT)) {
            if (!collisionHandler.checkCollision(player,-STEP, 0))
                player.updateXY(-STEP, 0);
        }

        if (userInput.isKeyPressed(Input.KEY_RIGHT))
            if (!collisionHandler.checkCollision(player,STEP, 0))
                player.updateXY(STEP, 0);

        if (userInput.isKeyPressed(Input.KEY_UP))
            if (!collisionHandler.checkCollision(player,0, -STEP))
                player.updateXY(0, -STEP);

        if (userInput.isKeyPressed(Input.KEY_DOWN))
            if (!collisionHandler.checkCollision(player,0, STEP))
                player.updateXY(0, STEP);

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

        g.drawString("x: " + player.getX() + " y: " + player.getY() + " delta: " + delta, 100, 1);
        g.drawString("Score: " + player.getScore(), 810, 10);
        g.drawString("Grass to cut: " + GameState.getInstance().getMowTilesRemaining(), 810, 30);

        g.drawImage(player.getSpriteImage(), player.getX(), player.getY());
        g.drawImage(enemyActor.getSpriteImage(), enemyActor.getX(), enemyActor.getY());

        displayInventory(g);

   //     System.out.println("DEBUG: " + debugMessage);

        //Font font = new Font("Ariel", Font.PLAIN, 12);
        //TrueTypeFont test = new TrueTypeFont(font, true);
        //test.drawString(810, 30, "HELLO");
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
    private void displayInventory(Graphics g) {
        int y = 60;

        //display equipped item (if any)
        String equippedItemName = "None";
        if (equippedInventoryItem != null) {
            equippedItemName = equippedInventoryItem.getName();
        }
        g.drawString("Equipped: " + equippedItemName, 810, y);
        y += 25;

        //type full inventory. (to be replaced with icons.
        g.drawString("Inventory:", 810, y);
        y += 25;
        for (InventoryItem inventoryItem : inventory.getAllInventoryItems()) {
            g.drawString(inventoryItem.getName(), 820, y);
            y += 15;
            g.drawString(inventoryItem.getDescription(), 820, y);
            y += 15;
            g.drawString("Value: " + inventoryItem.getValue(), 820, y);
            y += 15;
            g.drawString("Items left: " + inventory.getNumberOfItemsRemaining(inventoryItem.getInventoryItemType()), 820, y);
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
