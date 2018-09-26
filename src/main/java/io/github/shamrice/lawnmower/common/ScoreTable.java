package io.github.shamrice.lawnmower.common;

/**
 * Score table holds the score points and score penalties for what the player may mow over.
 * TODO : Not sure how I feel about this class yet...
 */
public class ScoreTable {

    public static final int FLOWER_PENALTY = -500;
    public static final int OVER_MOWED_GRASS_PENALTY = -25;
    public static final int DEAD_GRASS_PENALTY = -50;

    public static final int CUT_GRASS_SCORE = 50;
}
