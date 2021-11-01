package com.example.ebookdo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ebookdo.DetailBookActivity;
import com.example.ebookdo.R;
import com.example.ebookdo.model.BookDetailModel;

import java.util.ArrayList;

public class BookFilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity activity;
    private ArrayList<String> bookList;

    public BookFilesAdapter(Activity activity, ArrayList<String> bookList) {
        this.activity = activity;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // tao layout view holder tuy theo loai view item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_file,parent,false);
        return new BookFilesAdapter.BookPreviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        populateItemRows((BookFilesAdapter.BookPreviewHolder) viewHolder, position);
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }


    class BookPreviewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;

        public BookPreviewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_book_file);
        }
    }
    

    private void populateItemRows(BookFilesAdapter.BookPreviewHolder holder, int position) {

        final String book = bookList.get(position);
        holder.tvName.setText(book);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //activity.startActivity(new Intent(activity, DetailBookActivity.class).putExtra("previewBook",book));
                Toast.makeText(activity.getApplicationContext(), "Open file " + book, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
