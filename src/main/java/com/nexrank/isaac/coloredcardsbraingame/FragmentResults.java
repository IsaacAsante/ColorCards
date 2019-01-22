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

    private long correctAnswerCount = 0;
    private long wrongAnswerCount = 0;
    private long skippedAnswerCount = 0;

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

    public void setCorrectAnswerCount(long correctAnswerCount) {
        this.correctAnswerCount = correctAnswerCount;
    }

    public void setWrongAnswerCount(long wrongAnswerCount) {
        this.wrongAnswerCount = wrongAnswerCount;
    }

    public void setSkippedAnswerCount(long skippedAnswerCount) {
        this.skippedAnswerCount = skippedAnswerCount;
    }

    public String displayResultCount(long result) {
        String resultString = "";
        if (result > 10000 && result <= 50000) {
            resultString = "10k+";
        } else if (result > 50000 && result <= 100000) {
            resultString = "50k+";
        } else if (result > 100000 && result <= 500000) {
            resultString = "100k+";
        } else if (result > 500000 && result <= 1000000) {
            resultString = "500k+";
        } else if (result > 1000000 && result <= 5000000) {
            resultString = "1m+";
        } else if (result > 5000000 && result <= 10000000) {
            resultString = "5m+";
        } else if (result > 10000000 && result <= 50000000) {
            resultString = "10m+";
        } else if (result > 50000000 && result <= 100000000) {
            resultString = "50m+";
        } else if (result > 100000000 && result <= 500000000) {
            resultString = "100m+";
        } else if (result > 500000000 && result <= 1000000000) {
            resultString = "500m+";
        } else if (result > 1000000000) {
            resultString = "1b+";
        } else {
            resultString = String.valueOf(result);
        }
        return resultString;
    }

    public void increaseCorrectAnswerCount() {
        correctAnswerCount++;
        textView_CorrectAnswerResult.setText(displayResultCount(correctAnswerCount));
    }

    public void increaseWrongAnswerCount() {
        wrongAnswerCount++;
        textView_WrongAnswerResult.setText(displayResultCount(wrongAnswerCount));
    }

    public void increaseSkippedAnswerCount() {
        skippedAnswerCount++;
        textView_SkippedAnswerResult.setText(displayResultCount(skippedAnswerCount));
    }

    public void resetResults() {
        correctAnswerCount = 0;
        wrongAnswerCount = 0;
        skippedAnswerCount = 0;
    }

    public void showAllResults() {
        textView_CorrectAnswerResult.setText(displayResultCount(correctAnswerCount));
        textView_WrongAnswerResult.setText(displayResultCount(wrongAnswerCount));
        textView_SkippedAnswerResult.setText(displayResultCount(skippedAnswerCount));
    }

    public Bundle provideGameResultInfo() {
        Bundle gameResult = new Bundle();
        gameResult.putLong(KEY_CORRECT_ANSWER_COUNT, correctAnswerCount);
        gameResult.putLong(KEY_WRONG_ANSWER_COUNT, wrongAnswerCount);
        gameResult.putLong(KEY_SKIPPED_ANSWER_COUNT, skippedAnswerCount);
        return gameResult;
    }

}
