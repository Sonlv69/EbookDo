package com.kiluss.ebookdo.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kiluss.ebookdo.OnTextClickListener;
import com.kiluss.ebookdo.R;

import java.util.ArrayList;

public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<String> searchData;
    private OnTextClickListener listener;

    public SearchHistoryAdapter(ArrayList<String> searchData, OnTextClickListener listener) {
        this.searchData = searchData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // tao layout view holder tuy theo loai view item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history,parent,false);
        return new SearchHistoryAdapter.SearchHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        populateItemRows((SearchHistoryAdapter.SearchHistoryHolder) viewHolder, position);
    }

    @Override
    public int getItemCount() {
        return searchData == null ? 0 : searchData.size();
    }


    class SearchHistoryHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private ImageView deleteBtn;

        public SearchHistoryHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_search_history_item);
            deleteBtn = itemView.findViewById(R.id.img_delete_history);
        }
    }


    private void populateItemRows(SearchHistoryAdapter.SearchHistoryHolder holder, int position) {
        String searchText = searchData.get(position);
        holder.tvName.setText(searchText);
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTextClick(searchText);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeleteClick(position);
            }
        });
    }


}
