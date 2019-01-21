package com.nexrank.isaac.coloredcardsbraingame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class InstructionActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        instructions = new ArrayList<String>();
        listView = findViewById(R.id.listView_Instructions);
        getInstructions();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, instructions);
        listView.setAdapter(adapter);
    }

    private void getInstructions() {
        try {
            String line;
            String singleInstruction = "";
            int lineNumber = 0;
            InputStream inputStream = getResources().getAssets().open("instructions.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() != 0) {
                    lineNumber++;
                    if (lineNumber != 0) {
                        singleInstruction += line;
                    }

                } else {
                    lineNumber = 0;
                    instructions.add(singleInstruction);
                    singleInstruction = ""; // Reset
                }
            }
            instructions.add(singleInstruction); // Add the last instruction
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "There was an error loading instructions.", Toast.LENGTH_SHORT).show();
        }
    }
}
