package io.github.shamrice.lawnmower.actors;

import org.newdawn.slick.Image;

public class PlayerActor extends Actor {

    private long score;

    public PlayerActor(Image spriteImage, float x, float y) {
        super(spriteImage, x, y);
        this.score = 0;
    }

    public long getScore() {
        return score;
    }

    public void changeScore(int delta) {
        score += delta;
        if (score < 0)
            score = 0;
    }
}
