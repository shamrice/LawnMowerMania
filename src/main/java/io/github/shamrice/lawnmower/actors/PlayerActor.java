package io.github.shamrice.lawnmower.actors;

import org.newdawn.slick.Image;

public class PlayerActor extends Actor {

    private long score;

    public PlayerActor(Image spriteImage, float x, float y) {
        super(spriteImage, x, y);
        this.score = 0;
    }

    /**
     *
     * @return Current score value.
     */
    public long getScore() {
        return score;
    }

    /**
     * Update score by the amount specified.
     * @param delta Amount to update score by.
     */
    public void changeScore(int delta) {
        score += delta;
        if (score < 0)
            score = 0;
    }
}
