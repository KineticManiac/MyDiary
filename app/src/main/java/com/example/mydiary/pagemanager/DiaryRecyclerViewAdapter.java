package com.example.mydiary.pagemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.data.diary.Diary;
import com.example.mydiary.data.diary.DiaryPage;
import com.example.mydiary.data.diary.Page;
import com.example.mydiary.data.diary.ViewPage;

import java.util.ArrayList;
import java.util.List;

class DiaryRecyclerViewAdapter extends RecyclerView.Adapter<DiaryRecyclerViewHolder> {
    private final Diary diary;
    private ArrayList<DiaryPage> pages;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;

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

    void remove(DiaryPage page){
        int index = pages.indexOf(page);
        notifyItemRemoved(index);
        pages.remove(index);
    }

    void add(DiaryPage page){
        int index = 0;
        while(index < pages.size() && page.compareTo(pages.get(index)) > 0)
            index++;
        pages.add(index, page);
        notifyItemInserted(index);
    }

    void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void setDiaryPageSelected(DiaryPage page, boolean selected){
        notifyItemChanged(pages.indexOf(page), selected);
    }

    @NonNull
    @Override
    public DiaryRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(DiaryRecyclerViewHolder.LAYOUT, null);
        return new DiaryRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryRecyclerViewHolder holder, int position, List<Object> payloads){
        if(payloads.size() == 1){
            Object payload = payloads.get(0);
            if(payload instanceof Boolean){
                boolean selected = (Boolean) payload;
                holder.setSelected(selected);
                return;
            }
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryRecyclerViewHolder holder, int position) {
        holder.loadPage(pages.get(position));
        holder.setOnClickListener(this::onClick);
        holder.setOnLongClickListener(this::onLongClick);
    }

    private boolean onLongClick(DiaryRecyclerViewHolder viewHolder) {
        if(onLongClickListener != null)
            return onLongClickListener.onLongClick(viewHolder.getPage());
        else
            return false;
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

    interface OnLongClickListener{
        boolean onLongClick(DiaryPage page);
    }
}
