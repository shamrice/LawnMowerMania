package io.github.shamrice.lawnmower.core.collision;

import io.github.shamrice.lawnmower.actors.Actor;
import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.common.TileType;
import io.github.shamrice.lawnmower.inventory.InventoryItemType;
import io.github.shamrice.lawnmower.state.GameState;
import io.github.shamrice.lawnmower.state.LevelState;
import org.apache.log4j.Logger;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.tiled.TiledMap;

public class CollisionHandler {

    private final static Logger logger = Logger.getLogger(CollisionHandler.class);

    private final static int SPRITE_WIDTH = 32;
    private final static int SPRITE_HEIGHT = 32;
    private final static int MAP_LAYER = 1;
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
                    collisionMap[x][y] = new Rectangle(x * map.getTileWidth(), y * map.getTileWidth(), SPRITE_WIDTH, SPRITE_HEIGHT);
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

        // TODO : collision is off by 1 map X/Y. Based on player sprite upper left corner position.

        if (this.collisionMap == null)
            throw new IllegalStateException("No collision map set up to check collision against.");

        float attemptedX = player.getX() + deltaX;
        float attemptedY = player.getY() + deltaY;
        int attemptedMapX = (int)(attemptedX/ SPRITE_WIDTH);
        int attemptedMapY = (int)(attemptedY / SPRITE_HEIGHT);

        Shape tempPlayerShape = new Rectangle(attemptedX, attemptedY, SPRITE_WIDTH,   SPRITE_HEIGHT);
        Shape collisionTile = collisionMap[attemptedMapX][attemptedMapY];

        if (collisionTile != null && collisionTile.intersects(tempPlayerShape)) {
            logger.debug("attemptedMapXY: " + attemptedMapX + "," + attemptedMapY +
                    " :: collisionTile " + collisionTile.getX() + "," + collisionTile.getY());
            return handlePlayerActorCollisionWithMapBoundary(attemptedMapX, attemptedMapY);
        }

        return false;
    }

    /**
     * Check collision between mouse x,y and map x,y and uses item.
     * @param itemTypeUsed Item type being used.
     * @param mouseX Mouse X screen location
     * @param mouseY Mouse Y screen location
     * @return returns true if item was able to be used or false if it was not.
     */
    public boolean checkMouseCollision(InventoryItemType itemTypeUsed, int mouseX, int mouseY) {

        int attemptedMapX = mouseX / SPRITE_WIDTH;
        int attemptedMapY = mouseY / SPRITE_HEIGHT;

        Shape tempClickShape = new Rectangle(mouseX, mouseY, SPRITE_WIDTH,   SPRITE_HEIGHT);
        Shape collisionShape = collisionMap[attemptedMapX][attemptedMapY];
        TiledMap map = LevelState.getInstance().getCurrentTiledMap();

        //check if click was outside of map
        if (attemptedMapX > map.getWidth() || attemptedMapY > map.getHeight()) {
            return false;
        }

        //check if collision with boundary tile
        if (collisionShape != null && collisionShape.intersects(tempClickShape)) {
            logger.debug("MOUSE COLLISION WITH BORDER: " + attemptedMapX + "," + attemptedMapY);
            return false;
        }

        //no blocking collisions. return if item was usable on level map.
        return useInventoryItemOnMap(itemTypeUsed, attemptedMapX, attemptedMapY, map);
    }

    /**
     * Checks collision between two actor objects. Returns true if they are currently colliding.
     * @param actor1 Actor to test collision with
     * @param actor2 Second actor to test collision with first actor.
     * @return Will return true if there is a collision or false if there is not.
     */
    public boolean checkCollisionBetweenActors(Actor actor1, Actor actor2) {
        Shape tempShape = new Rectangle(actor1.getX(), actor1.getY(), SPRITE_WIDTH,   SPRITE_HEIGHT);
        Shape tempShape2 = new Rectangle(actor2.getX(), actor2.getY(), SPRITE_WIDTH,   SPRITE_HEIGHT);

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

    private boolean handlePlayerActorCollisionWithMapBoundary(int mapX, int mapY) {

        logger.debug("COLLISION : mapXY: " + mapX + "," + mapY
                + " :: collisionMapMinXY: " + collisionMap[mapX][mapY].getMinX() + ", " + collisionMap[mapX][mapY].getMinY()
                + " - collisionMapMaxXY: " + collisionMap[mapX][mapY].getMaxX() + ", " + collisionMap[mapX][mapY].getMaxY());

        TiledMap map = LevelState.getInstance().getCurrentTiledMap();
        int currentTileId = map.getTileId(mapX, mapY, MAP_LAYER);

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

    /**
     * Uses InventoryItemType on TiledMap at specified x,y coordinates.
     * @param itemTypeUsed Type of inventory item to use on map.
     * @param mapX x map coordinates
     * @param mapY y map coordinates
     * @param map TiledMap to use the item on at the coordinates specified.
     * @return Returns true if the item was able to be used and false if it was not.
     */
    private boolean useInventoryItemOnMap(InventoryItemType itemTypeUsed, int mapX, int mapY, TiledMap map) {
        switch (itemTypeUsed) {
            case GRASS_SEED:
                //only use grass seed on grass that needs it.
                if (map.getTileId(mapX, mapY, MAP_LAYER) == TileType.OVER_MOWED_GRASS.getId()
                    || map.getTileId(mapX, mapY, MAP_LAYER) == TileType.DEAD_GRASS.getId()) {
                    map.setTileId(mapX, mapY, 1, TileType.CUT_GRASS.getId());
                    return true;
                }
        }
        logger.info("Cannot use item " + itemTypeUsed.name());
        return false;
    }
}
