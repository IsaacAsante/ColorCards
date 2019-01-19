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

    // Shared Preferences
    private final String KEY_CORRECT_ANSWER_COUNT = "correctAnswerCount";
    private final String KEY_WRONG_ANSWER_COUNT = "wrongAnswerCount";
    private final String KEY_SKIPPED_ANSWER_COUNT = "skippedAnswerCount";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        textView_CorrectAnswerResult = view.findViewById(R.id.textView_CorrectResult);
        textView_WrongAnswerResult = view.findViewById(R.id.textView_WrongResult);
        textView_SkippedAnswerResult = view.findViewById(R.id.textView_SkippedResult);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showAllResults(); // Making sure the values are not empty
    }

    public void setCommunicator (Context context) {
        communicator = (Communicator) context;
    }

    public void setCorrectAnswerCount(int correctAnswerCount) {
        this.correctAnswerCount = correctAnswerCount;
    }

    public void setWrongAnswerCount(int wrongAnswerCount) {
        this.wrongAnswerCount = wrongAnswerCount;
    }

    public void setSkippedAnswerCount(int skippedAnswerCount) {
        this.skippedAnswerCount = skippedAnswerCount;
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

    public void resetResults() {
        correctAnswerCount = 0;
        wrongAnswerCount = 0;
        skippedAnswerCount = 0;
    }

    public void showAllResults() {
        textView_CorrectAnswerResult.setText(String.valueOf(correctAnswerCount));
        textView_WrongAnswerResult.setText(String.valueOf(wrongAnswerCount));
        textView_SkippedAnswerResult.setText(String.valueOf(skippedAnswerCount));
    }

    public Bundle provideGameResultInfo() {
        Bundle gameResult = new Bundle();
        gameResult.putInt(KEY_CORRECT_ANSWER_COUNT, correctAnswerCount);
        gameResult.putInt(KEY_WRONG_ANSWER_COUNT, wrongAnswerCount);
        gameResult.putInt(KEY_SKIPPED_ANSWER_COUNT, skippedAnswerCount);
        return gameResult;
    }

}
