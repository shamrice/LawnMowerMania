package io.github.shamrice.lawnmower.core.collision;

import io.github.shamrice.lawnmower.actors.Actor;
import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.common.Constants;
import io.github.shamrice.lawnmower.common.TileType;
import io.github.shamrice.lawnmower.state.LevelState;
import org.apache.log4j.Logger;
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
                if (tileId > 0 && tileId != TileType.FLOWERS.getId()) {
                    collisionMap[x][y] = new Rectangle(
                            x * map.getTileWidth(),
                            y * map.getTileWidth(),
                            Constants.SPRITE_WIDTH,
                            Constants.SPRITE_HEIGHT);

                    collisionEntries++;
                }
            }
        }

        return collisionEntries;
    }

    /**
     * Checks to see if player has collided with a boundary tile and handle it.
     * @param player player
     * @param deltaX change in player x position
     * @param deltaY change in player y position
     * @return Returns true on collision or false if no collision is happening.
     * @throws IllegalStateException Exception is thrown if there is no collision map set up for the collision handler.
     */
    public boolean checkCollision(PlayerActor player, float deltaX, float deltaY) throws IllegalStateException {

        if (this.collisionMap == null)
            throw new IllegalStateException("No collision map set up to check collision against.");

        float attemptedX = player.getX() + deltaX;
        float attemptedY = player.getY() + deltaY;

        Shape tempPlayerShape = new Rectangle(
                attemptedX,
                attemptedY,
                Constants.SPRITE_WIDTH - 2,    // -2 so collision box of player is smaller than tile width
                Constants.SPRITE_HEIGHT - 2);

        for (int x = 0; x < collisionMap.length; x++) {
            for (int y = 0; y < collisionMap[x].length; y++) {
                if (collisionMap[x][y] != null && collisionMap[x][y].intersects(tempPlayerShape)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check collision between mouse x,y and map x,y and uses item.
     * @param mouseX Mouse X screen location
     * @param mouseY Mouse Y screen location
     * @return returns true if item was able to be used or false if it was not.
     */
    public boolean checkMouseCollision(int mouseX, int mouseY) {

        int attemptedMapX = mouseX / Constants.SPRITE_WIDTH;
        int attemptedMapY = mouseY / Constants.SPRITE_HEIGHT;

        Shape tempClickShape = new Rectangle(mouseX, mouseY, Constants.SPRITE_WIDTH,   Constants.SPRITE_HEIGHT);
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

        return true;
    }

    /**
     * Checks collision between two actor objects. Returns true if they are currently colliding.
     * @param actor1 Actor to test collision with
     * @param actor2 Second actor to test collision with first actor.
     * @return Will return true if there is a collision or false if there is not.
     */
    public boolean checkCollisionBetweenActors(Actor actor1, Actor actor2) {
        Shape tempShape = new Rectangle(actor1.getX(), actor1.getY(), Constants.SPRITE_WIDTH,   Constants.SPRITE_HEIGHT);
        Shape tempShape2 = new Rectangle(actor2.getX(), actor2.getY(), Constants.SPRITE_WIDTH,   Constants.SPRITE_HEIGHT);

        return tempShape.intersects(tempShape2);
    }

}
