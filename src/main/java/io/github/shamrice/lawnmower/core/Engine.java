package io.github.shamrice.lawnmower.core;

import io.github.shamrice.lawnmower.actors.*;
import io.github.shamrice.lawnmower.common.Constants;
import io.github.shamrice.lawnmower.configuration.Configuration;
import io.github.shamrice.lawnmower.configuration.ConfigurationBuilder;
import io.github.shamrice.lawnmower.core.collision.CollisionHandler;
import io.github.shamrice.lawnmower.core.graphics.GraphicsManager;
import io.github.shamrice.lawnmower.core.graphics.Panel;
import io.github.shamrice.lawnmower.core.level.LevelManager;
import io.github.shamrice.lawnmower.inventory.Inventory;
import io.github.shamrice.lawnmower.inventory.InventoryItem;
import io.github.shamrice.lawnmower.inventory.InventoryItemType;
import io.github.shamrice.lawnmower.state.GameState;
import io.github.shamrice.lawnmower.state.LevelState;
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
    //private EnemyActor enemyActor; //TODO : will be moved into it's own manager and will be a list for # of enemies in level.
    private GraphicsManager graphicsManager;
    private LevelManager levelManager;

    List<Rectangle> inventoryHitBoxList = new ArrayList<>(); // TODO : add sprites to inventory and use box as click area.

    private GameState state = GameState.getInstance();
    private LevelState levelState = LevelState.getInstance();

    public Engine() {
        super("Lawn Mower Mania");
    }

    @Override
    public void init(GameContainer container) throws SlickException {

        Configuration configuration = ConfigurationBuilder.buildConfiguration();

        // TODO : assets should be based on config and built/displayed per level.
        Image playerImage = new Image(ASSET_LOCATION + "lawnmower1.png");
        player = new PlayerActor(playerImage, 64, 50, STEP);

        // TODO : assets should be based on config and built/displayed per level.
        Image beeImage = new Image(ASSET_LOCATION + "bee.png");
        EnemyActor enemyActor = new EnemyActor(ActorType.BEE, beeImage, 716, 256, 100, 0.25f);

        // TODO : assets should be based on config and built/displayed per level.
        TiledMap map = new TiledMap(MAPS_LOCATION + "test3.tmx");

        collisionHandler = new CollisionHandler();

        int numTilesNotToMow = collisionHandler.setUpCollisionMap(map);

        Inventory inventory = new Inventory(configuration.getInventoryItemLookUp());

        // TODO : inventory will eventually be updated via the shop before a level.
        for (int i = 0; i < 100; i++) {
            inventory.addInventoryItem(InventoryItemType.GRASS_SEED);
        }

        List<Actor> currentActors = new ArrayList<>();
        currentActors.add(enemyActor);
        currentActors.add(player);

        levelState.setCurrentActors(currentActors);
        levelState.setCurrentTiledMap(1, map);
        levelState.setMowTilesRemaining((map.getWidth() * map.getHeight()) - numTilesNotToMow);

        state.setConfiguration(configuration);
        state.setRunning(true);
        state.setInventory(inventory);
        state.setCurrentPanel(Panel.LEVEL);

        graphicsManager = new GraphicsManager(configuration.getTrueTypeFont(), player);
        levelManager = new LevelManager();

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
            levelManager.updateMowedTile(player);
        }

        this.delta = delta;

        if (levelState.getMowTilesRemaining() <= 0) {
            logger.info("MOW COMPLETE :: Score : " + state.getScore());
            container.exit();
        }

        if (!state.isRunning() || !player.isAlive())
            container.exit();

        //move enemies in the level.
        levelManager.moveEnemies(player, levelState.getCurrentEnemyActors());

        //check collision between all current enemies and player.
        // TODO : migth switch back later to regular streamed foreach instead of parallel if threading issues arise.
        levelState.getCurrentEnemyActors().parallelStream().forEach(
                enemy -> {
                    if (collisionHandler.checkCollisionBetweenActors(player, enemy)) {
                        logger.info("GAME OVER. You have been killed by a " + enemy.getActorType().name());
                        GameState.getInstance().setRunning(false);
                    }
                }
        );
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {

        graphicsManager.setGraphics(g);
        graphicsManager.displayPanel(state.getCurrentPanel(), levelState.getCurrentActors());

        //TODO : image assets need to be built by configuration not hard coded.

    }


    @Override
    public void mousePressed(int button, int x, int y) {
        if (button == 0) {
            isMouseButtonDown = true;
        }

        GameState state = GameState.getInstance();

        //TODO : this should check to make sure the user is pressing on the inventory item in the inventory.
        //TODO : this should check to make sure item attempted to be equipped is not the item already equipped. if
        //TODO : it is, add back the current equipped item to the inventory and equip the new item.
        if (state.getInventory().getEquippedInventoryItem() == null) {
            state.getInventory().setEquippedInventoryItem(state.getInventory().useInventoryItem(InventoryItemType.GRASS_SEED));
        }
        logger.debug("Mouse button " + button + " pressed at " + x + ", " + y);
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        if (button == 0) {
            isMouseButtonDown = false;
        }

        GameState state = GameState.getInstance();
        InventoryItem equippedItem = state.getInventory().getEquippedInventoryItem();
        if (equippedItem != null) {
            if (collisionHandler.checkMouseCollision(x, y)) {
                if (levelManager.useInventoryItemOnMap(equippedItem.getInventoryItemType(), x, y)) {
                    logger.info("Used item " + state.getInventory().getEquippedInventoryItem().getName() + " at " + x + ", " + y);
                    state.getInventory().setEquippedInventoryItem(null);
                }
            }
        }

        logger.debug("Mouse button " + button + " released at " + x + ", " + y);
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        logger.debug("CLICKED:"+x+","+y+" "+clickCount);
    }

}
