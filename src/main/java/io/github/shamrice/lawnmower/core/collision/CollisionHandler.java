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

import java.util.ArrayList;
import java.util.List;

public class CollisionHandler {

    private final static Logger logger = Logger.getLogger(CollisionHandler.class);
    private List<Rectangle> collisionTileList = null;

    public CollisionHandler() {}

    /**
     * Sets up collision map used in the check collision method.
     * @param map TiledMap to use to base collision map off of.
     * @return Returns number of tiles that should not be mowed on the map
     */
    public int setUpCollisionMap(TiledMap map) {

        int tilesNotToMow = 0;
        collisionTileList = new ArrayList<>();

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int tileId = map.getTileId(x, y, Constants.MAP_LAYER);

                if (tileId > 0) {
                    tilesNotToMow++;

                    if (tileId != TileType.FLOWERS.getId()) {
                        collisionTileList.add(
                                new Rectangle(
                                        x * map.getTileWidth(),
                                        y * map.getTileWidth(),
                                        Constants.SPRITE_WIDTH,
                                        Constants.SPRITE_HEIGHT
                                )
                        );
                    }
                }
            }
        }

        return tilesNotToMow;

    }

    /**
     * Checks to see if player has collided with a boundary tile and handle it.
     * @param actor Actor to test collisionw with boundary for
     * @param deltaX change in player x position
     * @param deltaY change in player y position
     * @return Returns true on collision or false if no collision is happening.
     * @throws IllegalStateException Exception is thrown if there is no collision map set up for the collision handler.
     */
    public boolean checkCollision(Actor actor, float deltaX, float deltaY) throws IllegalStateException {

        if (this.collisionTileList == null)
            throw new IllegalStateException("No collision map set up to check collision against.");

        float attemptedX = actor.getX() + deltaX;
        float attemptedY = actor.getY() + deltaY;

        Shape tempPlayerShape = new Rectangle(
                attemptedX,
                attemptedY,
                Constants.SPRITE_WIDTH - 2,    // -2 so collision box of player is smaller than tile width
                Constants.SPRITE_HEIGHT - 2);

        for (Shape collisionShape : collisionTileList) {
            if (collisionShape.intersects(tempPlayerShape)) {
                return true;
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

        Shape tempClickShape = new Rectangle(mouseX, mouseY, 1,   1);
        TiledMap map = LevelState.getInstance().getCurrentTiledMap();

        //check if click was outside of map
        if (attemptedMapX > map.getWidth() || attemptedMapY > map.getHeight()) {
            return false;
        }

        for (Shape collisionShape : collisionTileList) {
            if (collisionShape.intersects(tempClickShape)) {
                logger.debug("MOUSE COLLISION WITH BORDER: " + attemptedMapX + "," + attemptedMapY);
                return false;
            }
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
