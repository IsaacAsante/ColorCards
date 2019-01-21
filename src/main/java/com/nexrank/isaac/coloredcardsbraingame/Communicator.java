package com.nexrank.isaac.coloredcardsbraingame;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public interface Communicator {
    // Gameplay
    public void increaseQuestionID();
    public void changeTextColor(int color);
    public void changeTextInstruction(String colorName);
    public void verifyAnswer(String answer);
    public void resetProgressBar();
    public boolean gameIsRunning(); // Let fragments know when the game has stopped
    public void setTimeUp(boolean status); // Let the timer determine when the game should stop
    public void updateUserPoints(AnswerResult answer);
    public void resetPointsTextColor(); // Set the text color for points to grey
    public GameLevel getLevelDetails();
    public boolean isExistingGame();

    // Important method (multiple methods called at once)
    public void restartGameLevel();
    public void increaseGameLevel();

    // Other fragments may use the following method.
    public void presentNewChallenge();

    public void updateMissedCount();

    // AdMob
    public void showRewardedVideoAd();

    // Alerts
    public void showGameOverAlert(GameResult result, long points);

    // Intent
    public void handleResults();
    public void goBackSaveState();
    public void quitGame();

    // Menu
    public Menu getMenu();
    public MenuItem findMenuItemByID(int id);
    public void resumeGame(MenuItem item);

}