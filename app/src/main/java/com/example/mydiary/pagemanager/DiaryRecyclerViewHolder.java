package com.example.mydiary.pagemanager;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.R;
import com.example.mydiary.data.diary.Page;

public class DiaryRecyclerViewHolder extends RecyclerView.ViewHolder {
    public static final int LAYOUT = R.layout.card_diary_page;
    private static final int TITLE_ID = R.id.card_page_title;

    private final View itemView;
    private final TextView titleTextView;

    private Page page;

    public DiaryRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        titleTextView = itemView.findViewById(TITLE_ID);
    }

    public void loadPage(Page page){
        this.page = page;

        titleTextView.setText(page.getMood() + " " + page.getTitle());
    }
}
