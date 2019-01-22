package com.nexrank.isaac.coloredcardsbraingame;

public class GameLevel {

    private String levelName;
    private String levelNickname;
    private int levelNo;
    private long levelPoints;

    public GameLevel(String levelName, String levelNickname, int levelNo) {
        this.levelName = levelName;
        this.levelNickname = levelNickname;
        this.levelNo = levelNo;
    }

    public static GameLevel newInstance(int levelNumber) {
        switch (levelNumber) {
            case 2:
                return new GameLevel("Level 2", "Amateur", 2);
            case 3:
                return new GameLevel("Level 3", "Pro", 3);
            case 4:
                return new GameLevel("Level 4", "Master", 4);
            case 5:
                return new GameLevel("Level 5", "Invincible", 5);
            default:
                return new GameLevel("Level 1", "Beginner", 1);
        }
    }

    public String getLevelName() {
        return levelName;
    }

    public String getLevelNickname() {
        return levelNickname;
    }

    public int getLevelNo() {
        return levelNo;
    }

    public String printLevel() {
        return levelName + " (" + levelNickname + ")";
    }

}
