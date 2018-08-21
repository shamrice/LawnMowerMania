package io.github.shamrice.lawnmower;

import io.github.shamrice.lawnmower.core.Engine;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class LawnMowerApplication {

    public static void main(String[] args) {
        try {
            AppGameContainer app = new AppGameContainer(new Engine());
            app.setTargetFrameRate(60);
            app.setDisplayMode(1000, 800, false);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

}
