package com.kiluss.ebookdo.adapter;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

import static java.net.URLConnection.guessContentTypeFromName;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.kiluss.ebookdo.BuildConfig;
import com.kiluss.ebookdo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class BookFilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity activity;
    private ArrayList<File> files;

    public BookFilesAdapter(Activity activity, ArrayList<File> files) {
        this.activity = activity;
        this.files = files;
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
        return files == null ? 0 : files.size();
    }


    class BookPreviewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;

        public BookPreviewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_book_file);
        }
    }
    

    private void populateItemRows(BookFilesAdapter.BookPreviewHolder holder, int position) {

        File file = files.get(position);
        holder.tvName.setText(file.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_file(file);
            }
        });
    }

    public void open_file(File file) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType =
                myMime.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getName()));
        if(android.os.Build.VERSION.SDK_INT >=24) {
            Uri fileURI = FileProvider.getUriForFile(Objects.requireNonNull(activity.getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            intent.setDataAndType(fileURI, mimeType);

        }else {
            intent.setDataAndType(Uri.fromFile(file), mimeType);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            activity.getApplicationContext().startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(activity.getApplicationContext(), "No Application found to open this type of file.", Toast.LENGTH_LONG).show();

        }
    }

}
