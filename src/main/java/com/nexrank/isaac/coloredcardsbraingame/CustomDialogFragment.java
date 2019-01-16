package com.nexrank.isaac.coloredcardsbraingame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Base64Utils;

public class CustomDialogFragment extends DialogFragment {

    private static final String ARGS_VICTORY = "argsVictory";
    private static final String ARGS_CURRENT_LEVEL = "argsCurrentLevel";

    private boolean gameResultVictory; // True for Win / False for Loss
    private int gameCurrentLevelNo; // Just-ended level

    private TextView textView_dialogTitle;
    private TextView textView_dialogMessage;
    private ImageView imageView_dialogMessage;
    private Button button_1;
    private Button button_2;

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

    public View getCustomDialogViews() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.game_over_dialogfragment, null);
        textView_dialogTitle = view.findViewById(R.id.textView_CustomDialogTitle);
        textView_dialogMessage = view.findViewById(R.id.textView_CustomDialogMessage);
        imageView_dialogMessage = view.findViewById(R.id.imageView_LevelResult);
        button_1 = view.findViewById(R.id.button_Positive);
        button_2 = view.findViewById(R.id.button_Negative);
        return view;
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
        builder.setView(getCustomDialogViews()); // Initialize the TextViews as well

        // TODO: Test how the Alert shows after Level 5 (the last level)
        // If the player won
        if (gameResultVictory) {
            // Create the victory messages to show
            int nextLevelNo = gameCurrentLevelNo + 1;
            String nextLevelNickname = GameLevel.newInstance(nextLevelNo).getLevelNickname();
            String dialogTitle = "You've passed Level " + gameCurrentLevelNo + "!";
            String dialogMsg = "Congratulations! \nYour new rank is: " + nextLevelNickname + "!";
            String button1Text = "NEXT LEVEL";
            String button2Text = "VIEW RESULTS";


            // Set TextView values
            textView_dialogTitle.setText(dialogTitle);
            textView_dialogMessage.setText(dialogMsg);
            imageView_dialogMessage.setImageResource(R.drawable.level_complete);
            button_1.setText(button1Text);
            button_2.setText(button2Text);

            // TODO: Implement communicator.increaseGameLevel();

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

        Dialog customDialog = builder.create();

        // Disable default title
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return customDialog;
    }
}
