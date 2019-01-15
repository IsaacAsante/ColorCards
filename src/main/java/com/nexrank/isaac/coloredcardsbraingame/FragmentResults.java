package com.nexrank.isaac.coloredcardsbraingame;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentResults extends Fragment {

    private int correctAnswerCount = 0;
    private int wrongAnswerCount = 0;
    private int skippedAnswerCount = 0;

    private TextView textView_CorrectAnswerResult;
    private TextView textView_WrongAnswerResult;
    private TextView textView_SkippedAnswerResult;

    private Communicator communicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        textView_CorrectAnswerResult = view.findViewById(R.id.textView_CorrectResult);
        textView_WrongAnswerResult = view.findViewById(R.id.textView_WrongResult);
        textView_SkippedAnswerResult = view.findViewById(R.id.textView_SkippedResult);
        return view;
    }

    public void setCommunicator (Context context) {
        communicator = (Communicator) context;
    }

    public int getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public int getWrongAnswerCount() {
        return wrongAnswerCount;
    }

    public int getSkippedAnswerCount() {
        return skippedAnswerCount;
    }

    public void increaseCorrectAnswerCount() {
        correctAnswerCount++;
        textView_CorrectAnswerResult.setText(String.valueOf(correctAnswerCount));
    }

    public void increaseWrongAnswerCount() {
        wrongAnswerCount++;
        textView_WrongAnswerResult.setText(String.valueOf(wrongAnswerCount));
    }

    public void increaseSkippedAnswerCount() {
        skippedAnswerCount++;
        textView_SkippedAnswerResult.setText(String.valueOf(skippedAnswerCount));
    }

}
