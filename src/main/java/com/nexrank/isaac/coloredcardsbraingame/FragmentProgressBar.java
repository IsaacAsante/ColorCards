package com.nexrank.isaac.coloredcardsbraingame;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentProgressBar extends Fragment {
    private final int MAXIMUM_STATUS = 100;
    private Handler mhandler;
    private int status;
    private int statusUpdateInterval;

    // Views
    TextView textView_levelNo;
    ProgressBar progressBar;

    private Communicator communicator;

    private GameLevel currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progressbar, container, false);
        mhandler = new Handler();
        textView_levelNo = view.findViewById(R.id.textView_GameLevel);
        progressBar = view.findViewById(R.id.determinateBar_Shuffling);
        status = 100; // Full progress bar
        statusUpdateInterval = 20; // Milliseconds
        return view;
    }

    public void setCommunicator(Context context) {
        this.communicator = (Communicator) context;
    }

    public void displayLevelDetails() {
        currentLevel = communicator.getLevelDetails();
        textView_levelNo.setText(currentLevel.printLevel());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        displayLevelDetails(); // Display Level 1
        mhandler.postDelayed(runnable, statusUpdateInterval);
    }

    public void refillProgress() {
        status = MAXIMUM_STATUS;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (status > 0) {
                if (communicator.gameIsRunning()) {
                    // Increase the speed of the game's progress bar
                    if (communicator.getLevelDetails().getLevelNo() == 4 || communicator.getLevelDetails().getLevelNo() == 5) {
                        status -= 2;
                    } else {
                        status -= 1;
                    }
                    progressBar.setProgress(status);
                }
            } else {
                // Tell the Activity to show new cards then reanimate the progress bar after the status is 0.
                communicator.updateMissedCount(); // Register a miss
                communicator.resetPointsTextColor();
                communicator.presentNewChallenge();
                refillProgress();

            }
            mhandler.postDelayed(this, statusUpdateInterval);
        }
    };


}
