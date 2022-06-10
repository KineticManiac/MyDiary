package com.example.mydiary.pagemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.data.diary.Diary;
import com.example.mydiary.data.diary.Page;
import com.example.mydiary.data.diary.ViewPage;

import java.util.ArrayList;
import java.util.Comparator;

class DiaryRecyclerViewAdapter extends RecyclerView.Adapter<DiaryRecyclerViewHolder> {
    private final ArrayList<Page> pages;

    DiaryRecyclerViewAdapter(Diary diary){
        super();

        pages = new ArrayList<>();
        for(ViewPage page: diary){
            pages.add(page);
        }

        pages.sort(Comparator.comparing(Page::getDate));
    }

    @NonNull
    @Override
    public DiaryRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(DiaryRecyclerViewHolder.LAYOUT, null);
        return new DiaryRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryRecyclerViewHolder holder, int position) {
        holder.loadPage(pages.get(position));
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }
}
