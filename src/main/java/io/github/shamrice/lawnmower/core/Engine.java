package io.github.shamrice.lawnmower.core;

import io.github.shamrice.lawnmower.actors.*;
import io.github.shamrice.lawnmower.configuration.Configuration;
import io.github.shamrice.lawnmower.configuration.ConfigurationBuilder;
import io.github.shamrice.lawnmower.core.graphics.GraphicsManager;
import io.github.shamrice.lawnmower.core.graphics.Panel;
import io.github.shamrice.lawnmower.core.level.LevelManager;
import io.github.shamrice.lawnmower.inventory.Inventory;
import io.github.shamrice.lawnmower.inventory.InventoryItem;
import io.github.shamrice.lawnmower.inventory.InventoryItemType;
import io.github.shamrice.lawnmower.state.GameState;
import io.github.shamrice.lawnmower.state.LevelState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Engine extends BasicGame {

    private final static Logger logger = LogManager.getLogger();

    private float delta;
    private boolean isMouseButtonDown = false;
    //private CollisionHandler collisionHandler;
    private PlayerActor player;
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

        Configuration configuration = null;

        try {
            configuration = ConfigurationBuilder.buildConfiguration();
        } catch (IOException ioExc) {
            logger.error(ioExc.getMessage(), ioExc);
            logger.error("Failed to build configuration... exiting.");
            System.exit(-1);
        }

        Inventory inventory = new Inventory(configuration.getInventoryItemLookUp());
        player = new PlayerActor(configuration.getActorConfiguration(ActorType.PLAYER), 64, 50);

        state.setConfiguration(configuration);
        state.setRunning(true);
        state.setInventory(inventory);
        state.setCurrentPanel(Panel.LEVEL);

        levelState.addActorToCurrentActors(player);

        graphicsManager = new GraphicsManager(configuration.getTrueTypeFont(), player);

        levelManager = new LevelManager();
        levelManager.initLevel();

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
                playerSpeed *= player.getMovementSpeedMultiplier();
                player.setSpriteAnimationFrameDuration(player.getRunningFrameDuration());
            }
        } else if (!userInput.isKeyDown(Input.KEY_SPACE)) {
            player.recoverStamina(0.2f);
            player.setSpriteAnimationFrameDuration(player.getDefaultFrameDuration());
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

        if (!levelState.getCollisionHandler().checkCollision(player, tempStepX, tempStepY)) {
            player.updateXY(tempStepX, tempStepY);
            levelManager.updateMowedTile(player);
        }

        this.delta = delta;

        if (levelState.getMowTilesRemaining() <= 0) {
            logger.info("MOW COMPLETE :: Score : " + state.getScore());
            //TODO : increase level, show score, shop and then load new level and reset display.
            container.exit();
        }

        if (!state.isRunning() || !player.isAlive())
            container.exit();

        //move enemies in the level.
        levelManager.moveEnemies(levelState.getCollisionHandler(), player, levelState.getCurrentEnemyActors());

        //check collision between all current enemies and player.
        levelState.getCurrentEnemyActors().parallelStream().forEach(
                enemy -> {
                    if (levelState.getCollisionHandler().checkCollisionBetweenActors(player, enemy)) {
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
            if (levelState.getCollisionHandler().checkMouseCollision(x, y)) {
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
