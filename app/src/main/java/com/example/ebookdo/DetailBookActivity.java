package com.example.ebookdo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ebookdo.model.BookDetailModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DetailBookActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvLang;
    private TextView tvRelease;
    private TextView tvBookNo;
    private TextView tvDownloads;
    private ImageView imgCover;
    private TextView tvDescription;
    private Button btnDownload;
    private ConstraintLayout constraintLayout;
    private TextView tvBookType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_book);

        constraintLayout = findViewById(R.id.constraint_layout_detail_activity);
        constraintLayout.setVisibility(View.INVISIBLE);
        BookDetailModel book = (BookDetailModel) getIntent().getSerializableExtra("previewBook");

        tvTitle = findViewById(R.id.tv_book_name_detail);
        tvAuthor = findViewById(R.id.tv_author_detail);
        tvLang = findViewById(R.id.tv_lang_detail);
        tvRelease = findViewById(R.id.tv_release_detail);
        tvBookNo = findViewById(R.id.tv_book_no_detail);
        tvDownloads = findViewById(R.id.tv_downloads_detail);
        tvBookType = findViewById(R.id.tv_book_type_detail);
        imgCover = findViewById(R.id.img_cover_detail);
        tvDescription = findViewById(R.id.tv_description_detail);
        btnDownload = findViewById(R.id.download_button);

        tvTitle.setText(book.getBookTitle());
        tvAuthor.setText(book.getAuthor());
        tvLang.setText(book.getLang());
        tvRelease.setText(book.getRelease());
        tvBookNo.setText(book.getBookNo());
        tvDescription.setText(book.getDescription());
        tvDownloads.setText(book.getDownloads());
        //set cover
        if (book.getCover() != null) {
            Glide.with(this)
                    .load(book.getCover())
                    .apply(new RequestOptions().override(520, 800))
                    .centerCrop()
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.error)
                    .into(imgCover);
        }
        if (book.getDownloadUrl() != null) {
            String url = book.getDownloadUrl();
            if (url.contains("epub.images")) {
                tvBookType.setText("epub(with images)");
            } else if (url.contains("epub.noimages")) {
                tvBookType.setText("epub(no images)");
            }
        } else {
            tvBookType.setText("wrong");
        }
        constraintLayout.setVisibility(View.VISIBLE);

        if (book.getBookUrl() != null ) {
            new DetailBookActivity.DownloadTask().execute(book.getBookUrl());
        }

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (book.getDownloadUrl() != null) {
                    String url = book.getDownloadUrl();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(),"Invalid link, please try other books!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Download HTML bằng AsynTask
    private class DownloadTask extends AsyncTask<String, Void, BookDetailModel> {

        private static final String TAG = "DownloadTask";

        @Override
        protected BookDetailModel doInBackground(String... strings) {
            Document document = null;
            BookDetailModel book = new BookDetailModel();
            try {
                document = (Document) Jsoup.connect(strings[0]).get();
                if (document != null) {
                    //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6


                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return book;
        }

        @Override
        protected void onPostExecute(BookDetailModel book) {
            super.onPostExecute(book);
            //Setup data recyclerView
            constraintLayout.setVisibility(View.VISIBLE);
        }
    }
}