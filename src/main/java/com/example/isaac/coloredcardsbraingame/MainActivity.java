package com.example.isaac.coloredcardsbraingame;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements Communicator {

    private FragmentCards fragmentCards;
    private FragmentInstruction fragmentInstruction;
    private FragmentProgressBar fragmentProgressBar;
    private FragmentResults fragmentResults;
    private FragmentUserProgress fragmentUserProgress;
    private ImageView imageViewCorrect;
    private ImageView imageViewWrong;
    private AnimationDrawable correctAnimation;
    private AnimationDrawable wrongAnimation;

    private MediaPlayer correctFX;
    private MediaPlayer wrongFX;

    private boolean timeUp;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViewCorrect = (ImageView) findViewById(R.id.imageView_Correct);
        imageViewCorrect.setBackgroundResource(R.drawable.correct);
        correctAnimation = (AnimationDrawable) imageViewCorrect.getBackground();

        imageViewWrong = (ImageView) findViewById(R.id.imageView_Wrong);
        imageViewWrong.setBackgroundResource(R.drawable.wrong);
        wrongAnimation = (AnimationDrawable) imageViewWrong.getBackground();

        correctFX = MediaPlayer.create(this, R.raw.correct);
        wrongFX = MediaPlayer.create(this, R.raw.error);

        timeUp = true;
        ConnectFragments();

        // Initialize Mobile Ads
        // TODO: Update the App ID to reflect the one from my AdMob account in the production app
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.bottomAdViewBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void ConnectFragments() {
        fragmentCards = new FragmentCards();
        fragmentInstruction = new FragmentInstruction();
        fragmentProgressBar = new FragmentProgressBar();
        fragmentResults = new FragmentResults();
        fragmentUserProgress = new FragmentUserProgress();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_RecycleView, fragmentCards, "FragmentCards");
        transaction.replace(R.id.frameLayout_Instruction, fragmentInstruction, "FragmentInstructions");
        transaction.replace(R.id.frameLayout_ProgressBar, fragmentProgressBar, "FragmentProgressBar");
        transaction.replace(R.id.frameLayout_results, fragmentResults, "FragmentResults");
        transaction.replace(R.id.frameLayout_UserProgress, fragmentUserProgress, "FragmentUserProgress");
        transaction.commit();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof FragmentCards) {
            FragmentCards fragmentCards = (FragmentCards) fragment;
            fragmentCards.setCommunicator(this); // MainActivity for FragmentCards
        }
        else if (fragment instanceof FragmentProgressBar) {
            FragmentProgressBar fragmentProgressBar = (FragmentProgressBar) fragment;
            fragmentProgressBar.setCommunicator(this);
        }
        else if (fragment instanceof FragmentResults) {
            FragmentResults fragmentResults = (FragmentResults) fragment;
            fragmentResults.setCommunicator(this);
            System.out.println("Fragment Results communicator context.");
        }
        else if (fragment instanceof FragmentUserProgress) {
            FragmentUserProgress fragmentTimer = (FragmentUserProgress) fragment;
            fragmentTimer.setCommunicator(this);
        }
    }

    @Override
    public void increaseQuestionID() {
        fragmentInstruction.increaseQuestionNumber();
    }

    @Override
    public void changeTextColor(int color) {
        fragmentInstruction.changeInstructionTextColor(color);
    }

    @Override
    public void changeTextInstruction(String colorName) {
        fragmentInstruction.updateInstructionColorName(colorName);
    }

    @Override
    public void verifyAnswer(String colorName) {
        if (fragmentInstruction.isCorrect(colorName)) {
            if (correctAnimation.isRunning()) {
                correctAnimation.stop(); // Guarantee animation restart effect
            }
            correctAnimation.start();
            playCorrectSound();
            updateUserPoints(AnswerResult.CORRECT);

            fragmentResults.increaseCorrectAnswerCount(); // Increase the count of correct taps
        }
        else {
            Toast.makeText(this, "The answer is wrong", Toast.LENGTH_SHORT).show(); // DEBUGGING PURPOSES
            if (wrongAnimation.isRunning()) {
                wrongAnimation.stop();
            }
            wrongAnimation.start();
            playWrongSound();
            updateUserPoints(AnswerResult.WRONG);

            fragmentResults.increaseWrongAnswerCount(); // Increase the count of wrong taps
        }
    }

    @Override
    public void resetProgressBar() {
        fragmentProgressBar.refillProgress();
    }

    @Override
    public boolean gameIsRunning() {
        return timeUp;
    }

    public void setTimeUp(boolean status) {
        timeUp = status;
    }

    @Override
    public void updateUserPoints(AnswerResult correct) {
        if (fragmentUserProgress.isAdded()) {
            switch (correct) {
                case CORRECT:
                    fragmentUserProgress.increaseUserPoints(); // Increase the player's points
                    break;
                case WRONG:
                    fragmentUserProgress.decreaseUserPoints(); // Decrease the player's points
                    break;
            }
        }
    }

    @Override
    public void resetPointsTextColor() {
        fragmentUserProgress.resetUserPointsTextColor();
    }

    @Override
    public void presentNewChallenge() {
        fragmentCards.shuffleColorCardList(); // Shuffle the list of cards
        // fragmentInstruction.increaseQuestionNumber(); // Increase the ID of the question
        changeTextColor(fragmentCards.selectRandomColor());
        changeTextInstruction(fragmentCards.selectRandomColorName()); // Change the name of the color to select
        resetProgressBar();
    }

    @Override
    public void updateMissedCount() {
        fragmentResults.increaseSkippedAnswerCount();
    }

    public void playCorrectSound() {
        // Audio source: https://freesound.org/people/LittleRainySeasons/sounds/335908/
        if (correctFX.isPlaying()) {
            correctFX.seekTo(0);
        }
        correctFX.start();
    }

    public void playWrongSound() {
        // Audio source: https://freesound.org/people/Autistic%20Lucario/sounds/142608/
        if (wrongFX.isPlaying()) {
            wrongFX.seekTo(0);
        }
        wrongFX.start();
    }
}
