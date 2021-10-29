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
        constraintLayout.setVisibility(View.VISIBLE);
        if (book.getDownloadUrl() != null ) {
            new DetailBookActivity.DownloadTask().execute(book.getDownloadUrl());
        }
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
                    Element element = document.selectFirst(
                                            "div#dle-content " +
                                            "> div.fullnews " +
                                            "> div.movie");
                    if (element != null) {
                        Element getTitle = element.selectFirst("div.top_block > div.titleblock");
                        Element authorSubject = element.getElementsByClass("alt_name").first();
                        Element getGenre = element.selectFirst("div.after > div.info > ul > li.fontt");
                        Element getPub = element.selectFirst("div.after > div.info > ul").getElementsByTag("li").get(2);
                        Element getPages = element.selectFirst("div.after > div.info > ul").getElementsByTag("li").get(3);
                        Element describe = element.getElementsByClass("descr").first();
                        Element download = element.getElementsByClass("download_table").first();
                        //Parse to model
                        if (getTitle != null) { //ten sach
                            Element nameSubject = getTitle.getElementsByTag("h1").first();
                            if (nameSubject != null) {
                                String name = nameSubject.text();
                                book.setBookTitle(name);
                            }
                        }
                        if (authorSubject != null) { // tac gia
                            String au = authorSubject.text().replace("by ", "");
                            book.setAuthor(au);
                        }
                        if (getGenre != null) { // the loai
                            Element genreSubject = getGenre.getElementsByTag("a").first();
                            if ( genreSubject != null) {
                                book.setLang(genreSubject.text());
                            }
                        }
                        if (getPub != null) { //ngay xuat ban
                            Element pubSubject = getPub.getElementsByTag("span").get(0);
                            if(pubSubject != null) {
                                book.setRelease(pubSubject.text());
                            }
                        }
                        if (getPages != null) { // so trang
                            Element pagesSubject = getPages.getElementsByClass("fontt").first();
                            if (pagesSubject != null){
                                String str = pagesSubject.text();
                                if(str.contains("Number of pages: ~ "))
                                    book.setBookNo(str.replace("Number of pages: ~ ", ""));
                                else
                                    book.setBookNo("unknown");
                            }
                        }
                        if (describe != null) { // mo ta
                            // giu nguyen xuong dong khi lay text
                            String words = describe.html();
                            String temp = words.replace("<br>", "$$$");
                            Document doc = Jsoup.parse(temp);
                            String text = doc.body().text().replace("$$$", "\n");
                            book.setDescription(text.trim()); // set text cho des
                        }
                        if (download != null) { // download link

                            Elements links = download.select("tbody.download_table_body > tr");
                            if (links != null) {
                                int c = 0;
                                for (Element l : links) {
                                    Element downloadName = l.getElementsByClass("donwload_name").first();
                                    if (downloadName != null) {
                                        if (downloadName.text().contains(".epub")) {
                                            Element downloadLink = l.getElementsByClass("donwload_link").first()
                                                    .getElementsByTag("a").first();
                                            if (downloadLink != null) {
                                                book.setDownloadUrl(downloadLink.attr("href"));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
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
            tvAuthor.setText(book.getAuthor());
            tvLang.setText(book.getLang());
            tvRelease.setText(book.getRelease());
            tvBookNo.setText(book.getBookNo());
            tvDescription.setText(book.getDescription());
            constraintLayout.setVisibility(View.VISIBLE);
            if (book.getDownloadUrl() != null) {
                String url = book.getDownloadUrl();
                if (url.contains(".epub")) {
                    tvBookType.setText("epub");
                } else if (url.contains(".pdf")) {
                    tvBookType.setText("pdf");
                } else if (url.contains(".mobi")) {
                    tvBookType.setText("mobi");
                }
            } else {
                tvBookType.setText("unknown");
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
    }
}