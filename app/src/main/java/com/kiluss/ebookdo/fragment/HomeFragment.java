package com.kiluss.ebookdo.fragment;

import static android.view.View.GONE;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiluss.ebookdo.R;
import com.kiluss.ebookdo.adapter.BookPreviewAdapter;
import com.kiluss.ebookdo.custom.CustomLinearLayoutManager;
import com.kiluss.ebookdo.model.BookDetailModel;
import com.kiluss.ebookdo.process.BookData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kiluss.ebookdo.viewmodel.HomeFragmentViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public static final String MY_URL = "https://www.gutenberg.org/";
    public static String NEXT_URL = "https://www.gutenberg.org/";
    private RecyclerView recyclerView;
    private BookPreviewAdapter bookPreviewAdapter;
    private ArrayList<BookDetailModel> listBook;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int scrollPosition;
    private FloatingActionButton fabToTopList;
    private CustomLinearLayoutManager linearLayoutManager;
    private ShimmerFrameLayout shimmerFrameLayout;
    private int bookNumber;
    private HomeFragmentViewModel homeFragmentViewModel;
    private View v;
    int test = 0;


    private boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        v = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = v.findViewById(R.id.rcv_book_preview);
        linearLayoutManager = new CustomLinearLayoutManager(this.requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        Log.i("create","created");
        swipeRefreshLayout = v.findViewById(R.id.swipe_layout);
        fabToTopList = v.findViewById(R.id.fab_to_top_home);
        shimmerFrameLayout = v.findViewById(R.id.shimmer_view_home_container);

        homeFragmentViewModel = new ViewModelProvider(getActivity()).get(HomeFragmentViewModel.class);

        //((ShimmerFrameLayout) container).startShimmer(); // If auto-start is set to false
        listBook = new ArrayList<>();
        listBook = homeFragmentViewModel.getBookList();
        bookNumber = homeFragmentViewModel.getBookNumber();

        initializeRefreshListener();
        initScrollListener();

        //RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(itemDecoration);
        if (listBook.size() > 0) {
            bookPreviewAdapter = new BookPreviewAdapter(getActivity(), listBook);
            recyclerView.setAdapter(bookPreviewAdapter);
            shimmerFrameLayout.setVisibility(GONE);
        } else {
            new DownloadTask().execute(MY_URL);
        }
        Log.i("booknumber", String.valueOf(bookNumber));
        Log.i("size", String.valueOf(listBook.size()));
        fabToTopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        // Inflate the layout for this fragment
        return v;
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
        protected void onPostExecute (ArrayList<BookDetailModel> books) {
            super.onPostExecute(books);
            //Setup data recyclerView
            if (getActivity() != null) {
                if (bookPreviewAdapter == null) {
                    bookPreviewAdapter = new BookPreviewAdapter(getActivity(), books);
                    recyclerView.setAdapter(bookPreviewAdapter);
                    shimmerFrameLayout = v.findViewById(R.id.shimmer_view_home_container);
                    shimmerFrameLayout.setVisibility(GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else if (listBook.size() > 0) {
                    if (listBook.get(scrollPosition - 1) == null) {
                        listBook.remove(scrollPosition - 1);
                    }
                    bookPreviewAdapter.notifyItemRemoved(scrollPosition - 1);
                    bookPreviewAdapter.notifyDataSetChanged();
                    isLoading = false;
                }
            }
            swipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // ham jsoup lay data main content
    private ArrayList<BookDetailModel> executeMainContent(String url) {
        Document document = null;
        ArrayList<BookDetailModel> mList = new ArrayList<>();
        try {
            if (bookPreviewAdapter == null) {
                document = (Document) Jsoup.connect(url).timeout(60000).get();
                if (document != null) {
                    //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6
                    Elements sub = document.select(
                            "div.page_content " +
                                    "> div.library " +
                                    "> div.box_shadow " +
                                    "> div.lib.latest.no-select " +
                                    "> a");
                    if (sub != null) {
                        for (Element element : sub) {
                            BookDetailModel book;
                            String lastBookNumber = element.attr("href");
                            if (bookNumber == 0) {
                                bookNumber = Integer.parseInt(lastBookNumber.replace("/ebooks/","")) - 10;
                                homeFragmentViewModel.setBookNumber(bookNumber);
                            }
                            String bookUrl = "https://www.gutenberg.org" + lastBookNumber;
                            BookData bookData = new BookData();
                            book = bookData.getItemBook(bookUrl);

                            //Add to list
                            mList.add(book);
                        }
                    }

                }
            } else {
                String preUrl = "https://www.gutenberg.org/ebooks/";
                int pos;
                for (pos = bookNumber; pos > bookNumber - 10; pos--) {
                    BookDetailModel book;
                    String bookUrl = preUrl + pos;
                    BookData bookData = new BookData();
                    book = bookData.getItemBook(bookUrl);
                    mList.add(book);
                    if (pos == 1) break;
                }
                bookNumber = pos;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return mList;
    }

    //swipe on top to refresh list
    void initializeRefreshListener() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.INVISIBLE);
                shimmerFrameLayout = v.findViewById(R.id.shimmer_view_home_container);
                //container.startShimmer(); // stop animation
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                listBook.clear();
                homeFragmentViewModel.setListBook(listBook);
                bookNumber = 0;
                homeFragmentViewModel.setBookNumber(bookNumber);
                //bookPreviewAdapter.notifyItemRangeRemoved(0,size-1);
                bookPreviewAdapter = null;
                new DownloadTask().execute(MY_URL);
            }
        });
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
                Log.i("pos", String.valueOf(linearLayoutManager.findLastCompletelyVisibleItemPosition()));
                // hien thi nut ve dau trang
                if(linearLayoutManager.findLastVisibleItemPosition() > 10)
                    fabToTopList.setVisibility(View.VISIBLE);
                else
                    fabToTopList.setVisibility(View.INVISIBLE);
                if (bookPreviewAdapter != null) {
                    if (!isLoading && bookNumber > 1) { // not in loading state
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == listBook.size() - 1) {
                            isLoading = true;
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
        new DownloadTask().execute(NEXT_URL);
    }


}