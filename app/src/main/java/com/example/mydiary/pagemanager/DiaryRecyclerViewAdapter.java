package com.example.mydiary.pagemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.data.diary.DiaryPage;
import com.example.mydiary.data.diary.Diary;
import com.example.mydiary.data.diary.Page;
import com.example.mydiary.data.diary.ViewPage;

import java.util.ArrayList;
import java.util.Comparator;

class DiaryRecyclerViewAdapter extends RecyclerView.Adapter<DiaryRecyclerViewHolder> {
    private final Diary diary;
    private ArrayList<DiaryPage> pages;
    private OnClickListener onClickListener;

    DiaryRecyclerViewAdapter(Diary diary){
        super();

        this.diary = diary;
        reload(); //Technically, it's the first load, not reload.
    }

    //(Re-)load all pages
    void reload(){
        pages = new ArrayList<>();
        for(ViewPage page: diary){
            pages.add(page);
        }

        pages.sort(Page::compare);
    }

    //Reload one page
    void reload(DiaryPage page){
        int arrSize = pages.size();

        int index = pages.indexOf(page);
        pages.set(index, page);
        notifyItemChanged(index);

        int newIndex = index;
        while(newIndex < arrSize - 1 && page.compareTo(pages.get(newIndex + 1)) > 0){
            newIndex++;
        }

        while(newIndex > 0 && page.compareTo(pages.get(newIndex - 1)) < 0) {
            newIndex--;
        }

        if(index != newIndex){
            pages.remove(index);
            pages.add(newIndex, page);
            notifyItemMoved(index, newIndex);
        }
    }

    void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
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
        holder.setOnClickListener(viewHolder -> onClick(viewHolder));
    }

    private void onClick(DiaryRecyclerViewHolder viewHolder){
        if(onClickListener != null)
            onClickListener.onClick(viewHolder.getPage());
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    interface OnClickListener{
        void onClick(DiaryPage page);
    }
}
