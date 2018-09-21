package io.github.shamrice.lawnmower.core.collision;

import io.github.shamrice.lawnmower.actors.Actor;
import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.common.TileType;
import io.github.shamrice.lawnmower.inventory.InventoryItemType;
import io.github.shamrice.lawnmower.state.GameState;
import io.github.shamrice.lawnmower.state.LevelState;
import org.apache.log4j.Logger;
import org.newdawn.slick.Game;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.tiled.TiledMap;

public class CollisionHandler {

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
                    collisionMap[x][y] = new Rectangle(x * map.getTileWidth(), y * map.getTileWidth(), 16, 16);
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

        Shape tempPlayerShape = new Rectangle(attemptedX, attemptedY, 16,   16);

        TiledMap map = LevelState.getInstance().getCurrentTiledMap();

        for (int j = 0; j < collisionMap.length; j++) {
            for (int k = 0; k < collisionMap[j].length; k++) {
                if (collisionMap[j][k] != null) {
                    if (collisionMap[j][k].intersects(tempPlayerShape)) {

                        logger.debug("COLLISION : " + collisionMap[j][k].getMinX() + ", " + collisionMap[j][k].getMinY()
                                + " - " + collisionMap[j][k].getMaxX() + ", " + collisionMap[j][k].getMaxY());

                        return handleCollisionWithMapBoundry(j, k);

                    }
                }
            }
        }

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
        TiledMap map = LevelState.getInstance().getCurrentTiledMap();

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
                    map.setTileId((int) tempX, (int) tempY, 1, TileType.CUT_GRASS.getId());
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

    public void updateMowedTile(PlayerActor player) {

        TiledMap map = LevelState.getInstance().getCurrentTiledMap();
        int scoreDelta = 0;
        int previousMapX = player.getPreviousMapX();
        int previousMapY = player.getPreviousMapY();
        int currentTileId = map.getTileId(player.getMapX(), player.getMapY(), 1);

        //make sure player is walking over a new tile and not the one they are already on.
        if (previousMapX != player.getMapX() || previousMapY != player.getMapY()) {

            logger.info("PreviousMapXY: " + previousMapX + ", " + previousMapY +
                    " :: playerMapXY: " + player.getMapX() + ", " + player.getMapY() +
                    " :: currentTileId: " + currentTileId);

            if (currentTileId < TileType.DEAD_GRASS.getId()) {
                currentTileId++;
            }

            if (currentTileId == TileType.UNCUT_GRASS.getId())
                currentTileId = TileType.CUT_GRASS.getId();

            map.setTileId(
                    player.getMapX(),
                    player.getMapY(),
                    1,
                    currentTileId
            );

            if (currentTileId == TileType.CUT_GRASS.getId()) {
                scoreDelta += 50;
                LevelState.getInstance().decreaseMowTilesRemaining();
                logger.debug("Num mow tiles remaining: " + LevelState.getInstance().getMowTilesRemaining());
            } else if (currentTileId > TileType.CUT_GRASS.getId()) {
                scoreDelta -= 25;
            }

            GameState.getInstance().changeScore(scoreDelta);

        }
    }

    private boolean handleCollisionWithMapBoundry(int mapX, int mapY) {

        TiledMap map = LevelState.getInstance().getCurrentTiledMap();
        int currentTileId = map.getTileId(mapX, mapY, 1);

        if (currentTileId == TileType.FLOWERS.getId()) {
            map.setTileId(mapX, mapY, 1, TileType.OVER_MOWED_GRASS.getId());
            GameState.getInstance().changeScore(-500);
            return false;

            //TODO : currently will cause grass to go all the way to dead grass because hit detection is checked every loop.
        } else if (currentTileId == TileType.OVER_MOWED_GRASS.getId() || currentTileId == TileType.DEAD_GRASS.getId()) {
            GameState.getInstance().changeScore(-50);
            return false;
        }

        return true;
    }
}
