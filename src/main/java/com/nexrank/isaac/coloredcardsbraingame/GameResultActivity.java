package com.nexrank.isaac.coloredcardsbraingame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private Button button_NextLevel;
    private Button button_ReturnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);
        setTitle("Game Results");

        initiateViews();
        assignValuesToViews();
        setButtonClickListeners();
    }

    private void initiateViews() {
        textView_Header = findViewById(R.id.textView_ResultsHeader);
        textView_LevelTimeLeft = findViewById(R.id.textView_LevelTimeLeftLabel);
        textView_LevelPoints = findViewById(R.id.textView_YourLevelPoints_Label);
        textView_PointsNeeded = findViewById(R.id.textView_LevelPointsLabel);
        textView_CorrectAnswers = findViewById(R.id.textView_YourCorrectAnswers_Label);
        textView_WrongAnswers = findViewById(R.id.textView_YourWrongAnswers_Label);
        textView_SkippedAnswers = findViewById(R.id.textView_YourSkippedAnswers_Label);
        button_NextLevel = findViewById(R.id.button_ProceedToNextLevel);
        button_ReturnHome = findViewById(R.id.button_ReturnHome);
    }

    private void assignValuesToViews() {
        Intent gameIntent = getIntent();
        int gameLevel = gameIntent.getIntExtra("gameLevel", 0);
        int nextLevel = gameLevel + 1;
        Bundle userProgressBundle = gameIntent.getBundleExtra("userProgress");
        Bundle gameResultBundle = gameIntent.getBundleExtra("gameResult");

        // Display level
        textView_Header.append(String.valueOf(gameLevel));

        // Display the time left
        String formattedTimeLeft; // How the time is displayed
        int minutes = (int) (userProgressBundle.getLong("timeLeft") / 1000) / 60;
        int secondsLeft = (int) (userProgressBundle.getLong("timeLeft") / 1000) % 60; // Convert the time left on the clock in seconds
        if (minutes > 0) {
            formattedTimeLeft = String.format(Locale.getDefault(), "%d with %dmin and %02ds left on the clock.", gameLevel, minutes, secondsLeft);
        } else {
            formattedTimeLeft = String.format(Locale.getDefault(), "%d with %02d seconds left on the clock.", gameLevel, secondsLeft);
        }
        textView_LevelTimeLeft.append(formattedTimeLeft);

        // Display points accumulated
        int pointsAccumulated = userProgressBundle.getInt("pointsAccumulated");
        String pointsAccumulatedMSG = nextLevel + " with " + pointsAccumulated + " points.";
        textView_LevelPoints.append(pointsAccumulatedMSG);

        // Display points to reach
        int pointsToReach = userProgressBundle.getInt("pointsToReach");
        String pointsToReachMSG = nextLevel + ", you needed a minimum of " + pointsToReach + " points.";
        textView_PointsNeeded.append(pointsToReachMSG);

        // Display correct cards
        int correctAnswerCount = gameResultBundle.getInt("correctAnswerCount");
        String correctAnswersMSG = correctAnswerCount + " correct picks.";
        textView_CorrectAnswers.append(correctAnswersMSG);

        // Display wrong cards
        int wrongAnswerCount = gameResultBundle.getInt("wrongAnswerCount");
        String wrongAnswersMSG = wrongAnswerCount + " wrong picks.";
        if (wrongAnswerCount > 0) {
            textView_WrongAnswers.append(wrongAnswersMSG);
        } else {
            textView_WrongAnswers.setText("You did not get anything wrong. Well done!");
        }

        // Display skipped cards
        int skippedAnswerCount = gameResultBundle.getInt("skippedAnswerCount");
        String skippedAnswersMSG = skippedAnswerCount + " times.";
        if (skippedAnswerCount > 0) {
            textView_SkippedAnswers.append(skippedAnswersMSG);
        } else {
            textView_SkippedAnswers.setText("You did not skip any card. This means you think and react very fast!");
        }

        String button1Msg = "Start Level " + nextLevel;
        button_NextLevel.setText(button1Msg);
    }

    private void setButtonClickListeners() {
        button_NextLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        button_ReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
