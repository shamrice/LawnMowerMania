package io.github.shamrice.lawnmower.core;


import io.github.shamrice.lawnmower.actors.PlayerActor;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.tiled.TiledMap;

import java.awt.Font;


public class Engine extends BasicGame {

    //TODO : move these to their own objects instead of class properties.
    private final float step = 32;
    private boolean isRunning = false;
    private float delta;
    private TiledMap map;
    private Rectangle collisionMap[][];
    private int numTilesRemaining;

    private PlayerActor player;


    public Engine() {
        super("Lawn Mower Mania");
    }

    @Override
    public void init(GameContainer container) throws SlickException {

        // TODO : so much to say about this method there's no room...

        Image playerImage = new Image("/home/erik/Documents/github/lawnmower/src/main/resources/assets/lawnmower1.png");
        player = new PlayerActor(playerImage, 64, 32);

        isRunning = true;

        System.out.println("Creating map");
        try {

            // TODO : assets should be based on config and built/displayed per level.
            map = new TiledMap("/home/erik/Documents/github/lawnmower/src/main/resources/test3.tmx");

            System.out.println("Done creating map");

            // TODO : this isn't terrrrrrrrrible I guess. Tile/sprite width should be defined instead of just "32"
            int collisionEntries = 0;
            collisionMap = new Rectangle[map.getWidth()][map.getHeight()];
            for (int x = 0; x < map.getWidth(); x++) {
                for (int y = 0; y < map.getHeight(); y++) {
                    int tileId = map.getTileId(x, y, 1);
                    if (tileId > 0) {
                        collisionMap[x][y] = new Rectangle(x * 32, y * 32, 16, 16);
                        collisionEntries++;
                    }
                }
            }

            numTilesRemaining = (map.getWidth() * map.getHeight()) - collisionEntries;


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {

        //TODO : decide between key press and isKeyDown game play.
        //TODO : if isKeyDown, regulate player speed.

        Input userInput = container.getInput();
        if (userInput.isKeyDown(Input.KEY_Q) || userInput.isKeyDown(Input.KEY_ESCAPE)) {
            isRunning = false;
        }

        if (userInput.isKeyPressed(Input.KEY_LEFT)) {
            if (!checkCollision(-step, 0))
                player.updateXY(-step, 0);
                //x -= step;
        }

        if (userInput.isKeyPressed(Input.KEY_RIGHT))
            if (!checkCollision(step, 0))
                player.updateXY(step, 0);
                ////x += step;

        if (userInput.isKeyPressed(Input.KEY_UP))
            if (!checkCollision(0, -step))
                player.updateXY(0, -step);
               // y -= step;

        if (userInput.isKeyPressed(Input.KEY_DOWN))
            if (!checkCollision(0, step))
                player.updateXY(0, step);
                //y += step;

        this.delta = delta;

        if (numTilesRemaining <= 0) {
            System.out.println("MOW COMPLETE");
            container.exit();
        }

        if (!isRunning)
            container.exit();
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {

        //TODO : image assets need to be built by configuration not hard coded.
        map.render(0,0);
        //Image playerImage = new Image("/home/erik/Documents/github/lawnmower/src/main/resources/assets/lawnmower1.png");

        g.drawString("x: " + player.getX() + " y: " + player.getY() + " delta: " + delta, 100, 1);
        g.drawString("Score: " + player.getScore(), 810, 10);
        g.drawString("Grass to cut: " + numTilesRemaining, 810, 30);

        //g.drawImage(playerImage, x, y);

        g.drawImage(player.getSpirteImage(), player.getX(), player.getY());

        //Font font = new Font("Ariel", Font.PLAIN, 12);
        //TrueTypeFont test = new TrueTypeFont(font, true);
        //test.drawString(810, 30, "HELLO");


    }

    private boolean checkCollision(float deltaX, float deltaY) {

        float attemptedX = player.getX() + deltaX;
        float attemptedY = player.getY() + deltaY;
        int scoreDelta = 0;


        System.out.println("deltaX: " + deltaX + " deltaY: " + deltaY + " x: " + player.getX() + " y: " + player.getY()
                + " attemptedX: " + attemptedX + " attemptedY: " + attemptedY);

        Shape tempPlayerShape = new Rectangle(attemptedX, attemptedY, 16,   16);


        for (int j = 0; j < collisionMap.length; j++) {
            for (int k = 0; k < collisionMap[j].length; k++) {
                if (collisionMap[j][k] != null) {
                    if (collisionMap[j][k].intersects(tempPlayerShape)) {
                        System.out.println("COLLISION : " + collisionMap[j][k].getMinX() + ", " + collisionMap[j][k].getMinY()
                                + " - " + collisionMap[j][k].getMaxX() + ", " + collisionMap[j][k].getMaxY());

                        //TODO : flowers collision handled inside of a boolean method for area collision is gross. though
                        //TODO : looping through this loop multiple times is pretty gross as well. maybe make flowers an
                        //TODO : object instead of modifying them based on tilemap? (have generic "flower bed" tile underneath.)
                        if (map.getTileId(j, k, 1) == 6) {
                            map.setTileId(j, k, 1, 3);
                            scoreDelta -= 500;
                        } else {

                            return true;
                        }
                    }
                }
            }
        }

        //TODO : this logic is terrible.. refactor to own method and come up with something better.
        //TODO : tile ids vary based on tileset... but somehow need to be consistent throughout game....
        // VERY HACKED way to test grass changing after multiple mows.
        int mapX = (int)(attemptedX / 32);
        int mapY = (int)(attemptedY / 32);
        int currentTileId = map.getTileId(mapX, mapY, 1);

        if (currentTileId < 4) {
            currentTileId++;
        }

        if (currentTileId == 1)
            currentTileId = 2;

        map.setTileId(
                mapX,
                mapY,
                1,
                currentTileId
        );

        if (currentTileId == 2){
            scoreDelta += 50;
            numTilesRemaining--;
            System.out.println("Num mow tiles remaining: " + numTilesRemaining);
        } else if (currentTileId > 2) {
            scoreDelta -= 25;
        }

        player.changeScore(scoreDelta);

        return false;

    }
}
