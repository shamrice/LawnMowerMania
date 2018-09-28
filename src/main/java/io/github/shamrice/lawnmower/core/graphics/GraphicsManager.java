package io.github.shamrice.lawnmower.core.graphics;

import io.github.shamrice.lawnmower.actors.Actor;
import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.core.graphics.displays.InformationDisplay;
import io.github.shamrice.lawnmower.core.graphics.displays.PlayAreaDisplay;
import io.github.shamrice.lawnmower.state.GameState;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Shape;

import java.util.ArrayList;
import java.util.List;


public class GraphicsManager {

    private InformationDisplay informationDisplay;
    private PlayAreaDisplay playAreaDisplay;
    private Graphics graphics;
    private PlayerActor playerActor;

    private List<Shape> shapesToDraw = new ArrayList<>();

    public GraphicsManager(TrueTypeFont font, PlayerActor playerActor) {
        this.informationDisplay = new InformationDisplay(font);
        this.playAreaDisplay = new PlayAreaDisplay();
        this.playerActor = playerActor;
    }

    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }

    public void displayPanel(Panel panel, List<Actor> actors) {

        switch(panel) {
            case LEVEL:
                displayPlayAreaPanel(actors);
                displayInformationPanel();
                break;
        }

        // TODO : debug call. remove when no longer needed.
        shapesToDraw.forEach(shape -> graphics.draw(shape));
        shapesToDraw.clear();

    }

    private void displayInformationPanel() {
        //informationDisplay.displayDebug(graphics, delta, playerActor);
        informationDisplay.displayStats(GameState.getInstance().getScore());
        //TODO : come up with a better way than to have to own a copy of the whole player object just for stamina display
        informationDisplay.displayStamina(playerActor.getStamina());
        informationDisplay.displayInventory();
    }

    private void displayPlayAreaPanel(List<Actor> actors) {
        playAreaDisplay.displayLevelMap();
        playAreaDisplay.displayActors(graphics, actors);
    }

    // TODO : debug method. remove when no longer needed.
    public void debugAddShapeToDraw(Shape shape) {
        shapesToDraw.add(shape);
    }

}
