package com.example.isaac.coloredcardsbraingame;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

public class FragmentUserProgress extends Fragment {

    private boolean timeUp;
    private TextView textView_PointsAccumulated;
    private TextView textView_PointsToReach;
    private TextView textView_TimeLeft;

    // Points-related fields
    private int pointsAccumulated; // Level-related points accumulated by the user. Must be reset at each level.
    private int pointsToReach; // Level-related points to collect to reach the game's next level
    private int pointsIncrementor; // The amount of points by which a correct answer will increase the points
    private int pointsDeductor; // The amount of points by which a wrong answer will decrease the points
    private int totalPoints; // Sum of all level-related points collected. Must be increased throughout the game
    private final int INITIAL_POINTS_TO_REACH = 1000;
    private final int INITIAL_POINTS_INCREMENTOR = 20;
    private final int INITIAL_POINTS_DEDUCTOR = 25;
    private final double INCREMENT_COEFFICIENT = 1.5; // Points accumulated will be multiplied by that value at each level increase
    private final double DECREMENT_COEFFICIENT = 2; // Points lost will be multiplied by that value at each level increase

    // Countdown-related fields
    private long millisForCurrentLevel;
    private final long COUNTDOWN_INTERVAL = 1000;

    private Communicator communicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_progress, container, false);
        textView_PointsAccumulated = view.findViewById(R.id.textView_LevelPoints);
        textView_PointsToReach = view.findViewById(R.id.textView_LevelPointsToReach);
        textView_TimeLeft = view.findViewById(R.id.textView_Timer);
        millisForCurrentLevel = 60000; // Level 1
        timeUp = false;
        pointsToReach = INITIAL_POINTS_TO_REACH;
        pointsIncrementor = INITIAL_POINTS_INCREMENTOR;
        pointsDeductor = INITIAL_POINTS_DEDUCTOR;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startTimer(); // Start the timer
    }

    public void setCommunicator(Context context) {
        this.communicator = (Communicator) context;
    }

    // Methods for the points below
    // pointsAccumulated, pointsToReach, pointsIncrementor, pointsDeductor, totalPoints

    // Getters
    public int getPointsAccumulated() {
        return pointsAccumulated;
    }

    public int getPointsToReach() {
        return pointsToReach;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void increaseUserPoints() {
        pointsAccumulated += pointsIncrementor;
        totalPoints += pointsIncrementor;
        textView_PointsAccumulated.setText(String.valueOf(pointsAccumulated));
        textView_PointsAccumulated.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
    }

    public void decreaseUserPoints() {
        if (pointsAccumulated - pointsDeductor >= 0) {
            pointsAccumulated -= pointsDeductor;
        } else {
            pointsAccumulated = 0;
        }

        if (totalPoints - pointsDeductor > 0) {
            totalPoints -= pointsDeductor;
        } else {
            totalPoints = 0;
        }
        textView_PointsAccumulated.setText(String.valueOf(pointsAccumulated));
        textView_PointsAccumulated.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
    }

    public void increasePointsToReach() {
        pointsToReach *= INCREMENT_COEFFICIENT;
        textView_PointsToReach.setText(String.valueOf(pointsToReach));
    }

    public void increaseUserPointsAccumulation() {
        pointsIncrementor *= INCREMENT_COEFFICIENT;
    }

    public void increaseUserPointsDeduction() {
        pointsDeductor *= DECREMENT_COEFFICIENT;
    }

    public void resetUserPoints() {
        pointsAccumulated = 0;
    }

    public void resetUserPointsTextColor() {
        textView_PointsAccumulated.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey));
    }

    public void startTimer() {
        CountDownTimer timer = new CountDownTimer(millisForCurrentLevel, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisForCurrentLevel = millisUntilFinished;
                int minutes = (int) (millisUntilFinished / 60000);
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                String timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                textView_TimeLeft.setText(timeLeft);
            }

            @Override
            public void onFinish() {
                communicator.setTimeUp(false);
            }
        }.start();
    }


    public static class GameLevel {
        private int levelNo;
        private Difficulty levelDifficulty;

        enum Difficulty {
            EASY, MEDIUM, HARD
        }

        public GameLevel() {
            levelNo = 1;
            levelDifficulty = Difficulty.EASY;
        }

        public int getLevelNo() {
            return levelNo;
        }

        public void setLevelNo(int levelNo) {
            this.levelNo = levelNo;
        }

        public Difficulty getLevelDifficulty() {
            return levelDifficulty;
        }

        public void setLevelDifficulty(Difficulty levelDifficulty) {
            this.levelDifficulty = levelDifficulty;
        }

        public void increaseLevel() {
            levelNo++;
        }
    }

}
