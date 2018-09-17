package io.github.shamrice.lawnmower.core.graphics.displays;

import io.github.shamrice.lawnmower.actors.EnemyActor;
import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.state.GameState;
import org.newdawn.slick.Graphics;

public class PlayAreaDisplay {

    public void displayLevelMap() {
        GameState.getInstance().getCurrentTiledMap().render(0,0);
    }

    public void displayActors(Graphics g, PlayerActor player, EnemyActor enemyActor) {
        g.drawImage(player.getSpriteImage(), player.getX(), player.getY());
        g.drawImage(enemyActor.getSpriteImage(), enemyActor.getX(), enemyActor.getY());
    }
}
