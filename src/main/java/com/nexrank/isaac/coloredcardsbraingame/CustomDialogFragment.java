package com.nexrank.isaac.coloredcardsbraingame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class CustomDialogFragment extends DialogFragment {

    private static final String ARGS_TITLE = "argsTitle";
    private static final String ARGS_MESSAGE = "argsMessage";
    private static final String ARGS_VICTORY = "argsVictory";

    private String gameResultTitle; // E.g. You won / lost
    private String gameResultMessage; // Prompt the user to play again OR add time
    private Boolean gameResultVictory; // True for Win / False for Loss

    public static CustomDialogFragment newInstance(Boolean victory, String title, String message) {
        CustomDialogFragment dialogFragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGS_VICTORY, victory);
        args.putString(ARGS_TITLE, title);
        args.putString(ARGS_MESSAGE, message);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            gameResultVictory = getArguments().getBoolean(ARGS_VICTORY);
            gameResultTitle = getArguments().getString(ARGS_TITLE);
            gameResultMessage = getArguments().getString(ARGS_MESSAGE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(gameResultTitle)
                .setMessage(gameResultMessage);

        // If the player won
        if (gameResultVictory) {
            builder.setPositiveButton("Next Level", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), "You have moved to the next level", Toast.LENGTH_SHORT).show();
                }
            })
                    .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "Your progress has been saved for next time", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            builder.setPositiveButton("Add 1min", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), "You have 1 minute", Toast.LENGTH_SHORT).show();
                }
            })
                    .setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "You have restarted the game", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "You have decided to stop playing", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return builder.create();
    }
}
