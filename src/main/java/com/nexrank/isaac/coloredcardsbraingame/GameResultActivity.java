package com.nexrank.isaac.coloredcardsbraingame;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

    // Shared Preference
    private final String KEY_POINTS_ACCUMULATED = "pointsAccumulated";
    private final String KEY_POINTS_TO_REACH = "pointsToReach";
    private final String KEY_TIME_LEFT = "timeLeft";
    private final String KEY_CORRECT_ANSWER_COUNT = "correctAnswerCount";
    private final String KEY_WRONG_ANSWER_COUNT = "wrongAnswerCount";
    private final String KEY_SKIPPED_ANSWER_COUNT = "skippedAnswerCount";
    private final String KEY_GAME_LEVEL = "gameLevel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);
        // Change the Action Bar's color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));


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
        int gameLevel = gameIntent.getIntExtra(KEY_GAME_LEVEL, 0);
        int nextLevel = gameLevel + 1;
        Bundle userProgressBundle = gameIntent.getBundleExtra("userProgress");
        Bundle gameResultBundle = gameIntent.getBundleExtra("gameResult");

        // Display level
        textView_Header.append(" " + String.valueOf(gameLevel));

        // Display the time left
        String formattedTimeLeft; // How the time is displayed
        int minutes = (int) (userProgressBundle.getLong(KEY_TIME_LEFT) / 1000) / 60;
        int secondsLeft = (int) (userProgressBundle.getLong(KEY_TIME_LEFT) / 1000) % 60; // Convert the time left on the clock in seconds
        if (minutes > 0) {
            formattedTimeLeft = String.format(Locale.getDefault(), " %d with %dmin and %02ds left on the clock.", gameLevel, minutes, secondsLeft);
        } else {
            formattedTimeLeft = String.format(Locale.getDefault(), " %d with %02d seconds left on the clock.", gameLevel, secondsLeft);
        }
        textView_LevelTimeLeft.append(formattedTimeLeft);

        // Display points accumulated
        long pointsAccumulated = userProgressBundle.getLong(KEY_POINTS_ACCUMULATED);
        String pointsAccumulatedMSG = " " + nextLevel + " with " + pointsAccumulated + " points.";
        textView_LevelPoints.append(pointsAccumulatedMSG);

        // Display points to reach
        long pointsToReach = userProgressBundle.getLong(KEY_POINTS_TO_REACH);
        String pointsToReachMSG = " " + nextLevel + ", you needed a minimum of " + pointsToReach + " points.";
        textView_PointsNeeded.append(pointsToReachMSG);

        // Display correct cards
        long correctAnswerCount = gameResultBundle.getLong(KEY_CORRECT_ANSWER_COUNT);
        String correctAnswersMSG = " " + correctAnswerCount + " correct picks.";
        textView_CorrectAnswers.append(correctAnswersMSG);

        // Display wrong cards
        long wrongAnswerCount = gameResultBundle.getLong(KEY_WRONG_ANSWER_COUNT);
        String wrongAnswersMSG = " " + wrongAnswerCount + " wrong picks.";
        if (wrongAnswerCount > 0) {
            textView_WrongAnswers.append(wrongAnswersMSG);
        } else {
            textView_WrongAnswers.setText("You did not get anything wrong. Well done!");
        }

        // Display skipped cards
        long skippedAnswerCount = gameResultBundle.getLong(KEY_SKIPPED_ANSWER_COUNT);
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
