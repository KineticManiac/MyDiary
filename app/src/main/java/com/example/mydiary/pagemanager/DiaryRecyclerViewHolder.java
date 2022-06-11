package com.example.mydiary.pagemanager;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.R;
import com.example.mydiary.data.diary.DiaryPage;

import java.text.DateFormat;

class DiaryRecyclerViewHolder extends RecyclerView.ViewHolder {
    static final int LAYOUT = R.layout.card_diary_page;

    private static final int TITLE_ID = R.id.card_page_title;
    private static final int MOOD_ID = R.id.card_page_mood;
    private static final int DATE_ID = R.id.card_page_date;
    private static final int CARD_ID = R.id.card_page_card;

    private final TextView titleTextView;
    private final TextView moodTextView;
    private final TextView dateTextView;
    private final CardView cardView;

    private DiaryPage page;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;

    DiaryRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(TITLE_ID);
        moodTextView = itemView.findViewById(MOOD_ID);
        dateTextView = itemView.findViewById(DATE_ID);

        cardView = itemView.findViewById(CARD_ID);
        cardView.setOnClickListener(view -> onClick());
        cardView.setOnLongClickListener(view -> onLongClick());
    }

    private boolean onLongClick() {
        if(onLongClickListener != null)
            return onLongClickListener.onLongClick(this);
        else
            return false;
    }

    DiaryPage getPage(){
        return page;
    }

    void loadPage(DiaryPage page){
        this.page = page;

        titleTextView.setText(page.getTitle());
        moodTextView.setText(page.getMood().toString());
        dateTextView.setText(DateFormat.getDateInstance().format(page.getDate()));
    }

    void setSelected(boolean selected){
        cardView.setCardBackgroundColor(selected ? Color.parseColor("#ff0099cc") : Color.WHITE);
    }

    void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    void setOnLongClickListener(OnLongClickListener onLongClickListener){
        this.onLongClickListener = onLongClickListener;
    }

    private void onClick(){
        if(onClickListener != null)
            onClickListener.onClick(this);
    }

    interface OnClickListener{
        void onClick(DiaryRecyclerViewHolder viewHolder);
    }

    interface OnLongClickListener {
        boolean onLongClick(DiaryRecyclerViewHolder viewHolder);
    }
}
