package com.nexrank.isaac.coloredcardsbraingame;

import android.view.Menu;
import android.view.MenuItem;

public interface Communicator {
    // Gameplay
    void changeTextColor(int color);
    void changeTextInstruction(String colorName);
    void verifyAnswer(String answer);
    void resetProgressBar();
    boolean gameIsRunning(); // Let fragments know when the game has stopped
    void setTimeUp(boolean status); // Let the timer determine when the game should stop
    void updateUserPoints(AnswerResult answer);
    void resetPointsTextColor(); // Set the text color for points to grey
    GameLevel getLevelDetails();
    int getGameLevelNo();
    boolean isExistingGame();

    // Important method (multiple methods called at once)
    void restartGameLevel();
    void increaseGameLevel();

    // Other fragments may use the following method.
    void presentNewChallenge();

    void updateMissedCount();

    // AdMob
    void askViewRewardedVideoAd();
    void showRewardedVideoAd();

    // Alerts
    void showGameOverAlert(GameResult result, long points);

    // Intent
    void handleResults();
    void goBackSaveState();
    void quitGame();

    // Menu
    Menu getMenu();
    MenuItem findMenuItemByID(int id);
    void resumeGame(MenuItem item);

}