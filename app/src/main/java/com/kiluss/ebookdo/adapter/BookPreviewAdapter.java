package com.kiluss.ebookdo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kiluss.ebookdo.R;
import com.kiluss.ebookdo.model.BookDetailModel;
import com.kiluss.ebookdo.DetailBookActivity;

import java.util.ArrayList;

public class BookPreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Activity activity;
    private ArrayList<BookDetailModel> bookList;
    private ArrayList<BookDetailModel> bookListOld;
    private final int VIEW_TYPE_BOOK = 0;
    private final int VIEW_TYPE_LOADING = 1; // loading view

    public BookPreviewAdapter(Activity activity, ArrayList<BookDetailModel> bookList) {
        this.activity = activity;
        this.bookList = bookList;
        this.bookListOld = bookList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
       // tao layout view holder tuy theo loai view item
        if (viewType == VIEW_TYPE_BOOK) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book,parent,false);
            return new BookPreviewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        // set data cho view tuy theo loai layout
        if (viewHolder instanceof BookPreviewHolder) {

            populateItemRows((BookPreviewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    // kiem tra view la loai nao(loading view hay book view)
    @Override
    public int getItemViewType(int position) {
        return bookList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_BOOK;
    }


    class BookPreviewHolder extends RecyclerView.ViewHolder{

        private ImageView imgCover;
        private TextView tvName;
        private TextView tvAuthor;
        private TextView tvDownloads;

        public BookPreviewHolder(View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.img_Cover);
            tvName = itemView.findViewById(R.id.tv_Book_Name);
            tvAuthor = itemView.findViewById(R.id.tv_Author);
            tvDownloads = itemView.findViewById(R.id.tv_Downloads);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(BookPreviewHolder holder, int position) {

        final BookDetailModel book = bookList.get(position);
        holder.tvName.setText(book.getBookTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvDownloads.setText(book.getDownloads() + " downloads");
        Glide.with(activity)
                .load(book.getCover())
                .apply(new RequestOptions().override(580, 900))
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

    @Override
    public Filter getFilter() { //ham tim kiem
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if (strSearch.isEmpty()){
                    bookList = bookListOld;
                }else {
                    ArrayList<BookDetailModel> list = new ArrayList<>();
                    for (BookDetailModel book : bookListOld){
                        if (book.getBookTitle().toLowerCase().contains(' ' + strSearch.toLowerCase())
                                || book.getBookTitle().toLowerCase().startsWith(strSearch.toLowerCase())) {
                            list.add(book);
                        }
                    }
                    bookList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = bookList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                bookList= (ArrayList<BookDetailModel>) filterResults.values;
                notifyDataSetChanged();;
            }
        };
    }

}
