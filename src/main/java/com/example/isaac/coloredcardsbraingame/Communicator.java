package com.example.isaac.coloredcardsbraingame;

public interface Communicator {
    // The methods below are mainly called individually by FragmentCards
    public void increaseQuestionID();
    public void changeTextColor(int color);
    public void changeTextInstruction(String colorName);
    public void verifyAnswer(String answer);
    public void resetProgressBar();
    public boolean gameIsRunning(); // Let fragments know when the game has stopped
    public void setTimeUp(boolean status); // Let the timer determine when the game should stop
    public void updateUserPoints(AnswerResult answer);
    public void resetPointsTextColor(); // Set the text color for points to grey


    // Other fragments may use the following method.
    public void presentNewChallenge();

    public void updateMissedCount();

    // AdMob
    public void showRewardedVideoAd();

}