package io.github.shamrice.lawnmower.core.collision;

import io.github.shamrice.lawnmower.actors.Actor;
import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.inventory.InventoryItemType;
import io.github.shamrice.lawnmower.state.GameState;
import org.apache.log4j.Logger;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.tiled.TiledMap;

public class CollisionHandler {

    private final static int TILE_WIDTH = 32; //TODO : can this be replaced with map.getTileWidth()?
    private final static Logger logger = Logger.getLogger(CollisionHandler.class);

    private Rectangle collisionMap[][] = null;

    public CollisionHandler() {}

    /**
     * Sets up collision map used in the check collision method.
     * @param map TiledMap to use to base collision map off of.
     * @return Returns number of collision entries in generated collision map.
     */
    public int setUpCollisionMap(TiledMap map) {

        int collisionEntries = 0;
        collisionMap = new Rectangle[map.getWidth()][map.getHeight()];
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int tileId = map.getTileId(x, y, 1);
                if (tileId > 0) {
                    collisionMap[x][y] = new Rectangle(x * TILE_WIDTH, y * TILE_WIDTH, 16, 16);
                    collisionEntries++;
                }
            }
        }

        return collisionEntries;
    }

    /**
     * Checks to see if player has collided with a tile and handle it. TODO : to be refactored.
     * @param player
     * @param deltaX
     * @param deltaY
     * @return Returns true on collision or false if no collision is happening.
     * @throws IllegalStateException Exception is thrown if there is no collision map set up for the collision handler.
     */
    public boolean checkCollision(PlayerActor player, float deltaX, float deltaY) throws IllegalStateException {

        if (this.collisionMap == null)
            throw new IllegalStateException("No collision map set up to check collision against.");

        float attemptedX = player.getX() + deltaX;
        float attemptedY = player.getY() + deltaY;
        int scoreDelta = 0;


        logger.debug("deltaX: " + deltaX + " deltaY: " + deltaY + " x: " + player.getX() + " y: " + player.getY()
                + " attemptedX: " + attemptedX + " attemptedY: " + attemptedY);

        Shape tempPlayerShape = new Rectangle(attemptedX, attemptedY, 16,   16);

        TiledMap map = GameState.getInstance().getCurrentTiledMap();

        for (int j = 0; j < collisionMap.length; j++) {
            for (int k = 0; k < collisionMap[j].length; k++) {
                if (collisionMap[j][k] != null) {
                    if (collisionMap[j][k].intersects(tempPlayerShape)) {

                        logger.debug("COLLISION : " + collisionMap[j][k].getMinX() + ", " + collisionMap[j][k].getMinY()
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
            GameState.getInstance().decreaseMowTilesRemaining();
            logger.debug("Num mow tiles remaining: " + GameState.getInstance().getMowTilesRemaining());
        } else if (currentTileId > 2) {
            scoreDelta -= 25;
        }

        player.changeScore(scoreDelta);
        GameState.getInstance().setCurrentTiledMap(GameState.getInstance().getCurrentLevel(), map);

        return false;
    }

    /**
     * Check collision between mouse x,y and map x,y and uses item. TODO : NEEDS TO BE REFACTORED!!!
     * @param itemTypeUsed Item type being used.
     * @param mouseX Mouse X screen location
     * @param mouseY Mouse Y screen location
     * @return returns true if item was able to be used or false if it was not.
     */
    public boolean checkMouseCollision(InventoryItemType itemTypeUsed, int mouseX, int mouseY) {

        Shape tempClickShape = new Rectangle(mouseX, mouseY, 16,   16);
        TiledMap map = GameState.getInstance().getCurrentTiledMap();

        for (int j = 0; j < collisionMap.length; j++) {
            for (int k = 0; k < collisionMap[j].length; k++) {
                if (collisionMap[j][k] != null) {
                    if (collisionMap[j][k].intersects(tempClickShape)) {

                        logger.debug("MOUSE COLLISION WITH BORDER: " + collisionMap[j][k].getMinX() + ", " + collisionMap[j][k].getMinY()
                                + " - " + collisionMap[j][k].getMaxX() + ", " + collisionMap[j][k].getMaxY());

                        return false; //clicked on border. Not level area collision.
                    }
                }
            }
        }

        //TODO : this has become a weird combination of checking collision and actually using the item that it has collided with..

        float tempX = (float)mouseX / 32;
        float tempY = (float)mouseY / 32;

        if (tempX > map.getWidth() || tempY > map.getHeight()) {
            return false;
        } else {
            logger.debug("Mouse collision at: " + (int)tempX + ", " + (int)tempY);

            switch (itemTypeUsed) {
                case GRASS_SEED:
                    map.setTileId((int) tempX, (int) tempY, 1, 1);
                    return true;
                default:
                    logger.info("Cannot use item " + itemTypeUsed.name());
            }

        }
        return false;
    }

    /**
     * Checks collision between two actor objects. Returns true if they are currently colliding.
     * @param actor1 Actor to test collision with
     * @param actor2 Second actor to test collision with first actor.
     * @return Will return true if there is a collision or false if there is not.
     */
    public boolean checkCollisionBetweenActors(Actor actor1, Actor actor2) {
        Shape tempShape = new Rectangle(actor1.getX(), actor1.getY(), 16,   16);
        Shape tempShape2 = new Rectangle(actor2.getX(), actor2.getY(), 16,   16);

        return tempShape.intersects(tempShape2);

    }
}
