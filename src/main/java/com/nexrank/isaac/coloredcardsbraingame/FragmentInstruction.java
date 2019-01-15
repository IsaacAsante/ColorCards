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
        String[] colorNames = { "RED", "PINK", "PURPLE", "BLUE"};
        int[] colors = { R.color.red, R.color.pink, R.color.purple, R.color.blue };
        int randomNo = new Random().nextInt(4);
        textView2_Color.setText(colorNames[randomNo]);
        textView2_Color.setTextColor(ContextCompat.getColor(getActivity(), colors[randomNo]));
    }

    public void increaseQuestionNumber() {
        questionNo++;
        textView_QuestionNumber.setText("#" + Integer.toString(questionNo) + ":");
    }

    public void changeInstructionTextColor (int color) {
        textView2_Color.setTextColor(ContextCompat.getColor(getActivity(), color));
    }

    public void updateInstructionColorName(String colorName) {
        textView2_Color.setText(colorName);
    }

    public boolean isCorrect(String colorName) {
        if (textView2_Color.getText().toString().equals(colorName)) {
            return true;
        } return false;
    }
}
