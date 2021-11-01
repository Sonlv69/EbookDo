package com.example.ebookdo.fragment;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebookdo.R;
import com.example.ebookdo.adapter.BookFilesAdapter;
import com.example.ebookdo.adapter.BookPreviewAdapter;
import com.example.ebookdo.custom.CustomLinearLayoutManager;

import java.io.File;
import java.util.ArrayList;

public class DownloadFragment extends Fragment {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private Toast mToast;
    private RecyclerView recyclerView;
    private CustomLinearLayoutManager linearLayoutManager;
    private BookFilesAdapter bookFilesAdapter;
    private ArrayList<String> books;



    public DownloadFragment() {
        // Required empty public constructor
    }

//    public static DownloadFragment newInstance(String param1, String param2) {
//        DownloadFragment fragment = new DownloadFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_download, container, false);
        // Inflate the layout for this fragment
        recyclerView = v.findViewById(R.id.rcv_book_files);
        linearLayoutManager = new CustomLinearLayoutManager(this.requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        mToast = Toast.makeText(getContext()," ",Toast.LENGTH_SHORT);
//        String path = Environment.getExternalStoragePublicDirectory("/ebook").toString();

        //if OS is Marshmallow or above, handle runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
            }else{
                //Do your work
                readEbookFiles();
            }
        } else {
            //system OS is less than marshmallow, perform download
//            mToast.setText(book.getBookTitle() + ".epub download started..");
//            mToast.show();
            readEbookFiles();
        }
        //set adapter
        bookFilesAdapter = new BookFilesAdapter(getActivity(),books);
        recyclerView.setAdapter(bookFilesAdapter);

        return v;
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted from popup, perform download
                    readEbookFiles();
                } else {
                    //permission denied from popup, show error message
                    mToast.setText("Permission denied!");
                    mToast.show();
                }
            }
        }
    }


    private void readEbookFiles() {
        ContextWrapper cw = new ContextWrapper(getContext());
        String path =cw.getExternalFilesDir("/ebook").toString();
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        String canRead = String.valueOf(directory.canRead());
        books = new ArrayList<String>();
        if(directory.canRead() && files!=null) {
            Log.d("Files", "Size: " + files.length);
            for(File file: files) {
                Log.d("FILE",file.getName());
                books.add(file.getName());
            }
        }
        else
            Log.d("Null?", "it is null");
            Log.d("Can read?", canRead);

    }
}