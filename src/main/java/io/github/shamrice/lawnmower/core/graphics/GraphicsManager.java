package io.github.shamrice.lawnmower.core.graphics;

import io.github.shamrice.lawnmower.actors.EnemyActor;
import io.github.shamrice.lawnmower.actors.PlayerActor;
import io.github.shamrice.lawnmower.core.graphics.displays.InformationDisplay;
import io.github.shamrice.lawnmower.core.graphics.displays.PlayAreaDisplay;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;


public class GraphicsManager {

    private InformationDisplay informationDisplay;
    private PlayAreaDisplay playAreaDisplay;

    public GraphicsManager(TrueTypeFont font) {
        this.informationDisplay = new InformationDisplay(font);
        this.playAreaDisplay = new PlayAreaDisplay();
    }

    public void displayInformationPanel(Graphics graphics, PlayerActor playerActor, float delta) {
        informationDisplay.displayDebug(graphics, delta, playerActor);
        informationDisplay.displayStamina(playerActor);
        informationDisplay.displayInventory(graphics);
    }

    public void displayPlayAreaPanel(Graphics graphics, PlayerActor playerActor, EnemyActor enemyActor) {
        playAreaDisplay.displayLevelMap();
        playAreaDisplay.displayActors(graphics, playerActor, enemyActor);
    }

}
