package com.example.mydiary.pagemanager;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.R;
import com.example.mydiary.data.diary.Page;

import java.text.DateFormat;

class DiaryRecyclerViewHolder extends RecyclerView.ViewHolder {
    static final int LAYOUT = R.layout.card_diary_page;

    private static final int TITLE_ID = R.id.card_page_title;
    private static final int MOOD_ID = R.id.card_page_mood;
    private static final int DATE_ID = R.id.card_page_date;

    private final View itemView;
    private final TextView titleTextView;
    private final TextView moodTextView;
    private final TextView dateTextView;

    private Page page;

    DiaryRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        titleTextView = itemView.findViewById(TITLE_ID);
        moodTextView = itemView.findViewById(MOOD_ID);
        dateTextView = itemView.findViewById(DATE_ID);
    }

    void loadPage(Page page){
        this.page = page;

        titleTextView.setText(page.getTitle());
        moodTextView.setText(page.getMood().toString());
        dateTextView.setText(DateFormat.getDateInstance().format(page.getDate()));
    }
}
