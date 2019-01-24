package com.nexrank.isaac.coloredcardsbraingame;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class FragmentUserProgress extends Fragment {

    private TextView textView_PointsAccumulated;
    private TextView textView_PointsDivider;
    private TextView textView_PointsToReach;
    private TextView textView_TimeLeft;
    private ImageView imageView_time;
    private Button button_BonusTime;

    // Points-related fields
    private long pointsAccumulated; // Level-related points accumulated by the user. Must be reset at each level.
    private long pointsToReach; // Level-related points to collect to reach the game's next level
    private int pointsIncrementor; // The amount of points by which a correct answer will increase the points
    private int pointsDeductor; // The amount of points by which a wrong answer will decrease the points
    private final int INITIAL_POINTS_TO_REACH = 50; // Points to finish Level 1
    private final int INITIAL_POINTS_INCREMENTOR = 20; // Points gained for every correct answer
    private final int INITIAL_POINTS_DEDUCTOR = 25; // Points removed for wrong answers
    private final double INCREMENT_COEFFICIENT = 2; // Points accumulated will be multiplied by that value at each level increase
    private final int HIGH_DEDUCTOR = 250; // The number of points that the player loses for a wrong card.
    private final int SOFT_DEDUCTOR = 5;
    private final int LEVEL_INVINCIBLE = 5;

    // Countdown-related fields
    private CountDownTimer timer;
    private boolean timerRunning;
    private long millisForCurrentLevel;
    private final long INITIAL_TIME = 30000; // Must be 1min
    private final long COUNTDOWN_INTERVAL = 1000;
    private final long BONUS_TIME = 60000;

    private Communicator communicator;

    // Shared Preferences
    private final String KEY_POINTS_ACCUMULATED = "pointsAccumulated";
    private final String KEY_POINTS_TO_REACH = "pointsToReach";
    private final String KEY_TIME_LEFT = "timeLeft";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_progress, container, false);
        textView_PointsAccumulated = view.findViewById(R.id.textView_LevelPoints);
        textView_PointsDivider = view.findViewById(R.id.textView_LevelPointsSlash);
        textView_PointsToReach = view.findViewById(R.id.textView_LevelPointsToReach);
        textView_TimeLeft = view.findViewById(R.id.textView_Timer);
        imageView_time = view.findViewById(R.id.imageView_Time);
        button_BonusTime = view.findViewById(R.id.button_Bonus);

        // Assigning values to the views
        String pointsAccumulatedString = " " + pointsAccumulated;
        textView_PointsAccumulated.setText(pointsAccumulatedString);
        textView_PointsToReach.setText(String.valueOf(pointsToReach));

        // By default, the bonus button should be hidden until the Rewarded Video Ad has finished loading.
        button_BonusTime.setVisibility(View.INVISIBLE);

        // Countdown-related variables and methods
        timerRunning = false;
        initiateUserProgress();
        pointsIncrementor = INITIAL_POINTS_INCREMENTOR;

        if (communicator.getGameLevelNo() == LEVEL_INVINCIBLE) {
            pointsDeductor = HIGH_DEDUCTOR;
        } else {
            pointsDeductor = INITIAL_POINTS_DEDUCTOR;
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // If the game resumes in Level 5 directly, make sure to hide the points to reach, time and bonus button.
        if (communicator.getGameLevelNo() == LEVEL_INVINCIBLE) {
            hideLowLevelItems();
        } else {
            // Add values to the point-related TextViews
            displayPointAccumulated();
            displayPointToReach();

            // NOTE: The timer is not started here because it is handled inside onResume() of MainActivity.
        }

        // Show the Rewarded Video Ad when the bonus button is clicked
        if (button_BonusTime != null) {
            button_BonusTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    communicator.askViewRewardedVideoAd();
                }
            });
        }
    }

    public void initiateUserProgress() {
        if (!communicator.isExistingGame()) {
            // For new games
            millisForCurrentLevel = INITIAL_TIME; // 1min in Level 1
            pointsToReach = INITIAL_POINTS_TO_REACH;
        }
    }

    public int getLevelInvincibleConstant() {
        return LEVEL_INVINCIBLE;
    }

    public void setPointsAccumulated(long pointsAccumulated) {
        this.pointsAccumulated = pointsAccumulated;
    }

    public void setPointsToReach(long pointsToReach) {
        this.pointsToReach = pointsToReach;
    }

    public void setMillisForCurrentLevel(long millisForCurrentLevel) {
        this.millisForCurrentLevel = millisForCurrentLevel;
    }

    public void displayPointAccumulated() {
        String points = " " + pointsAccumulated;
        textView_PointsAccumulated.setText(points);
    }

    public void displayPointToReach() {
        String points = " " + pointsToReach;
        textView_PointsToReach.setText(points);
    }

    public void setCommunicator(Context context) {
        this.communicator = (Communicator) context;
    }

    public void setBonusButtonVisibility(int visibility) {
        if (visibility == 1) {
            button_BonusTime.setVisibility(View.VISIBLE);
        } else if (visibility == 0) {
            button_BonusTime.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(getActivity(), "Error showing the bonus button", Toast.LENGTH_SHORT).show();
        }
    }

    // Methods for the points below
    // pointsAccumulated, pointsToReach, pointsIncrementor, pointsDeductor, totalPoints

    public void increaseUserPoints() {
        pointsAccumulated += pointsIncrementor;
        displayPointAccumulated();
        textView_PointsAccumulated.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
    }

    public void decreaseUserPoints() {
        if (pointsAccumulated - pointsDeductor >= 0) {
            pointsAccumulated -= pointsDeductor;
        } else {
            pointsAccumulated = 0;
        }

        displayPointAccumulated();
        textView_PointsAccumulated.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
    }

    public void softDecreaseUserPoints() {
        if (pointsAccumulated - SOFT_DEDUCTOR >= 0) {
            pointsAccumulated -= SOFT_DEDUCTOR;
        } else {
            pointsAccumulated = 0;
        }

        displayPointAccumulated();
        textView_PointsAccumulated.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
    }

    public void increasePointsToReach() {
        pointsToReach *= INCREMENT_COEFFICIENT;
    }

    public void increasePointsDeductor() {
        pointsDeductor = HIGH_DEDUCTOR;
    }

    public void resetUserPoints() {
        pointsAccumulated = 0;
    }

    public void resetUserPointsTextColor() {
        // This keeps returning exceptions
        try {
            textView_PointsAccumulated.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public boolean isTimerRunning() {
        return timerRunning;
    }

    public void startTimer() {
        timerRunning = true;
        timer = new CountDownTimer(millisForCurrentLevel, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisForCurrentLevel = millisUntilFinished;
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                String timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                textView_TimeLeft.setText(timeLeft);
                if (communicator.getGameLevelNo() < LEVEL_INVINCIBLE) {
                    // In Level 5, no need to check if the user has won as it's an eternal mode.
                    verifyIfUserHasWon();
                }
            }

            @Override
            public void onFinish() {

                communicator.setTimeUp(true);
                selectDialogFragmentToShow();
            }
        }.start();
    }

    public void cancelTimer() {
        if(timer != null) {
            timer.cancel();
        }
        timerRunning = false;
    }

    public void addBonusTime() {
        millisForCurrentLevel += BONUS_TIME;
    }

    // May be used for both restarts and new levels
    public void increaseGameLevelTime(int currentGameLevelNo) {
        millisForCurrentLevel = INITIAL_TIME * currentGameLevelNo;
    }

    public void resetTime() {
        millisForCurrentLevel = INITIAL_TIME;
    }

    private void selectDialogFragmentToShow() {
        if (pointsAccumulated >= pointsToReach) {
            communicator.showGameOverAlert(GameResult.Win, pointsToReach);
        } else {
            communicator.showGameOverAlert(GameResult.Loss, pointsAccumulated);
        }
    }

    private void verifyIfUserHasWon() {
        if (pointsAccumulated >= pointsToReach) {
            timer.cancel();
            communicator.setTimeUp(true);
            communicator.showGameOverAlert(GameResult.Win, pointsToReach);
        }
    }

    public Bundle provideUserProgressData() {
        Bundle progressData = new Bundle();
        progressData.putLong(KEY_POINTS_ACCUMULATED, pointsAccumulated);
        progressData.putLong(KEY_POINTS_TO_REACH, pointsToReach);
        progressData.putLong(KEY_TIME_LEFT, millisForCurrentLevel);
        return progressData;
    }

    public void hideLowLevelItems() {
        textView_PointsDivider.setVisibility(View.GONE);
        textView_PointsToReach.setVisibility(View.GONE);
        textView_TimeLeft.setVisibility(View.GONE);
        imageView_time.setVisibility(View.GONE);
        button_BonusTime.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        cancelTimer();
        super.onStop();
    }
}
