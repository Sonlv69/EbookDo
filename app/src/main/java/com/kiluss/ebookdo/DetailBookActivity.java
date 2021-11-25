package com.kiluss.ebookdo;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.kiluss.ebookdo.adapter.BookPreviewAdapter;
import com.kiluss.ebookdo.adapter.BookRelativePreviewAdapter;
import com.kiluss.ebookdo.custom.CustomLinearLayoutManager;
import com.kiluss.ebookdo.fragment.DownloadDialogFragment;
import com.kiluss.ebookdo.fragment.HomeFragment;
import com.kiluss.ebookdo.model.BookDetailModel;
import com.kiluss.ebookdo.process.BookData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class DetailBookActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvLang;
    private TextView tvRelease;
    private TextView tvBookNo;
    private TextView tvDownloads;
    private ImageView imgCover;
    private RecyclerView recyclerView;
    private BookRelativePreviewAdapter bookRelativePreviewAdapter;
    private ArrayList<BookDetailModel> listBook;
    private Button btnDownload;
    private ConstraintLayout constraintLayout;
    private TextView tvBookType;
    private BookDetailModel book;
    private LinearLayoutManager linearLayoutManager;
    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView tvEmptyRltBook;
    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_detail_book);

        constraintLayout = findViewById(R.id.constraint_layout_detail_activity);
        constraintLayout.setVisibility(View.INVISIBLE);
        book = (BookDetailModel) getIntent().getSerializableExtra("previewBook");

        tvTitle = findViewById(R.id.tv_book_name_detail);
        tvAuthor = findViewById(R.id.tv_author_detail);
        tvLang = findViewById(R.id.tv_lang_detail);
        tvRelease = findViewById(R.id.tv_release_detail);
        tvBookNo = findViewById(R.id.tv_book_no_detail);
        tvDownloads = findViewById(R.id.tv_downloads_detail);
        tvBookType = findViewById(R.id.tv_book_type_detail);
        tvEmptyRltBook = findViewById(R.id.tv_relative_empty);
        imgCover = findViewById(R.id.img_cover_detail);
        recyclerView = findViewById(R.id.rcv_relative_book);
        btnDownload = findViewById(R.id.download_button);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_relative_book_container);

        shimmerFrameLayout.setVisibility(View.VISIBLE);

        tvTitle.setText(book.getBookTitle());
        tvAuthor.setText(book.getAuthor());
        tvLang.setText(book.getLang());
        tvRelease.setText(book.getRelease());
        tvBookNo.setText(book.getBookNo());
        tvDownloads.setText(book.getDownloads());
        tvEmptyRltBook.setVisibility(View.INVISIBLE);

        linearLayoutManager = new CustomLinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        listBook = new ArrayList<>();

        mToast = Toast.makeText( this  , "" , Toast.LENGTH_SHORT );

        //process when download complete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // get relative book
        new DownloadTask().execute(book.getBookUrl() + "/also/");

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
            tvBookType.setText("no link");
        }
        constraintLayout.setVisibility(View.VISIBLE);

//        if (book.getBookUrl() != null ) {
//            new DetailBookActivity.DownloadTask().execute(book.getBookUrl());
//        }

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (book.getDownloadUrl() != null) {
                    //if OS is Marshmallow or above, handle runtime permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                            //permission is denied, request it
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            //show popup for runtime permission
                            requestPermissions(permissions,PERMISSION_STORAGE_CODE);
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {
                                //permission is denied, request it
                                String[] permissions1 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                //show popup for runtime permission
                                requestPermissions(permissions1,PERMISSION_STORAGE_CODE);
                            }
                        } else {
                            //permission already granted, perform download
                            mToast.setText(book.getBookTitle() + ".epub download started..");
                            mToast.show();
                            onDownloadTask(); //start downloading
                        }
                    } else {
                        //system OS is less than marshmallow, perform download
                        mToast.setText(book.getBookTitle() + ".epub download started..");
                        mToast.show();
                        onDownloadTask();
                    }
                } else {
                    mToast.setText("Invalid link, please try other books!");
                    mToast.show();
                }
            }
        });
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            mToast.setText("Download completed!");
            mToast.show();
        }
    };

    //handle permission result
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted from popup, perform download
                    onDownloadTask();
                    mToast.setText(book.getBookTitle() + ".epub download started..");
                    mToast.show();
                } else {
                    //permission denied from popup, show error message
                    mToast.setText("Permission denied!");
                    mToast.show();
                }
            }
        }
    }

    private void showDownloadDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DownloadDialogFragment alertDialog = DownloadDialogFragment.newInstance(book.getDownloadUrl(),book.getBookTitle());
        alertDialog.show(fm, "fragment_alert");
    }

    private void onDownloadTask() {
        String downloadUrl = book.getDownloadUrl();
        String bookName = book.getBookTitle();

        Log.i("dir",Environment.DIRECTORY_DOWNLOADS);

        //create download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        //allow types of network to download files
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(bookName + ".epub");//set tile in download notification
        request.setDescription(getString(R.string.app_name)); // set description in download notification

        request.setAllowedOverRoaming(false);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(this,"/epub",bookName + ".epub"); // get book title as file name
        //request.setDestinationInExternalPublicDir("Music", bookName + ".epub");

        //get download service and enqueue(hang doi) file
        DownloadManager manager = (DownloadManager)this.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    //Download HTML bằng AsynTask
    private class DownloadTask extends AsyncTask<String, Void, ArrayList<BookDetailModel>> {

        private static final String TAG = "DownloadTask";

        @Override
        protected ArrayList<BookDetailModel> doInBackground(String... strings) {
            listBook.addAll(executeMainContent(strings[0]));
            return listBook;
        }

        // sau khi get duoc all data vao ham nay de set giao dien
        @Override
        protected void onPostExecute(ArrayList<BookDetailModel> books) {
            super.onPostExecute(books);
            //Setup data recyclerView
                if (books.size() > 0) {
                    bookRelativePreviewAdapter = new BookRelativePreviewAdapter(DetailBookActivity.this,books);
                    recyclerView.setAdapter(bookRelativePreviewAdapter);
                    // stop man hinh loading
                    shimmerFrameLayout.setVisibility(GONE);
                    // show list book len
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(GONE);
                    tvEmptyRltBook.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.setVisibility(GONE);
                }
        }
    }

    // ham jsoup lay data main content
    private ArrayList<BookDetailModel> executeMainContent(String url) {
        Document document = null;
        ArrayList<BookDetailModel> mList = new ArrayList<>();
        try {
            document = (Document) Jsoup.connect(url).timeout(30000).get();
            if (document != null) {
                //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6
                Elements sub = document.select(
                        "div.page_content " +
                                "> div.body " +
                                "> div " +
                                "> ul.results " +
                                "> li.booklink");
                if (sub.hasClass("booklink")) {
                    int bookCount =0;
                    for (Element element : sub) {
                        bookCount++;
                        BookDetailModel book;
                        String bookUrl = "https://www.gutenberg.org" + element.getElementsByClass("link").attr("href");
                        BookData bookData = new BookData();
                        book = bookData.getItemBook(bookUrl);

                        //Add to list
                        mList.add(book);
                        if (bookCount == 6) break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mList;
    }

}