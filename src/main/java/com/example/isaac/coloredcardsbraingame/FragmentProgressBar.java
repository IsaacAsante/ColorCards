package com.example.isaac.coloredcardsbraingame;

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

public class FragmentProgressBar extends Fragment {
    private final int MAXIMUM_STATUS = 100;
    private Handler mhandler;
    private int status;
    private int statusUpdateInterval;
    ProgressBar progressBar;

    private Communicator communicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progressbar, container, false);
        mhandler = new Handler();
        progressBar = view.findViewById(R.id.determinateBar_Shuffling);
        status = 100; // Full progress bar
        statusUpdateInterval = 20; // Milliseconds
        return view;
    }

    public void setCommunicator(Context context) {
        this.communicator = (Communicator) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                    --status;
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
