package com.example.isaac.coloredcardsbraingame;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class FragmentCards extends Fragment {

    private RecyclerView recyclerView;
    private static ArrayList<ColorCard> colorCards;
    private CardsRecyclerAdapter adapter;
    private Communicator communicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);
        colorCards = new ArrayList<ColorCard>();

        // Set up the RecycleView
        buildRecycleView(view);
        // Create the list of card objects
        populateColorCardsList();

        return view;
    }

    public void setCommunicator(Context context) {
        communicator = (Communicator) context;
    }

    private void buildRecycleView(View view) {
        recyclerView = view.findViewById(R.id.fragmentCards_RecycleView);
        adapter = new CardsRecyclerAdapter((Context) getActivity(), colorCards);
        recyclerView.setLayoutManager(new GridLayoutManager((Context) getActivity(), 2));
        recyclerView.setAdapter(adapter);
        adapter.setOnCardClickListener(new CardsRecyclerAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(int position) {
                communicator.verifyAnswer(colorCards.get(position).getColorName());
                communicator.presentNewChallenge(); // 5 methods called
            }
        });
    }

    private void populateColorCardsList() {
        colorCards.add(new ColorCard("RED", R.color.red));
        colorCards.add(new ColorCard("PINK", R.color.pink));
        colorCards.add(new ColorCard("PURPLE", R.color.purple));
        colorCards.add(new ColorCard("BLUE", R.color.blue));
        colorCards.add(new ColorCard("GREEN", R.color.green));
        colorCards.add(new ColorCard("ORANGE", R.color.orange));
        colorCards.add(new ColorCard("BROWN", R.color.brown));
        colorCards.add(new ColorCard("GREY", R.color.grey));
        colorCards.add(new ColorCard("BLACK", R.color.black));
    }

    public String selectRandomColorName() {
        if (!colorCards.isEmpty()) {
            int cardIndex = new Random().nextInt(4);
            String colorName = colorCards.get(cardIndex).getColorName();
            return colorName;
        }
        return "Black";
    }

    public int selectRandomColor() {
        int cardIndex = new Random().nextInt(colorCards.size());
        int randomColor = colorCards.get(cardIndex).getColor();
        return randomColor;
    }

    public void shuffleColorCardList() {
        if (!colorCards.isEmpty() || colorCards != null) {
            Collections.shuffle(colorCards);
            adapter.notifyDataSetChanged();
        }
    }
}
