package com.nexrank.isaac.coloredcardsbraingame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDialogFragment extends DialogFragment {

    // Fragment arguments to pass when a new instance is created
    private static final String INSTANCE_TYPE = "instanceType";
    private static final String ARGS_VICTORY = "argsVictory";
    private static final String ARGS_CURRENT_LEVEL = "argsCurrentLevel";
    private static final String ARGS_FINAL_POITNS = "argsFinalPoints";

    // Strings
    private final String NEXT_LEVEL = "NEXT LEVEL";
    private final String VIEW_RESULTS = "VIEW RESULTS";
    private final String ADD_TIME = "ADD 1 MINUTE";
    private final String RESTART = "RESTART";
    private final String RESUME = "RESUME";
    private final String GO_BACK = "GO BACK";

    private boolean gameResultVictory; // True for Win / False for Loss
    private int gameCurrentLevelNo; // Just-ended level
    private long gameFinalPoints;
    private boolean gamePaused;
    private boolean gameCancelled;
    private boolean gameReturnToSplash;
    private boolean gameSwitchingToInstructions;

    private TextView textView_dialogTitle;
    private TextView textView_dialogMessage;
    private ImageView imageView_dialogMessage;
    private Button button_1;
    private Button button_2;

    private Communicator communicator;

    public static CustomDialogFragment newInstance(Boolean victory, int levelNo, long points) {
        CustomDialogFragment dialogFragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(INSTANCE_TYPE, DialogType.GAME_OVER_DIALOG);
        args.putBoolean(ARGS_VICTORY, victory);
        args.putInt(ARGS_CURRENT_LEVEL, levelNo);
        args.putLong(ARGS_FINAL_POITNS, points);
        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false); // Prevent the fragment from closing when the user taps outside
        return dialogFragment;
    }

    public static CustomDialogFragment newInstance(DialogType dialogType) {
        CustomDialogFragment dialogFragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(INSTANCE_TYPE, dialogType);
        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false); // Prevent the dialog from dismissing until the player makes a selection
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
            if (getArguments().getSerializable(INSTANCE_TYPE) == DialogType.GAME_OVER_DIALOG) {
                gameResultVictory = getArguments().getBoolean(ARGS_VICTORY);
                gameCurrentLevelNo = getArguments().getInt(ARGS_CURRENT_LEVEL);
                gameFinalPoints = getArguments().getLong(ARGS_FINAL_POITNS);
            } else if (getArguments().getSerializable(INSTANCE_TYPE) == DialogType.PAUSE_GAME_DIALOG) {
                gamePaused = true;
            } else if (getArguments().getSerializable(INSTANCE_TYPE) == DialogType.CANCEL_GAME_DIALOG) {
                gameCancelled = true;
            } else if (getArguments().getSerializable(INSTANCE_TYPE) == DialogType.GO_BACK_DIALOG) {
                gameReturnToSplash = true;
            } else if (getArguments().getSerializable(INSTANCE_TYPE) == DialogType.VIEW_INSTRUCTIONS) {
                gameSwitchingToInstructions = true;
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(getCustomDialogViews()); // Initialize the TextViews as well

        // If the player won
        if (gameResultVictory) {
            // Create the victory messages to show
            int nextLevelNo = gameCurrentLevelNo + 1;
            String nextLevelNickname = GameLevel.newInstance(nextLevelNo).getLevelNickname();
            String dialogTitle = nextLevelNickname + " Level Unlocked!";
            String dialogMsg = getString(R.string.congratulations) +
                    "\n\n" + getString(R.string.you_ve_reached) + " " + gameFinalPoints + " " +
                    getString(R.string.points_exclamation);
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
                    dismiss(); // Dismiss the fragment
                    communicator.increaseGameLevel();
                }
            });

            // Action when the user clicks on "View Results"
            button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss(); // Dismiss the fragment
                    communicator.handleResults();
                }
            });

        } else if (gamePaused) {
            String dialogTitle = getString(R.string.game_paused);
            String dialogMSG = getString(R.string.the_game_is_currently_paused);
            textView_dialogTitle.setText(dialogTitle);
            textView_dialogMessage.setText(dialogMSG);
            imageView_dialogMessage.setImageResource(R.drawable.pause);
            button_1.setText(RESUME);
            button_2.setText(GO_BACK);

            button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    MenuItem item = communicator.findMenuItemByID(R.id.PauseGame);
                    communicator.resumeGame(item);
                }
            });

            button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    communicator.goBackSaveState();
                    getActivity().finish();
                }
            });
        } else if (gameCancelled) {
            String dialogTitle = getString(R.string.cancelling_game);
            String dialogMSG = getString(R.string.you_are_about_to_quit_this_game);
            textView_dialogTitle.setText(dialogTitle);
            textView_dialogMessage.setText(dialogMSG);
            imageView_dialogMessage.setImageResource(R.drawable.cancel);
            button_1.setText(getString(R.string.yes_cancel));
            button_2.setText(getString(R.string.no_stay));

            button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    communicator.quitGame();
                }
            });

            button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    communicator.resumeGame(communicator.getMenu().findItem(R.id.PauseGame));
                }
            });
        } else if (gameSwitchingToInstructions) {
            String dialogTitle = getString(R.string.switching_to_instructions);
            String dialogMSG = getString(R.string.you_are_in_the_middle_of_a_game_continue);
            textView_dialogTitle.setText(dialogTitle);
            textView_dialogMessage.setText(dialogMSG);
            imageView_dialogMessage.setImageResource(R.drawable.question_mark);
            button_1.setText(getString(R.string.yes));
            button_2.setText(getString(R.string.no));

            button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // The user is switching to the Instruction activity
                    dismiss();
                    Intent intent = new Intent(getActivity(), InstructionActivity.class);
                    getActivity().startActivityForResult(intent, 2);
                }
            });

            button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // The user no longer wants to switch to the Instructions
                    dismiss();
                    communicator.resumeGame(communicator.getMenu().findItem(R.id.PauseGame));
                }
            });
        } else {
            // If the player lost
            String dialogTitle = getString(R.string.you_failed);
            String dialogMsg = getString(R.string.you_did_not_gather_enough_points) + "\n\n" +
                    getString(R.string.finish_this_level);
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
}
