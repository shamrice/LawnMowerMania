package io.github.shamrice.lawnmower.core.graphics.displays;

import io.github.shamrice.lawnmower.actors.Actor;
import io.github.shamrice.lawnmower.state.LevelState;
import org.newdawn.slick.Graphics;

import java.util.List;

public class PlayAreaDisplay {

    public void displayLevelMap() {
        LevelState.getInstance().getCurrentTiledMap().render(0,0);
    }

    public void displayActors(Graphics g, List<Actor> actors) {
        actors.forEach(actor -> g.drawImage(actor.getSpriteImage(), actor.getX(), actor.getY()));
    }
}
