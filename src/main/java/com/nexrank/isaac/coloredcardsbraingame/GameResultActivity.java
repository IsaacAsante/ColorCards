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
        textView_LevelTimeLeft = findViewById(R.id.textView_LevelTimeLeft);
        textView_LevelPoints = findViewById(R.id.textView_YourLevelPoints);
        textView_PointsNeeded = findViewById(R.id.textView_LevelPoints);
        textView_CorrectAnswers = findViewById(R.id.textView_YourCorrectAnswers);
        textView_WrongAnswers = findViewById(R.id.textView_YourWrongAnswers);
        textView_SkippedAnswers = findViewById(R.id.textView_YourSkippedAnswers);
    }

    private void assignValuesToViews() {
        Intent gameIntent = getIntent();
        Bundle userProgressBundle = gameIntent.getBundleExtra("userProgress");
        Bundle gameResultBundle = gameIntent.getBundleExtra("gameResult");

        // Display the time left
        String formattedTimeLeft; // How the time is displayed
        int minutes = (int) (userProgressBundle.getLong("timeLeft") / 1000) / 60;
        int secondsLeft = (int) (userProgressBundle.getLong("timeLeft") / 1000) % 60; // Convert the time left on the clock in seconds
        if (minutes > 0) {
            formattedTimeLeft = String.format(Locale.getDefault(), "%dmin and %02ds", minutes, secondsLeft);
        } else {
            formattedTimeLeft = String.format(Locale.getDefault(), "%02d seconds", secondsLeft);
        }
        textView_LevelTimeLeft.setText(formattedTimeLeft);

        // Display points accumulated
        int pointsAccumulated = userProgressBundle.getInt("pointsAccumulated");
        textView_LevelPoints.setText(String.valueOf(pointsAccumulated));

        // Display points to reach
        int pointsToReach = userProgressBundle.getInt("pointsToReach");
        textView_PointsNeeded.setText(String.valueOf(pointsToReach));

        // Display correct cards
        int correctAnswerCount = gameResultBundle.getInt("correctAnswerCount");
        textView_CorrectAnswers.setText(String.valueOf(correctAnswerCount));

        // Display wrong cards
        int wrongAnswerCount = gameResultBundle.getInt("wrongAnswerCount");
        textView_WrongAnswers.setText(String.valueOf(wrongAnswerCount));

        // Display skipped cards
        int skippedAnswerCount = gameResultBundle.getInt("skippedAnswerCount");
        textView_SkippedAnswers.setText(String.valueOf(skippedAnswerCount));
    }
}
