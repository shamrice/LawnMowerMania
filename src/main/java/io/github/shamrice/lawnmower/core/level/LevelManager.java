package io.github.shamrice.lawnmower.core.level;

import io.github.shamrice.lawnmower.actors.EnemyActor;
import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.common.Constants;
import io.github.shamrice.lawnmower.common.ScoreTable;
import io.github.shamrice.lawnmower.common.TileType;
import io.github.shamrice.lawnmower.core.collision.CollisionHandler;
import io.github.shamrice.lawnmower.inventory.InventoryItemType;
import io.github.shamrice.lawnmower.state.GameState;
import io.github.shamrice.lawnmower.state.LevelState;
import org.apache.log4j.Logger;
import org.newdawn.slick.tiled.TiledMap;

import java.util.List;

public class LevelManager {

    private final static Logger logger = Logger.getLogger(LevelManager.class);

    /**
     * Uses InventoryItemType on TiledMap at specified x,y coordinates.
     * @param itemTypeUsed Type of inventory item to use on map.
     * @param mouseX x map coordinates
     * @param mouseY y map coordinates*
     * @return Returns true if the item was able to be used and false if it was not.
     */
    public boolean useInventoryItemOnMap(InventoryItemType itemTypeUsed, int mouseX, int mouseY) {

        TiledMap map = LevelState.getInstance().getCurrentTiledMap();
        int mapX = mouseX / Constants.SPRITE_WIDTH;
        int mapY = mouseY / Constants.SPRITE_HEIGHT;

        switch (itemTypeUsed) {
            case GRASS_SEED:
                //only use grass seed on grass that needs it.
                if (map.getTileId(mapX, mapY, Constants.MAP_LAYER) == TileType.OVER_MOWED_GRASS.getId()
                        || map.getTileId(mapX, mapY, Constants.MAP_LAYER) == TileType.DEAD_GRASS.getId()) {

                    map.setTileId(mapX, mapY, Constants.MAP_LAYER, TileType.CUT_GRASS.getId());

                    GameState.getInstance().changeScore(ScoreTable.CUT_GRASS_SCORE);
                    return true;
                }
        }
        logger.info("Cannot use item " + itemTypeUsed.name());
        return false;
    }

    /**
     * Updates the current tile the player is mowing over to the appropriate cut grass level.
     * @param player PlayerActor
     */
    public void updateMowedTile(PlayerActor player) {

        TiledMap map = LevelState.getInstance().getCurrentTiledMap();
        int scoreDelta = 0;
        int previousMapX = player.getPreviousMapX();
        int previousMapY = player.getPreviousMapY();
        int currentTileId = map.getTileId(player.getMapX(), player.getMapY(), Constants.MAP_LAYER);

        //make sure player is walking over a new tile and not the one they are already on.
        if (previousMapX != player.getMapX() || previousMapY != player.getMapY()) {

            logger.debug("PreviousMapXY: " + previousMapX + ", " + previousMapY +
                    " :: playerMapXY: " + player.getMapX() + ", " + player.getMapY() +
                    " :: currentTileId: " + currentTileId);


            if (currentTileId < TileType.DEAD_GRASS.getId()) {
                currentTileId++;
            }

            // set new tile id and set score delta.
            if (currentTileId == TileType.FLOWERS.getId()) {
                currentTileId = TileType.OVER_MOWED_GRASS.getId();
                scoreDelta = ScoreTable.FLOWER_PENALTY;
            } else if (currentTileId == TileType.UNCUT_GRASS.getId()) {
                currentTileId = TileType.CUT_GRASS.getId();
                LevelState.getInstance().decreaseMowTilesRemaining();
                scoreDelta = ScoreTable.CUT_GRASS_SCORE;
            } else if (currentTileId == TileType.DEAD_GRASS.getId()) {
                scoreDelta = ScoreTable.DEAD_GRASS_PENALTY;
            } else if (currentTileId == TileType.OVER_MOWED_GRASS.getId()) {
                scoreDelta = ScoreTable.OVER_MOWED_GRASS_PENALTY;
            }

            map.setTileId(
                    player.getMapX(),
                    player.getMapY(),
                    Constants.MAP_LAYER,
                    currentTileId
            );

            GameState.getInstance().changeScore(scoreDelta);
        }
    }


    //TODO : will be possibly moved into it's own enemy manager where each enemy can be handled based on type.
    public void moveEnemies(CollisionHandler collisionHandler, PlayerActor player, List<EnemyActor> enemiesToMove) {

        for (EnemyActor enemyActor : enemiesToMove) {

            //calculate the distance between the enemy and the player
            //formula: squareRoot((x2 - x1)^2 + (y2 - y1)^2)
            double xDiff = player.getX() - enemyActor.getX();
            double yDiff = player.getY() - enemyActor.getY();
            double distance = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));

            //TODO : configurable "enemy close" speed increase instead of hardcoded.
            float speedMultiplier = 1f;
            if (distance < 200) {
                speedMultiplier = 1.75f;
                enemyActor.setSpriteAnimationFrameDuration(80); //TODO : not sure how i feel about this getting called so much...
            } else {
                enemyActor.setSpriteAnimationFrameDuration(enemyActor.getPreviousSpriteAnimationDuration());
            }

            //default setting delta as if player values are greater.
            float deltaX = enemyActor.getMovementSpeed() * speedMultiplier;
            float deltaY = enemyActor.getMovementSpeed() * speedMultiplier;

            //if either is not true, inverse delta.
            if (player.getX() < enemyActor.getX()) {
                deltaX = -deltaX;
            }

            if (player.getY() < enemyActor.getY()) {
                deltaY = -deltaY;
            }

            //don't move enemy if they attempt to walk into a wall and aren't able to fly over.
            if (enemyActor.isBlockedByLevelBoundaries()) {
                if (!collisionHandler.checkCollision(enemyActor, deltaX, deltaY)) {
                    enemyActor.updateXY(deltaX, deltaY);
                }
            } else {
                enemyActor.updateXY(deltaX, deltaY);
            }

        }
    }

}
