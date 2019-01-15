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

    private Boolean gameResultVictory; // True for Win / False for Loss

    private Communicator communicator;

    public static CustomDialogFragment newInstance(Boolean victory) {
        CustomDialogFragment dialogFragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGS_VICTORY, victory);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    public void setCommunicator (Context context) {
        communicator = (Communicator) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            gameResultVictory = getArguments().getBoolean(ARGS_VICTORY);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // If the player won
        if (gameResultVictory) {
            builder.setTitle("You've passed Level 1")
                    .setMessage("Congratulations! You've completed Level 1. You are now an Intermediate player. It's now time to prove yourself in Level 2!")
                    .setPositiveButton("Move to Level 2", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            communicator.increaseGameLevel();
                        }
                    })
                    .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "Your progress has been saved for next time", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
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
                            Toast.makeText(getActivity(), "You have decided to stop playing", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return builder.create();
    }
}
