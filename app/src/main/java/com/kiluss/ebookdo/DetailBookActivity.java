package com.kiluss.ebookdo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kiluss.ebookdo.fragment.DownloadDialogFragment;
import com.kiluss.ebookdo.model.BookDetailModel;

public class DetailBookActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
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
    private BookDetailModel book;
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

        mToast = Toast.makeText( this  , "" , Toast.LENGTH_SHORT );

        //process when download complete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

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


}