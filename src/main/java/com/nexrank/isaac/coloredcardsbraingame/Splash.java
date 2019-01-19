package com.nexrank.isaac.coloredcardsbraingame;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Splash extends AppCompatActivity {

    private final int REQUEST_CODE = 1;
    private final String KEY_GAME_TYPE = "type";
    private final String KEY_GAME_STATUS = "status";
    private final int NEW_GAME = 1;
    private final int EXISTING_GAME = 0;

    private Button button_PlayNow;
    private Button button_ResumeGame;
    private Button button_Instruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        button_PlayNow = findViewById(R.id.button_PlayNow);
        button_ResumeGame = findViewById(R.id.button_ResumeGame);
        button_Instruction = findViewById(R.id.button_ViewInstructions);
        button_ResumeGame.setVisibility(View.GONE); // By default, it should not take any space

        setButtonOnClickListeners();
    }

    private void setButtonOnClickListeners() {
        button_PlayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                intent.putExtra(KEY_GAME_TYPE, NEW_GAME); // New game
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        button_ResumeGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                intent.putExtra(KEY_GAME_TYPE, EXISTING_GAME); // Existing game
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == -1) {
            if (data.getIntExtra(KEY_GAME_STATUS, 0) == EXISTING_GAME){
                button_ResumeGame.setVisibility(View.VISIBLE);
                button_PlayNow.setText("NEW GAME");
            }
            else {
                button_PlayNow.setText("PLAY NOW");
                button_ResumeGame.setVisibility(View.GONE);
            }
        }
    }
}
