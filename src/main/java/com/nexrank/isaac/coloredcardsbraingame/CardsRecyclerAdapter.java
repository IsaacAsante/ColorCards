package com.nexrank.isaac.coloredcardsbraingame;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CardsRecyclerAdapter extends RecyclerView.Adapter<CardsRecyclerAdapter.CardsViewHolder> {

    private Context context;
    private ArrayList<ColorCard> cardList;
    private OnCardClickListener mlistener;

    public CardsRecyclerAdapter(Context context, ArrayList<ColorCard> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_card, viewGroup, false);
        CardsViewHolder holder = new CardsViewHolder(view, mlistener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardsViewHolder viewHolder, int i) {
        ColorCard card = cardList.get(i);
        viewHolder.card.setBackgroundColor(ContextCompat.getColor(context, card.getColor()));
        viewHolder.cardColor.setText(card.getColorName());
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public interface OnCardClickListener {
        public void onCardClick(int position);
    }

    public void setOnCardClickListener(OnCardClickListener listener) {
        mlistener = listener;
    }

    public static class CardsViewHolder extends RecyclerView.ViewHolder {
        private CardView card;
        private TextView cardColor;
        public CardsViewHolder(@NonNull View itemView, final OnCardClickListener listener) {
            super(itemView);
            card = itemView.findViewById(R.id.cardView_SingleCard);
            cardColor = itemView.findViewById(R.id.textView_CardColor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        // If FragmentCards is implementing the OnCardClickListener interface
                        int position = getAdapterPosition(); // Get the itemview's position in the adapter
                        if (position != RecyclerView.NO_POSITION) // If the position is valid {
                            listener.onCardClick(position);
                        }
                    }
                }
            );
        }
    }
}
