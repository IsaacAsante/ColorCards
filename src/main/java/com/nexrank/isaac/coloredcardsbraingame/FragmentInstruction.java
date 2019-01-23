package com.nexrank.isaac.coloredcardsbraingame;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;


public class FragmentInstruction extends Fragment {

    private int questionNo = 1;
    private TextView textView_QuestionNumber;
    private TextView textView1;
    private TextView textView2_Color;
    private TextView textView3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instruction, container, false);
        textView_QuestionNumber = view.findViewById(R.id.textView_InstructionNumber);
        textView1 = view.findViewById(R.id.textView_InstructionA);
        textView2_Color = view.findViewById(R.id.textView_InstructionB);
        textView3 = view.findViewById(R.id.textView_InstructionC);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        forceRandomInstruction(); // Give one first instruction
    }

    public void forceRandomInstruction() {
        String[] colorNames = { getString(R.string.red), getString(R.string.pink), getString(R.string.purple), getString(R.string.blue)};
        int[] colors = { R.color.red, R.color.pink, R.color.purple, R.color.blue };
        int randomNo = new Random().nextInt(4);
        String colorName = " " + colorNames[randomNo] + " ";
        textView2_Color.setText(colorName);
        textView2_Color.setTextColor(ContextCompat.getColor(getActivity(), colors[randomNo]));
    }

    public void changeInstructionTextColor (int color) {
        textView2_Color.setTextColor(ContextCompat.getColor(getActivity(), color));
    }

    public void updateInstructionColorName(String colorName) {
        String modifiedColorName = " " + colorName + " ";
        textView2_Color.setText(modifiedColorName);
    }

    public boolean isCorrect(String colorName) {
        // Remove spaces
        // True if correct, wrong otherwise
        return textView2_Color.getText().toString().trim().equals(colorName);
    }
}
