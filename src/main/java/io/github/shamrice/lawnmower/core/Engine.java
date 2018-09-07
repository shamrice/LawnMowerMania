package io.github.shamrice.lawnmower.core;

import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.configuration.Configuration;
import io.github.shamrice.lawnmower.configuration.ConfigurationBuilder;
import io.github.shamrice.lawnmower.core.collision.CollisionHandler;
import io.github.shamrice.lawnmower.inventory.Inventory;
import io.github.shamrice.lawnmower.state.GameState;
import org.newdawn.slick.*;
import org.newdawn.slick.tiled.TiledMap;

public class Engine extends BasicGame {

    private static final float STEP = 32;

    private float delta;
    private CollisionHandler collisionHandler;
    private PlayerActor player;
    private Inventory inventory;

    public Engine() {
        super("Lawn Mower Mania");
    }

    @Override
    public void init(GameContainer container) throws SlickException {

        Configuration configuration = ConfigurationBuilder.buildConfiguration();

        Image playerImage = new Image("/home/erik/Documents/github/lawnmower/src/main/resources/assets/lawnmower1.png");
        player = new PlayerActor(playerImage, 64, 32);

        // TODO : assets should be based on config and built/displayed per level.
        TiledMap map = new TiledMap("/home/erik/Documents/github/lawnmower/src/main/resources/test3.tmx");

        collisionHandler = new CollisionHandler();

        int collisionEntries = collisionHandler.setUpCollisionMap(map);

        inventory = new Inventory(configuration.getInventoryItemLookUp());

        GameState.getInstance().setConfiguration(configuration);
        GameState.getInstance().setCurrentTiledMap(1, map);
        GameState.getInstance().setMowTilesRemaining((map.getWidth() * map.getHeight()) - collisionEntries);
        GameState.getInstance().setRunning(true);


        System.out.println("Init complete.");
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
            System.out.println("MOW COMPLETE");
            container.exit();
        }

        if (!GameState.getInstance().isRunning())
            container.exit();
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {

        //TODO : image assets need to be built by configuration not hard coded.

        GameState.getInstance().getCurrentTiledMap().render(0,0);

        g.drawString("x: " + player.getX() + " y: " + player.getY() + " delta: " + delta, 100, 1);
        g.drawString("Score: " + player.getScore(), 810, 10);
        g.drawString("Grass to cut: " + GameState.getInstance().getMowTilesRemaining(), 810, 30);

        g.drawImage(player.getSpriteImage(), player.getX(), player.getY());

        //Font font = new Font("Ariel", Font.PLAIN, 12);
        //TrueTypeFont test = new TrueTypeFont(font, true);
        //test.drawString(810, 30, "HELLO");
    }
}
