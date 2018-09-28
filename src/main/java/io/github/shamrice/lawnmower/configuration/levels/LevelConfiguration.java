package io.github.shamrice.lawnmower.configuration.levels;

import java.util.List;

public class LevelConfiguration {

    private String filename;
    private List<LevelEnemyConfiguration> levelEnemyConfigurations;

    public LevelConfiguration(String filename, List<LevelEnemyConfiguration> levelEnemyConfigurations) {
        this.filename = filename;
        this.levelEnemyConfigurations = levelEnemyConfigurations;
    }

    public String getFilename() {
        return filename;
    }

    public List<LevelEnemyConfiguration> getLevelEnemyConfigurations() {
        return levelEnemyConfigurations;
    }
}
