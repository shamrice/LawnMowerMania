package io.github.shamrice.lawnmower.core.graphics;

import io.github.shamrice.lawnmower.actors.Actor;
import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.core.graphics.displays.InformationDisplay;
import io.github.shamrice.lawnmower.core.graphics.displays.PlayAreaDisplay;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

import java.util.List;


public class GraphicsManager {

    private InformationDisplay informationDisplay;
    private PlayAreaDisplay playAreaDisplay;
    private Graphics graphics;
    private PlayerActor playerActor;

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
    }

    private void displayInformationPanel() {
        //informationDisplay.displayDebug(graphics, delta, playerActor);
        informationDisplay.displayStats(playerActor.getScore());
        informationDisplay.displayStamina(playerActor.getStamina());
        informationDisplay.displayInventory();
    }

    private void displayPlayAreaPanel(List<Actor> actors) {
        playAreaDisplay.displayLevelMap();
        playAreaDisplay.displayActors(graphics, actors);
    }

}
