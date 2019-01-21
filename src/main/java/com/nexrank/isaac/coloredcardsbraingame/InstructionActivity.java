package com.nexrank.isaac.coloredcardsbraingame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InstructionActivity extends AppCompatActivity {
    private ListView listView;
    private String[] instructions = {
            "Start a new game to play from scratch",
            "Follow the instructions",
            "Concentrate"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        listView = findViewById(R.id.listView_Instructions);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, instructions);
        listView.setAdapter(adapter);
    }
}
