package com.nexrank.isaac.coloredcardsbraingame;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class MainActivity extends AppCompatActivity implements Communicator, RewardedVideoAdListener {

    // TODO: Remove all hardcoded strings from layout files
    // TODO: Put all strings into Strings.xml
    // TODO: Fix all warnings in the code
    // Fragments
    private FragmentCards fragmentCards;
    private FragmentInstruction fragmentInstruction;
    private FragmentProgressBar fragmentProgressBar;
    private FragmentResults fragmentResults;
    private FragmentUserProgress fragmentUserProgress;
    private CustomDialogFragment gameOverDialog;

    // Views
    private ImageView imageViewCorrect;
    private ImageView imageViewWrong;

    // Animations
    private AnimationDrawable correctAnimation;
    private AnimationDrawable wrongAnimation;

    private MediaPlayer correctFX;
    private MediaPlayer wrongFX;

    private boolean timeIsUp;
    private int gameLevelNo;

    // AdMob ad units
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Color Match");
        imageViewCorrect = (ImageView) findViewById(R.id.imageView_Correct);
        imageViewCorrect.setBackgroundResource(R.drawable.correct);
        correctAnimation = (AnimationDrawable) imageViewCorrect.getBackground();

        imageViewWrong = (ImageView) findViewById(R.id.imageView_Wrong);
        imageViewWrong.setBackgroundResource(R.drawable.wrong);
        wrongAnimation = (AnimationDrawable) imageViewWrong.getBackground();

        correctFX = MediaPlayer.create(this, R.raw.correct);
        wrongFX = MediaPlayer.create(this, R.raw.error);

        timeIsUp = false;
        gameLevelNo = 1;

        ConnectFragments();

        // AppLovin User Consent (EU Laws)
        AppLovinPrivacySettings.setHasUserConsent(true, MainActivity.this);

        // AppLovin enable test ads
        final AppLovinSdk sdk = AppLovinSdk.getInstance(MainActivity.this);
        sdk.getSettings().setTestAdsEnabled(true);

        // Initialize Mobile Ads - Real App ID used
        MobileAds.initialize(this, "ca-app-pub-4887590264879985~8379773466");

        mAdView = findViewById(R.id.bottomAdViewBanner);
        // Test ads with my Nokia
        // TODO: Remove the test device
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("922A17FEBA1721235EF6CB6AFDD947AB")
                .build();
        mAdView.loadAd(adRequest);

        System.out.println("THE TEST DEVICE IS WORKING");

        // AppLovin
        // TODO: Cancel Test Ads from AppLovin and show Live Ads.
        AppLovinSdk.initializeSdk(MainActivity.this);

        // Load Rewarded Videos
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(MainActivity.this);
        mRewardedVideoAd.setRewardedVideoAdListener(MainActivity.this);
        loadRewardedVideoAd();
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
        if (fragment instanceof FragmentProgressBar) {
            FragmentProgressBar fragmentProgressBar = (FragmentProgressBar) fragment;
            fragmentProgressBar.setCommunicator(this);
            System.out.println("FragmentProgressBar onAttachFragment");
        } else if (fragment instanceof FragmentUserProgress) {
            FragmentUserProgress fragmentTimer = (FragmentUserProgress) fragment;
            fragmentTimer.setCommunicator(this);
            System.out.println("FragmentUserProgress onAttachFragment");
        } else if (fragment instanceof FragmentCards) {
            FragmentCards fragmentCards = (FragmentCards) fragment;
            fragmentCards.setCommunicator(this); // MainActivity for FragmentCards
            System.out.println("FragmentCards onAttachFragment");
        } else if (fragment instanceof FragmentResults) {
            FragmentResults fragmentResults = (FragmentResults) fragment;
            fragmentResults.setCommunicator(this);
            System.out.println("FragmentResults onAttachFragment");
        } else if (fragment instanceof CustomDialogFragment) {
            CustomDialogFragment customDialogFragment = (CustomDialogFragment) fragment;
            customDialogFragment.setCommunicator(this);
            System.out.println("CustomDialogFragment onAttachFragment");
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
        if (timeIsUp == false) {
            if (fragmentInstruction.isCorrect(colorName)) {
                if (correctAnimation.isRunning()) {
                    correctAnimation.stop(); // Guarantee animation restart effect
                }
                correctAnimation.start();
                playCorrectSound();
                updateUserPoints(AnswerResult.CORRECT);

                fragmentResults.increaseCorrectAnswerCount(); // Increase the count of correct taps
            } else {
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
    }

    @Override
    public void resetProgressBar() {
        fragmentProgressBar.refillProgress();
    }

    @Override
    public boolean gameIsRunning() {
        if (timeIsUp == false) {
            return true;
        } else {
            return false;
        }
    }

    public void setTimeUp(boolean status) {
        timeIsUp = status;
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
    public GameLevel getLevelDetails() {
        GameLevel level = GameLevel.newInstance(gameLevelNo);
        return level;
    }

    @Override
    public void increaseGameLevel() {
        ++gameLevelNo;
        fragmentProgressBar.displayLevelDetails(); // Display new level
        fragmentUserProgress.resetUserPoints(); // Back to 0
        fragmentUserProgress.displayPointAccumulated(); // Update user points
        fragmentUserProgress.increasePointsToReach(); // Add to the game's difficulty
        fragmentUserProgress.displayPointToReach(); // Update points
        fragmentUserProgress.cancelTimer(); // It needs to restart
        fragmentUserProgress.increaseGameLevelTime(gameLevelNo); // New level * default time
        fragmentResults.resetResults(); // Set them all to 0
        fragmentResults.showAllResults(); // Update the results

        setTimeUp(false); // Enable the player to interact with the game
        presentNewChallenge(); // Shuffle cards, refresh instructions and refill the progress bar
        fragmentUserProgress.startTimer(); // Must be the last method
    }

    @Override
    public void presentNewChallenge() {
        if (timeIsUp == false) {
            fragmentCards.shuffleColorCardList(); // Shuffle the list of cards
            // fragmentInstruction.increaseQuestionNumber(); // Increase the ID of the question
            changeTextInstruction(fragmentCards.selectRandomColorName()); // Change the name of the color to select
            changeTextColor(fragmentCards.selectRandomColor());
            resetProgressBar();
        }
    }

    @Override
    public void updateMissedCount() {
        fragmentResults.increaseSkippedAnswerCount();
    }

    @Override
    public void showRewardedVideoAd() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void showGameOverAlert(GameResult result) {
        if (result == GameResult.Win) {
            gameOverDialog = CustomDialogFragment.newInstance(true, gameLevelNo);
        } else {
            gameOverDialog = CustomDialogFragment.newInstance(false, gameLevelNo);
        }
        getSupportFragmentManager().beginTransaction().add(gameOverDialog, "GameOverDialog").commitAllowingStateLoss();
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

    // AdMob Rewarded Video - Real App Unit ID
    // TODO: Remove the test device method
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-4887590264879985/5045460278",
                new AdRequest.Builder()
                        .addTestDevice("922A17FEBA1721235EF6CB6AFDD947AB")
                        .build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        fragmentUserProgress.setBonusButtonVisibility(1); // Make the bonus visible
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
        fragmentUserProgress.cancelTimer();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // Add the bonus time, and restart the timer
        fragmentUserProgress.addBonusTime();
        fragmentUserProgress.startTimer();
        fragmentUserProgress.setBonusButtonVisibility(0); // Hide the bonus button again until the ad is ready
        loadRewardedVideoAd();
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Toast.makeText(this, "You have " + rewardItem.getAmount() + " more " + rewardItem.getType(),
                Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }
}