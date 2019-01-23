package com.nexrank.isaac.coloredcardsbraingame;

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
        colorCards = new ArrayList<ColorCard>();
        // Create the list of card objects
        populateColorCardsList();

        View view = inflater.inflate(R.layout.fragment_cards, container, false);
        // Set up the RecycleView
        buildRecycleView(view);


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
        recyclerView.setHasFixedSize(true); // The size does not change when the adapter contents change.
        adapter.setOnCardClickListener(new CardsRecyclerAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(int position) {
                communicator.verifyAnswer(colorCards.get(position).getColorName());
                communicator.presentNewChallenge(); // 5 methods called
            }
        });
    }

    private void populateColorCardsList() {
        colorCards.add(new ColorCard(getString(R.string.red), R.color.red));
        colorCards.add(new ColorCard(getString(R.string.pink), R.color.pink));
        colorCards.add(new ColorCard(getString(R.string.purple), R.color.purple));
        colorCards.add(new ColorCard(getString(R.string.blue), R.color.blue));
        colorCards.add(new ColorCard(getString(R.string.green), R.color.green));
        colorCards.add(new ColorCard(getString(R.string.orange), R.color.orange));
        colorCards.add(new ColorCard(getString(R.string.brown), R.color.brown));
        colorCards.add(new ColorCard(getString(R.string.grey), R.color.grey));
        colorCards.add(new ColorCard(getString(R.string.black), R.color.black));
    }

    public String selectRandomColorName() {
        int cardIndex = new Random().nextInt(4);
        String colorName = colorCards.get(cardIndex).getColorName();
        return colorName;
    }

    public int selectRandomColor() {
        int cardIndex = new Random().nextInt(4);
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
