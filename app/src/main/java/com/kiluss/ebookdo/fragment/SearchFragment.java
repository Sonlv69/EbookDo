package com.kiluss.ebookdo.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.kiluss.ebookdo.R;
import com.kiluss.ebookdo.adapter.BookPreviewAdapter;
import com.kiluss.ebookdo.custom.CustomLinearLayoutManager;
import com.kiluss.ebookdo.model.BookDetailModel;
import com.kiluss.ebookdo.process.BookData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class SearchFragment extends Fragment {
    public static String MY_URL = "https://www.gutenberg.org/";
    public static String SEARCH_URL = "https://www.gutenberg.org/";
    private RecyclerView recyclerView;
    private BookPreviewAdapter bookPreviewAdapter;
    private ArrayList<BookDetailModel> listBook;
    private int scrollPosition;
    private FloatingActionButton fabToTopList;
    private CustomLinearLayoutManager linearLayoutManager;
    private EditText searchText;
    private int startIndex;
    private boolean endOfResult = false;
    private ShimmerFrameLayout shimmerFrameLayout;
    private View v;
    int test = 0;

    private boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = v.findViewById(R.id.rcv_search_fragment);
        fabToTopList = v.findViewById(R.id.fab_to_top_search);
        searchText = v.findViewById(R.id.search_view);
        shimmerFrameLayout = v.findViewById(R.id.shimmer_view_search_container);

        shimmerFrameLayout.setVisibility(GONE);

        Log.i("test","search fragment oncreate");

        linearLayoutManager = new CustomLinearLayoutManager(this.requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        listBook = new ArrayList<>();
        //RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(itemDecoration);

        initScrollListener();
        fabToTopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        initScrollListener();
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    endOfResult = false;
                    recyclerView.setVisibility(View.INVISIBLE);
                    listBook.clear();
                    bookPreviewAdapter = null;
                    startIndex = 1;
                    MY_URL =  processSearchInput(searchText.getText().toString()) + startIndex;
                    SEARCH_URL = processSearchInput(searchText.getText().toString());
                    Log.i("result", MY_URL);
                    new SearchFragment.DownloadTask().execute(MY_URL);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        // Inflate the layout for this fragment
        return v;
    }
        public String processSearchInput(String input) {
        String result = "https://www.gutenberg.org/ebooks/search/?query="+ input.replace(" ","+") + "&start_index=";
        return result;
    }

//    // an back de tat search view khong thoat app
//    @Override
//    public void onBackPressed() {
//        if(!searchView.isIconified()) {
//            searchView.setQuery("", false);
//            searchView.setIconified(true);
//            return;
//        }
//        super.onBackPressed();
//    }

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
            if(getActivity() != null) {
                if (bookPreviewAdapter == null) {
                    bookPreviewAdapter = new BookPreviewAdapter(getActivity(),books);
                    recyclerView.setAdapter(bookPreviewAdapter);
                    // stop man hinh loading
                    shimmerFrameLayout = v.findViewById(R.id.shimmer_view_search_container);
                    shimmerFrameLayout.setVisibility(GONE);
                    // show list book len
                    recyclerView.setVisibility(View.VISIBLE);
                } else if (listBook.size() > 0) {
                    if (listBook.get(scrollPosition - 1) == null) {
                        listBook.remove(scrollPosition - 1);
                    }
                    bookPreviewAdapter.notifyItemRemoved(scrollPosition-1);
                    bookPreviewAdapter.notifyDataSetChanged();
                    isLoading = false;
                }
            }
            recyclerView.setVisibility(View.VISIBLE);
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
                if (sub != null) {
                    int i =0;
                    for (Element element : sub) {
                        BookDetailModel book;
                        String bookUrl = "https://www.gutenberg.org" + element.getElementsByClass("link").attr("href");
                        BookData bookData = new BookData();
                        book = bookData.getItemBook(bookUrl);

                        //Add to list
                        mList.add(book);
                        i++;
                        if (i==10) {
                            startIndex += i;
                            MY_URL = SEARCH_URL + startIndex;
                            break;
                        }
                    }
                    if (i<10) {
                        endOfResult = true;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"End of results",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    endOfResult = true;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"End of results",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mList;
    }

    //ham xu ly viec cuon recyclerview
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // hien thi nut ve dau trang
                if(linearLayoutManager.findLastVisibleItemPosition() > 10)
                    fabToTopList.setVisibility(View.VISIBLE);
                else
                    fabToTopList.setVisibility(View.INVISIBLE);
                if (bookPreviewAdapter != null) {
                    if (!isLoading && !endOfResult) { // not in loading state
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == listBook.size() - 1) {
                            isLoading = true;
                            Log.i("size", Integer.toString(listBook.size()));
                            //bottom of list!
                            loadMore();
                        }
                    }
                }
            }
        });
    }

    private void loadMore() {
        listBook.add(null); // kich hoat loading view ben adapter
        bookPreviewAdapter.notifyItemInserted(listBook.size() - 1);

        scrollPosition = listBook.size();//lay vi tri loading view
        new SearchFragment.DownloadTask().execute(MY_URL);
    }
}