package com.kiluss.ebookdo.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kiluss.ebookdo.BuildConfig;
import com.kiluss.ebookdo.DetailBookActivity;
import com.kiluss.ebookdo.R;
import com.kiluss.ebookdo.model.BookDetailModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class BookRelativePreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity activity;
    private ArrayList<BookDetailModel> bookList;

    public BookRelativePreviewAdapter(Activity activity, ArrayList<BookDetailModel> bookList) {
        this.activity = activity;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // tao layout view holder tuy theo loai view item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_relative,parent,false);
        return new BookRelativePreviewAdapter.BookPreviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        populateItemRows((BookRelativePreviewAdapter.BookPreviewHolder) viewHolder, position);
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }


    class BookPreviewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private ImageView imgCover;

        public BookPreviewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_book_name_relative);
            imgCover = itemView.findViewById(R.id.img_cover_relative);
        }
    }


    private void populateItemRows(BookRelativePreviewAdapter.BookPreviewHolder holder, int position) {

        final BookDetailModel book= bookList.get(position);
        holder.tvName.setText(book.getBookTitle());
        Glide.with(activity)
                .load(book.getCover())
                .apply(new RequestOptions().override(240, 340))
                .centerCrop()
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.error)
                .into(holder.imgCover);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, DetailBookActivity.class).putExtra("previewBook",book));

            }
        });
    }


}
