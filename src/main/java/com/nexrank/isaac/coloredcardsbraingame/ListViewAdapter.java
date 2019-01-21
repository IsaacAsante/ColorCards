package com.nexrank.isaac.coloredcardsbraingame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<String> {

    public ListViewAdapter(Context context, ArrayList<String> list) {
        super(context, 0, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_instruction, parent, false);
        }

        TextView singleInstruction = convertView.findViewById(R.id.textView_SingleInstruction);
        singleInstruction.setText(getItem(position));

        return convertView;
    }
}
