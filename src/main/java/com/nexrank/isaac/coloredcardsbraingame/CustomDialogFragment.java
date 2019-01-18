package com.nexrank.isaac.coloredcardsbraingame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Base64Utils;

public class CustomDialogFragment extends DialogFragment {

    // Fragment arguments to pass when a new instance is created
    private static final String ARGS_VICTORY = "argsVictory";
    private static final String ARGS_CURRENT_LEVEL = "argsCurrentLevel";
    private static final String ARGS_FINAL_POITNS = "argsFinalPoints";

    // Strings
    private final String NEXT_LEVEL = "NEXT LEVEL";
    private final String VIEW_RESULTS = "VIEW RESULTS";
    private final String ADD_TIME = "ADD 1 MINUTE";
    private final String RESTART = "RESTART";

    private boolean gameResultVictory; // True for Win / False for Loss
    private int gameCurrentLevelNo; // Just-ended level
    private long gameFinalPoints;

    private TextView textView_dialogTitle;
    private TextView textView_dialogMessage;
    private ImageView imageView_dialogMessage;
    private Button button_1;
    private Button button_2;

    private Communicator communicator;

    public static CustomDialogFragment newInstance(Boolean victory, int levelNo, long points) {
        CustomDialogFragment dialogFragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGS_VICTORY, victory);
        args.putInt(ARGS_CURRENT_LEVEL, levelNo);
        args.putLong(ARGS_FINAL_POITNS, points);
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
            gameFinalPoints = getArguments().getLong(ARGS_FINAL_POITNS);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(getCustomDialogViews()); // Initialize the TextViews as well

        // TODO: Test how the Alert shows after Level 5 (the last level)
        // If the player won
        if (gameResultVictory) {
            // Create the victory messages to show
            int nextLevelNo = gameCurrentLevelNo + 1;
            String nextLevelNickname = GameLevel.newInstance(nextLevelNo).getLevelNickname();
            String dialogTitle = nextLevelNickname + " Rank Unlocked!";
            String dialogMsg = "Congratulations! " +
                    "\n\nYou've reached " + gameFinalPoints + " points!";
            String button1Text = NEXT_LEVEL;
            String button2Text = VIEW_RESULTS;


            // Set TextView values
            textView_dialogTitle.setText(dialogTitle);
            textView_dialogMessage.setText(dialogMsg);
            imageView_dialogMessage.setImageResource(R.drawable.level_complete);
            button_1.setText(button1Text);
            button_2.setText(button2Text);

            // Action when the user clicks on "Next Level"
            button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    communicator.increaseGameLevel();
                    dismiss(); // Dismiss the fragment
                }
            });

            // Action when the user clicks on "View Results"
            button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss(); // Dismiss the fragment
                    Intent intent = new Intent(getActivity(), GameResultActivity.class);
                    intent.putExtra("gameLevel", gameCurrentLevelNo);
                    intent.putExtra("userProgress", communicator.retrieveUserProgressData()); // Grab points accumulated, points to reach and total points
                    intent.putExtra("gameResult", communicator.retrieveGameResultData()); // Grab the number of correct, wrong and skipped answers
                    startActivity(intent);
                }
            });

        } else {
            // If the player lost
            String dialogTitle = "You Failed!";
            String dialogMsg = "Sorry, you did not gather enough points." +
                    "\n\nFinish this level by extending your time, or try again.";
            String button1Text = ADD_TIME;
            String button2Text = RESTART;

            // Set TextView values
            textView_dialogTitle.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey));
            textView_dialogTitle.setText(dialogTitle);
            textView_dialogMessage.setText(dialogMsg);
            imageView_dialogMessage.setImageResource(R.drawable.level_incomplete);
            button_1.setText(button1Text);
            button_2.setText(button2Text);

            button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    // Add the bonus time
                    communicator.showRewardedVideoAd();
                }
            });

            button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    // Restart the level
                    communicator.restartGameLevel();
                }
            });
        }

        Dialog customDialog = builder.create();

        // Disable default title
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return customDialog;
    }

    // Used by the main MainActivity
    public void dismissFragment() {
        dismiss();
    }
}
