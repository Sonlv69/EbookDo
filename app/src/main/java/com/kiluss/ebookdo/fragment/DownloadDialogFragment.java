package com.kiluss.ebookdo.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.kiluss.ebookdo.R;
import com.kiluss.ebookdo.model.BookDetailModel;

public class DownloadDialogFragment extends DialogFragment {
    public DownloadDialogFragment() {
    }

    // tao interface voi mot phuong thuc de truyen lenh cancel den activity
    public interface DownloadDialogListener {
        void onCancelDownload(String inputText);
    }

    public static DownloadDialogFragment newInstance(String downloadUrl, String bookTitle) {
        DownloadDialogFragment frag = new DownloadDialogFragment();
        Bundle args = new Bundle();
        args.putString("url", downloadUrl);
        args.putString("title",bookTitle);
        frag.setArguments(args);
        return frag;
    }

    private ProgressBar mProgressBar;
    private View view;
    private TextView btnCancel, tvTitle;

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        String title = getArguments().getString("url");
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//        alertDialogBuilder.setTitle(title);
//        alertDialogBuilder.setMessage("Are you sure?");
//        alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
//            }
//
//        });
//
//        alertDialogBuilder.create();
//    }

    @Override public View onCreateView(LayoutInflater inflater,
                                       ViewGroup container, Bundle savedInstanceState) {
        String url = getArguments().getString("url");
//        getDialog().setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
//            }
//
//        });
        view = inflater.inflate(R.layout.layout_download_dialog, container);
        mProgressBar = view.findViewById(R.id.download_progress_bar);
        btnCancel = view.findViewById(R.id.dialog_cancel);
        tvTitle = view.findViewById(R.id.dialog_title);
        mProgressBar.setProgress(0);
        tvTitle.setText(url);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        new DownloadTask().execute(url);
        return view;
    }

    //Download file bằng AsynTask
    private class DownloadTask extends AsyncTask<String, Void, BookDetailModel> {

        private static final String TAG = "DownloadTask";

        @Override
        protected BookDetailModel doInBackground(String... strings) {
            mProgressBar = view.findViewById(R.id.download_progress_bar);

            String url = strings[0];
            String bookName = getArguments().getString("title");

            //create download request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            //allow types of network to download files
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle(getString(R.string.app_name));//set tile in download notification
            request.setDescription("Downloading file: " + bookName); // set description in download notification

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"sachebook" + ".epub"); // get book title as file name

            //get download service and enqueue(hang doi) file
            DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            mProgressBar = view.findViewById(R.id.download_progress_bar);
            // setting progress percentage
            mProgressBar.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(BookDetailModel book) {
            super.onPostExecute(book);
            // dismiss the dialog after the file was downloaded
            getDialog().dismiss();


        }
    }
}
