package com.nexrank.isaac.coloredcardsbraingame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class CustomDialogFragment extends DialogFragment {

    private static final String ARGS_VICTORY = "argsVictory";
    private static final String ARGS_CURRENT_LEVEL = "argsCurrentLevel";

    private boolean gameResultVictory; // True for Win / False for Loss
    private int gameCurrentLevelNo; // Just-ended level

    private Communicator communicator;

    public static CustomDialogFragment newInstance(Boolean victory, int levelNo) {
        CustomDialogFragment dialogFragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGS_VICTORY, victory);
        args.putInt(ARGS_CURRENT_LEVEL, levelNo);
        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false); // Prevent the fragment from closing when the user taps outside
        return dialogFragment;
    }

    public void setCommunicator(Context context) {
        communicator = (Communicator) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Receive result from the activity if arguments are set
        if (getArguments() != null) {
            gameResultVictory = getArguments().getBoolean(ARGS_VICTORY);
            gameCurrentLevelNo = getArguments().getInt(ARGS_CURRENT_LEVEL);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // TODO: Test how the Alert shows after Level 5 (the last level)
        // If the player won
        if (gameResultVictory) {
            int nextLevelNo = gameCurrentLevelNo + 1;
            builder.setTitle("You've passed Level " + gameCurrentLevelNo)
                    .setMessage("Congratulations! You've completed Level " + gameCurrentLevelNo
                            + ". Now, it's time prove yourself in Level " + nextLevelNo + "!")
                    .setPositiveButton("Move to Level " + nextLevelNo, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            communicator.increaseGameLevel();
                        }
                    })
                    .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: Direct the player to the Results screen
                            Toast.makeText(getActivity(), "Your progress has been saved for next time", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // TODO: Pass the level's point accumulated and points to reach to update the message.
            builder.setTitle("You failed")
                    .setMessage("You failed to collect 1000 points in this level. But no worries, just collect 1 extra minute to complete your progress, or simply retry.")
                    .setPositiveButton("View Results", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "Here are your game results", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNeutralButton("Add 1min", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "You have added 1 minute", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            communicator.restartGameLevel();
                        }
                    });
        }

        return builder.create();
    }
}
