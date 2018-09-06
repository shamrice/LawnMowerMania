package io.github.shamrice.lawnmower;

import io.github.shamrice.lawnmower.core.Engine;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class LawnMowerApplication {

    private static final int TARGET_FPS = 60;
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 800;
    private static final boolean IS_FULLSCREEN = false;

    public static void main(String[] args) {
        try {
            AppGameContainer app = new AppGameContainer(new Engine());
            app.setTargetFrameRate(TARGET_FPS);
            app.setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, IS_FULLSCREEN);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
