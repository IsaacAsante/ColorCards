package com.nexrank.isaac.coloredcardsbraingame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    // TODO: Create an account on AppAnie for ASO
    // TODO: Remove all System.out.println()

    // Menu
    private Menu menu;

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
    private boolean existingGame; // true if the player is resuming a game
    private final int LEVEL_INVINCIBLE = 5;

    // Intent to GameResultActivity
    private static final int REQUEST_CODE = 2;
    private final String KEY_GAME_STATUS = "status";
    private final String KEY_GAME_TYPE = "type";
    private final int GAME_RESUME = 0;
    private final int NEW_GAME = 1;
    private final int EXISTING_GAME = 0;

    // AdMob ad units
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;

    // Shared Preferences
    private final String KEY_POINTS_ACCUMULATED = "pointsAccumulated";
    private final String KEY_POINTS_TO_REACH = "pointsToReach";
    private final String KEY_TIME_LEFT = "timeLeft";
    private final String KEY_CORRECT_ANSWER_COUNT = "correctAnswerCount";
    private final String KEY_WRONG_ANSWER_COUNT = "wrongAnswerCount";
    private final String KEY_SKIPPED_ANSWER_COUNT = "skippedAnswerCount";
    private final String KEY_GAME_LEVEL = "gameLevel";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.PauseGame:
                pauseActiveGame(item);
                return true;
            case R.id.QuitGame:
                askQuitGame();
                return true;
            case R.id.ViewInstructions:
                viewInstructions(item);
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Change the Action Bar's color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        imageViewCorrect = (ImageView) findViewById(R.id.imageView_Correct);
        imageViewCorrect.setBackgroundResource(R.drawable.correct);
        correctAnimation = (AnimationDrawable) imageViewCorrect.getBackground();

        imageViewWrong = (ImageView) findViewById(R.id.imageView_Wrong);
        imageViewWrong.setBackgroundResource(R.drawable.wrong);
        wrongAnimation = (AnimationDrawable) imageViewWrong.getBackground();

        correctFX = MediaPlayer.create(this, R.raw.correct);
        wrongFX = MediaPlayer.create(this, R.raw.error);

        // Default game state
        timeIsUp = false;
        gameLevelNo = 1;
        existingGame = false;

        ConnectFragments();

        // Determine whether a game is a NEW game or an EXISTING one.
        getGameType();

        // Restore any existing game if there is
        restoreExistingGame();

        // AppLovin User Consent (EU Laws)
        AppLovinPrivacySettings.setHasUserConsent(true, MainActivity.this);

        // TODO: Remove this test ad in the master branch
        // AppLovin enable test ads
        // final AppLovinSdk sdk = AppLovinSdk.getInstance(MainActivity.this);
        // sdk.getSettings().setTestAdsEnabled(true);

        // Initialize Mobile Ads - Real App ID used
        MobileAds.initialize(this, "ca-app-pub-4887590264879985~8379773466");

        mAdView = findViewById(R.id.bottomAdViewBanner);
        // Test ads with my Nokia
        // TODO: Remove the test device and set the method Builder().build() in the master branch
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // AppLovin
        AppLovinSdk.initializeSdk(MainActivity.this);

        // Load Rewarded Videos
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(MainActivity.this);
        mRewardedVideoAd.setRewardedVideoAdListener(MainActivity.this);

        // When the game starts at Level 5 from existing data, don't load the Rewarded Video Ad.
        if (gameLevelNo < LEVEL_INVINCIBLE) {
            loadRewardedVideoAd();
        }
    }

    private void getGameType() {
        try {
            int gameType = getIntent().getIntExtra(KEY_GAME_TYPE, -1);
            if (gameType == EXISTING_GAME) {
                existingGame = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } else if (fragment instanceof FragmentUserProgress) {
            FragmentUserProgress fragmentTimer = (FragmentUserProgress) fragment;
            fragmentTimer.setCommunicator(this);
        } else if (fragment instanceof FragmentCards) {
            FragmentCards fragmentCards = (FragmentCards) fragment;
            fragmentCards.setCommunicator(this); // MainActivity for FragmentCards
        } else if (fragment instanceof FragmentResults) {
            FragmentResults fragmentResults = (FragmentResults) fragment;
            fragmentResults.setCommunicator(this);
        } else if (fragment instanceof CustomDialogFragment) {
            CustomDialogFragment customDialogFragment = (CustomDialogFragment) fragment;
            customDialogFragment.setCommunicator(this);
        }
    }

    private void saveGameData() {
        Bundle userProgressBundle = fragmentUserProgress.provideUserProgressData();
        Bundle gameResults = fragmentResults.provideGameResultInfo();

        // Access or create a new file
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.Existing_Game_Info), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_GAME_LEVEL, gameLevelNo);
        editor.putLong(KEY_POINTS_ACCUMULATED, userProgressBundle.getLong(KEY_POINTS_ACCUMULATED));
        editor.putLong(KEY_POINTS_TO_REACH, userProgressBundle.getLong(KEY_POINTS_TO_REACH));
        editor.putLong(KEY_TIME_LEFT, userProgressBundle.getLong(KEY_TIME_LEFT));
        editor.putLong(KEY_CORRECT_ANSWER_COUNT, gameResults.getLong(KEY_CORRECT_ANSWER_COUNT));
        editor.putLong(KEY_WRONG_ANSWER_COUNT, gameResults.getLong(KEY_WRONG_ANSWER_COUNT));
        editor.putLong(KEY_SKIPPED_ANSWER_COUNT, gameResults.getLong(KEY_SKIPPED_ANSWER_COUNT));

        editor.apply(); // Write the updates to the disk asynchronously to avoid hanging on the UI thread.
    }

    private void restoreExistingGame() {
        if (existingGame) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.Existing_Game_Info), MODE_PRIVATE);
            fragmentUserProgress.setPointsAccumulated(sharedPreferences.getLong(KEY_POINTS_ACCUMULATED, 0));
            fragmentUserProgress.setPointsToReach(sharedPreferences.getLong(KEY_POINTS_TO_REACH, 0));
            fragmentUserProgress.setMillisForCurrentLevel(sharedPreferences.getLong(KEY_TIME_LEFT, 60000)); // 1min default
            fragmentResults.setCorrectAnswerCount(sharedPreferences.getLong(KEY_CORRECT_ANSWER_COUNT, 0));
            fragmentResults.setWrongAnswerCount(sharedPreferences.getLong(KEY_WRONG_ANSWER_COUNT, 0));
            fragmentResults.setSkippedAnswerCount(sharedPreferences.getLong(KEY_SKIPPED_ANSWER_COUNT, 0));
            gameLevelNo = sharedPreferences.getInt(KEY_GAME_LEVEL, 1); // Level 1 as the default
        }
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    @Override
    // Use this method to access menu item from fragments
    public MenuItem findMenuItemByID(int id) {
        return menu.findItem(id);
    }

    // The MenuItem parameter is required, otherwise, the application will crash.
    public void pauseActiveGame(MenuItem item) {
        // Create a new custom dialog and set it as a Pause Dialog using the correct enum value.
        CustomDialogFragment dialogFragment = CustomDialogFragment.newInstance(DialogType.PAUSE_GAME_DIALOG);
        getSupportFragmentManager().beginTransaction().add(dialogFragment, "PauseDialog").commit();

        fragmentUserProgress.cancelTimer();
        timeIsUp = true; // End the game
        item.setIcon(R.drawable.menu_icon_resume);

        saveGameData(); // Capture all the important game data
    }

    @Override
    public void resumeGame(MenuItem item) {
        timeIsUp = false;
        fragmentUserProgress.startTimer();
        item.setIcon(R.drawable.menu_icon_pause);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                pauseActiveGame(item);
                return true;
            }
        });
        Toast.makeText(this, "The game has resumed.", Toast.LENGTH_SHORT).show();
    }

    private void askQuitGame() {
        // Create a new custom dialog and set it as a Pause Dialog using the correct enum value.
        CustomDialogFragment dialogFragment = CustomDialogFragment.newInstance(DialogType.CANCEL_GAME_DIALOG);
        getSupportFragmentManager().beginTransaction().add(dialogFragment, "CancelDialog").commit();

        fragmentUserProgress.cancelTimer();
        timeIsUp = true; // End the game
        menu.findItem(R.id.PauseGame).setIcon(R.drawable.menu_icon_pause);
        saveGameData();
    }

    @Override
    public void quitGame() {

        // End game
        fragmentUserProgress.cancelTimer();
        timeIsUp = true;

        // Remove all stored data
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.Existing_Game_Info), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String key : sharedPreferences.getAll().keySet()) {
            editor.remove(key);
        }
        editor.commit(); // Synchronous on purpose

        // Notify Splash.java that the user has canceled their progress
        Intent intent = new Intent();
        intent.putExtra(KEY_GAME_STATUS, NEW_GAME); // Notify Splash.java that the user could have progressed, but returned to the Splash screen.
        setResult(RESULT_OK, intent);
        finish();
    }

    public void viewInstructions(MenuItem item) {
        // Create a new custom dialog and set it as a Pause Dialog using the correct enum value.
        CustomDialogFragment dialogFragment = CustomDialogFragment.newInstance(DialogType.VIEW_INSTRUCTIONS);
        getSupportFragmentManager().beginTransaction().add(dialogFragment, "PauseDialog").commit();

        fragmentUserProgress.cancelTimer();
        timeIsUp = true; // End the game
        item.setIcon(R.drawable.menu_icon_resume);

        saveGameData(); // Capture all the important game data
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

    @Override
    public boolean isExistingGame() {
        return existingGame;
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
        if (timeIsUp == false) {
            fragmentUserProgress.resetUserPointsTextColor();
        }
    }

    @Override
    public GameLevel getLevelDetails() {
        GameLevel level = GameLevel.newInstance(gameLevelNo);
        return level;
    }

    @Override
    public int getGameLevelNo() {
        return gameLevelNo;
    }

    @Override
    public void increaseGameLevel() {
        // Similar to restartGameLevel(), but this one increases the game level, points to reach and level time.
        fragmentUserProgress.cancelTimer(); // It needs to restart

        if (gameLevelNo < 5) {
            // Up until Level 4
            ++gameLevelNo;
            fragmentUserProgress.increasePointsToReach(); // Add to the game's difficulty
            fragmentUserProgress.displayPointToReach(); // Update points
            fragmentUserProgress.increaseGameLevelTime(gameLevelNo); // New level * default time
            fragmentUserProgress.softIncreasePointsDeductor(gameLevelNo);
            fragmentUserProgress.startTimer(); // Must be the last method
        }
        if (gameLevelNo == 5) {
            fragmentUserProgress.increasePointsDeductor(); // Up to 1,000 points for wrong answers
            fragmentUserProgress.hideLowLevelItems(); // Hide the points to reach with the slash, clock image, timer, and bonus button.
        }
        fragmentProgressBar.displayLevelDetails(); // Display new level
        fragmentUserProgress.resetUserPoints(); // Back to 0
        fragmentUserProgress.displayPointAccumulated(); // Update user points
        fragmentResults.resetResults(); // Set them all to 0
        fragmentResults.showAllResults(); // Update the results

        setTimeUp(false); // Enable the player to interact with the game

        presentNewChallenge(); // Shuffle cards, refresh instructions and refill the progress bar

    }

    @Override
    public void restartGameLevel() {
        fragmentUserProgress.resetUserPoints(); // Back to 0
        fragmentUserProgress.displayPointAccumulated(); // Update user points
        fragmentUserProgress.displayPointToReach(); // Update points
        fragmentUserProgress.cancelTimer(); // It needs to restart
        fragmentUserProgress.increaseGameLevelTime(gameLevelNo); // Current level * default time
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
        if (gameLevelNo > 4) {
            fragmentUserProgress.softDecreaseUserPoints();
        }
    }

    @Override
    public void askViewRewardedVideoAd() {
        // Create a new custom dialog and set it as a Pause Dialog using the correct enum value.
        CustomDialogFragment dialogFragment = CustomDialogFragment.newInstance(DialogType.VIEW_AD_DIALOG);
        getSupportFragmentManager().beginTransaction().add(dialogFragment, "AdDialog").commit();

        fragmentUserProgress.cancelTimer();
        timeIsUp = true; // End the game
        menu.findItem(R.id.PauseGame).setIcon(R.drawable.menu_icon_pause); // Change the pause/resume icon in the action bar
        saveGameData();
    }

    @Override
    public void showRewardedVideoAd() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void showGameOverAlert(GameResult result, long points) {
        if (result == GameResult.Win) {
            gameOverDialog = CustomDialogFragment.newInstance(true, gameLevelNo, points);
        } else {
            gameOverDialog = CustomDialogFragment.newInstance(false, gameLevelNo, points);
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

    public void handleResults() {
        Intent intent = new Intent(MainActivity.this, GameResultActivity.class);
        intent.putExtra(KEY_GAME_LEVEL, gameLevelNo);
        intent.putExtra("userProgress", fragmentUserProgress.provideUserProgressData()); // Grab points accumulated, points to reach and total points
        intent.putExtra("gameResult", fragmentResults.provideGameResultInfo()); // Grab the number of correct, wrong and skipped answers
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            // If the user wants to continue playing
            if (resultCode == RESULT_OK) {
                increaseGameLevel();
            } else if (resultCode == RESULT_FIRST_USER) {
                fragmentUserProgress.cancelTimer(); // Prevent onResume() from starting the timer
                pauseActiveGame(menu.findItem(R.id.PauseGame));
            } else {
                Intent intent = new Intent();
                intent.putExtra(KEY_GAME_STATUS, GAME_RESUME); // Notify Splash.java that the user could have progressed, but returned to the Splash screen.
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void goBackSaveState() {
        timeIsUp = true; // End the game
        Intent intent = new Intent();
        intent.putExtra(KEY_GAME_STATUS, GAME_RESUME); // Notify Splash.java that the user could have progressed, but returned to the Splash screen.
        setResult(RESULT_OK, intent);
        finish();
    }

    // AdMob Rewarded Video - Real App Unit ID
    // TODO: Remove the test device method
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-4887590264879985/5045460278",
                new AdRequest.Builder().build());
    }
    
    @Override
    public void onRewardedVideoAdLoaded() {
        if (gameLevelNo == fragmentUserProgress.getLevelInvincibleConstant()) {
            fragmentUserProgress.setBonusButtonVisibility(0); // Make the bonus visible
        } else {
            fragmentUserProgress.setBonusButtonVisibility(1); // Make the bonus visible
        }
        // Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        // Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
        fragmentUserProgress.cancelTimer();
    }

    @Override
    public void onRewardedVideoStarted() {
        // Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        if (timeIsUp == true) {
            // If the user taps the Ad button from the custom dialog fragment, make the game receptive to interactions again.
            timeIsUp = false;
        }
        // Add the bonus time, and restart the timer
        fragmentUserProgress.startTimer();
        fragmentUserProgress.setBonusButtonVisibility(0); // Hide the bonus button again until the ad is ready

        CustomDialogFragment gameOverDialog = (CustomDialogFragment) getSupportFragmentManager().findFragmentByTag("GameOverDialog");

        if (gameOverDialog != null) {
            if (gameOverDialog.isAdded()) {
                gameOverDialog.dismiss();
            }
        }
        // Load the next rewarded video ad.
        loadRewardedVideoAd();

        // Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        String rewardConfirmation = getString(R.string.you_ve_received) + " " + rewardItem.getAmount() + " " + getString(R.string.more) + " " + rewardItem.getType();
        Toast.makeText(this, rewardConfirmation,
                Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        // Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Toast.makeText(this, getString(R.string.error_loading_reward), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        // Only give the reward if the user finished the video.
        fragmentUserProgress.addBonusTime();
        // Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        goBackSaveState(); // This will also call the setResult() method.
        // finish();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        // IMPORTANT: AdMob Rewarded Video Ad
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (gameLevelNo < LEVEL_INVINCIBLE) {
            boolean fragmentInLayout = false;
                Fragment fragmentPauseDialog = getSupportFragmentManager().findFragmentByTag("PauseDialog");
                Fragment fragmentCancelDialog = getSupportFragmentManager().findFragmentByTag("CancelDialog");
                Fragment fragmentGameOverDialog = getSupportFragmentManager().findFragmentByTag("GameOverDialog");
                Fragment fragmentViewAdDialog = getSupportFragmentManager().findFragmentByTag("AdDialog");

                if (fragmentPauseDialog != null) {
                    if (fragmentPauseDialog.isAdded()) {
                        fragmentInLayout = true;
                    }
                } else if (fragmentCancelDialog != null) {
                    if (fragmentCancelDialog.isAdded()) {
                        fragmentInLayout = true;
                    }
                } else if (fragmentGameOverDialog != null) {
                    if (fragmentGameOverDialog.isAdded()) {
                        fragmentInLayout = true;
                    }
                } else if (fragmentViewAdDialog != null) {
                    if (fragmentViewAdDialog.isAdded()) {
                        fragmentInLayout = true;
                    }
                }
                else {
                    fragmentInLayout = false;
                }
                
                if (fragmentInLayout) {
                    fragmentUserProgress.cancelTimer();
                    timeIsUp = true;
                } else {
                    if (fragmentUserProgress.isTimerRunning() == false) {
                        fragmentUserProgress.startTimer();
                    }
                    timeIsUp = false;
                }
        }

        //IMPORTANT: AdMob RewardedVideoAd
        mRewardedVideoAd.resume(this);
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gameLevelNo < LEVEL_INVINCIBLE) {
            fragmentUserProgress.cancelTimer();
        }
        // timeIsUp = true; // End the game
        saveGameData();
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}
