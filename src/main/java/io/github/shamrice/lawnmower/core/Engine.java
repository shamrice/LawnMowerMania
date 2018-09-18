package io.github.shamrice.lawnmower.core;

import io.github.shamrice.lawnmower.actors.*;
import io.github.shamrice.lawnmower.configuration.Configuration;
import io.github.shamrice.lawnmower.configuration.ConfigurationBuilder;
import io.github.shamrice.lawnmower.core.collision.CollisionHandler;
import io.github.shamrice.lawnmower.core.graphics.GraphicsManager;
import io.github.shamrice.lawnmower.core.graphics.Panel;
import io.github.shamrice.lawnmower.inventory.Inventory;
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
    private GraphicsManager graphicsManager;

    List<Rectangle> inventoryHitBoxList = new ArrayList<>(); // TODO : add sprites to inventory and use box as click area.

    private GameState state = GameState.getInstance();

    private String debugMessage = "";


    public Engine() {
        super("Lawn Mower Mania");
    }

    @Override
    public void init(GameContainer container) throws SlickException {

        Configuration configuration = ConfigurationBuilder.buildConfiguration();

        //TODO : move to configuration
        //java.awt.Font font = new java.awt.Font("Ariel", java.awt.Font.PLAIN, 12);
        //FONT = new TrueTypeFont(font, true);

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

        Inventory inventory = new Inventory(configuration.getInventoryItemLookUp());
        // TODO : inventory will eventually be updated via the shop before a level.
        inventory.addInventoryItem(InventoryItemType.GRASS_SEED);
        inventory.addInventoryItem(InventoryItemType.GRASS_SEED);
        inventory.addInventoryItem(InventoryItemType.NOT_FOUND);


        List<Actor> currentActors = new ArrayList<>();
        currentActors.add(enemyActor);
        currentActors.add(player);

        state.setCurrentActors(currentActors);
        //GameState state = GameState.getInstance();
        state.setConfiguration(configuration);
        state.setCurrentTiledMap(1, map);
        state.setMowTilesRemaining((map.getWidth() * map.getHeight()) - collisionEntries);
        state.setRunning(true);
        state.setInventory(inventory);
        state.setCurrentPanel(Panel.LEVEL);

        graphicsManager = new GraphicsManager(configuration.getTrueTypeFont(), player);

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
            logger.info("MOW COMPLETE :: Score : " + player.getScore());
            container.exit();
        }

        if (!GameState.getInstance().isRunning() || !player.isAlive())
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

        graphicsManager.setGraphics(g);
        graphicsManager.displayPanel(state.getCurrentPanel(), state.getCurrentActors());

        //TODO : image assets need to be built by configuration not hard coded.

    }


    @Override
    public void mousePressed(int button, int x, int y) {
        if (button == 0) {
            isMouseButtonDown = true;
        }

        GameState state = GameState.getInstance();
        //TODO : this should check to make sure the user is pressing on the inventory item in the inventory.
        state.setEquippedInventoryItem(state.getInventory().useInventoryItem(InventoryItemType.GRASS_SEED));
        logger.debug("Mouse button " + button + " pressed at " + x + ", " + y);
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        if (button == 0) {
            isMouseButtonDown = false;
        }

        GameState state = GameState.getInstance();
        if (state.getEquippedInventoryItem() != null) {
            if (collisionHandler.checkMouseCollision(state.getEquippedInventoryItem().getInventoryItemType(), x, y)) {
                logger.info("Used item " + state.getEquippedInventoryItem().getName() + " at " + x + ", " + y);
                state.setEquippedInventoryItem(null);
            }
        }

        logger.debug("Mouse button " + button + " released at " + x + ", " + y);
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        logger.debug("CLICKED:"+x+","+y+" "+clickCount);
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
