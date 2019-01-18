package com.nexrank.isaac.coloredcardsbraingame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class GameResultActivity extends AppCompatActivity {

    private TextView textView_Header;
    private TextView textView_LevelTimeLeft;
    private TextView textView_LevelPoints;
    private TextView textView_PointsNeeded;
    private TextView textView_CorrectAnswers;
    private TextView textView_WrongAnswers;
    private TextView textView_SkippedAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);
        setTitle("Game Results");

        initiateViews();
        assignValuesToViews();
    }

    private void initiateViews() {
        textView_Header = findViewById(R.id.textView_ResultsHeader);
        textView_LevelTimeLeft = findViewById(R.id.textView_LevelTimeLeftLabel);
        textView_LevelPoints = findViewById(R.id.textView_YourLevelPoints_Label);
        textView_PointsNeeded = findViewById(R.id.textView_LevelPointsLabel);
        textView_CorrectAnswers = findViewById(R.id.textView_YourCorrectAnswers_Label);
        textView_WrongAnswers = findViewById(R.id.textView_YourWrongAnswers_Label);
        textView_SkippedAnswers = findViewById(R.id.textView_YourSkippedAnswers_Label);
    }

    private void assignValuesToViews() {
        Intent gameIntent = getIntent();
        int gameLevel = gameIntent.getIntExtra("gameLevel", 0);
        Bundle userProgressBundle = gameIntent.getBundleExtra("userProgress");
        Bundle gameResultBundle = gameIntent.getBundleExtra("gameResult");

        // Display level
        textView_Header.append(String.valueOf(gameLevel));

        // Display the time left
        String formattedTimeLeft; // How the time is displayed
        int minutes = (int) (userProgressBundle.getLong("timeLeft") / 1000) / 60;
        int secondsLeft = (int) (userProgressBundle.getLong("timeLeft") / 1000) % 60; // Convert the time left on the clock in seconds
        if (minutes > 0) {
            formattedTimeLeft = String.format(Locale.getDefault(), "%dmin and %02ds left on the clock.", minutes, secondsLeft);
        } else {
            formattedTimeLeft = String.format(Locale.getDefault(), "%02d seconds left on the clock.", secondsLeft);
        }
        textView_LevelTimeLeft.append(formattedTimeLeft);

        // Display points accumulated
        int pointsAccumulated = userProgressBundle.getInt("pointsAccumulated");
        String pointsAccumulatedMSG = pointsAccumulated + " points.";
        textView_LevelPoints.append(pointsAccumulatedMSG);

        // Display points to reach
        int pointsToReach = userProgressBundle.getInt("pointsToReach");
        String pointsToReachMSG = pointsToReach + " points.";
        textView_PointsNeeded.append(pointsToReachMSG);

        // Display correct cards
        int correctAnswerCount = gameResultBundle.getInt("correctAnswerCount");
        String correctAnswersMSG = correctAnswerCount + " correct picks.";
        textView_CorrectAnswers.append(correctAnswersMSG);

        // Display wrong cards
        int wrongAnswerCount = gameResultBundle.getInt("wrongAnswerCount");
        String wrongAnswersMSG = wrongAnswerCount + " wrong picks.";
        textView_WrongAnswers.append(wrongAnswersMSG);

        // Display skipped cards
        int skippedAnswerCount = gameResultBundle.getInt("skippedAnswerCount");
        String skippedAnswersMSG = skippedAnswerCount + " times.";
        textView_SkippedAnswers.append(skippedAnswersMSG);
    }
}
