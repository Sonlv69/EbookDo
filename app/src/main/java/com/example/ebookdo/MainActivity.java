package com.example.ebookdo;

import static android.view.View.GONE;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ebookdo.adapter.BookPreviewAdapter;
import com.example.ebookdo.custom.CustomLinearLayoutManager;
import com.example.ebookdo.model.BookDetailModel;
import com.example.ebookdo.process.BookData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final String MY_URL = "https://www.gutenberg.org/";
    public static String NEXT_URL = "https://www.gutenberg.org/";
    private RecyclerView recyclerView;
    private BookPreviewAdapter bookPreviewAdapter;
    private TextView tvLoading;
    private ArrayList<BookDetailModel> listBook;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int scrollPosition;
    private FloatingActionButton fabToTopList;
    private FloatingActionButton fabToTopList2;
    private CustomLinearLayoutManager linearLayoutManager;
    private int bookNumber;
    private SearchView searchView;
    private String searchViewText;
    private State screen;
    int test = 0;

    private boolean isLoading = false;


    //trang thai man hinh
    enum State {
        HOME,
        SEARCH
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rcv_book_preview);
        linearLayoutManager = new CustomLinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        listBook = new ArrayList<>();
        tvLoading = findViewById(R.id.tv_loading);
        bookNumber = 0;
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        fabToTopList = findViewById(R.id.fab_to_top);
        fabToTopList2 = findViewById(R.id.fab_to_top2);
        //RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(itemDecoration);
        screen = State.HOME;
        new DownloadTask().execute(MY_URL);

        initializeRefreshListener();
        initScrollListener();
        fabToTopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        fabToTopList2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String s = Integer.toString(listBook.size());
                String s = Integer.toString(test);
                //String s = String.valueOf(searchView.isIconified());
                if (bookPreviewAdapter != null)
                    Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Download HTML bằng AsynTask
    private class DownloadTask extends AsyncTask<String, Void, ArrayList<BookDetailModel>> {

        private static final String TAG = "DownloadTask";

        @Override
        protected ArrayList<BookDetailModel> doInBackground(String... strings) {
            if(screen == State.HOME) {
                listBook.addAll(executeMainContent(strings[0]));
            }
            if(screen == State.SEARCH) {
                listBook.addAll(executeSearchContent(strings[0]));
            }
            return listBook;
        }

        // sau khi get duoc all data vao ham nay de set giao dien
        @Override
        protected void onPostExecute(ArrayList<BookDetailModel> books) {
            super.onPostExecute(books);
            //Setup data recyclerView
            if (bookPreviewAdapter == null) {
                bookPreviewAdapter = new BookPreviewAdapter(MainActivity.this,books);
                tvLoading.setVisibility(GONE);
                recyclerView.setAdapter(bookPreviewAdapter);
            } else if (listBook.size() > 0) {
                if (listBook.get(scrollPosition - 1) == null) {
                    listBook.remove(scrollPosition - 1);
                }
                bookPreviewAdapter.notifyItemRemoved(scrollPosition-1);
                bookPreviewAdapter.notifyDataSetChanged();
                isLoading = false;
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
                document = (Document) Jsoup.connect(url).get();
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
                            //Element getTitle = element.selectFirst("div.top_block > div.title");
//                                book.setBookTitle(element.text());
                            String lastBookNumber = element.attr("href");
                            if (bookNumber == 0)
                                bookNumber = Integer.parseInt(lastBookNumber.replace("/ebooks/","")) - 10;
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

    // ham jsoup lay data search content
    private ArrayList<BookDetailModel> executeSearchContent(String url) {
        Document document = null;
        ArrayList<BookDetailModel> mList = new ArrayList<>();
        try {
            if (bookPreviewAdapter == null) {
                document = (Document) Jsoup.connect(url).get();
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
                            //Element getTitle = element.selectFirst("div.top_block > div.title");
//                                book.setBookTitle(element.text());
                            String lastBookNumber = element.attr("href");
                            if (bookNumber == 0)
                                bookNumber = Integer.parseInt(lastBookNumber.replace("/ebooks/","")) - 10;
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
                // This method gets called when user pull for refresh,
                // You can make your API call here,
                // We are using adding a delay for the moment
                recyclerView.setVisibility(View.INVISIBLE);
                if(!searchView.isIconified()) {
                    searchView.setQuery("", false);
                    searchView.setIconified(true);
                }
                int size = listBook.size();
                listBook.clear();
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
                // hien thi nut ve dau trang
                if(linearLayoutManager.findLastVisibleItemPosition() > 10)
                    fabToTopList.setVisibility(View.VISIBLE);
                else
                    fabToTopList.setVisibility(View.INVISIBLE);
                if (bookPreviewAdapter != null) {
                    if (searchView.isIconified() || (!searchView.isIconified() && searchViewText == null)) { //tai man hinh chu hoac tai man hinh search nhung seachView khong co text
                        if (!isLoading && bookNumber > 1) { // not in loading state
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == listBook.size() - 1) {
                                isLoading = true;
                                //bottom of list!
                                test =2;
                                loadMore();
                            }
                        }
                    } else { // tai man hinh search

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

    // search view
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //bookPreviewAdapter.getFilter().filter(query);
                searchViewText = query;
                searchViewText = processSearchInput(query);
                new DownloadTask().execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //bookPreviewAdapter.getFilter().filter(newText);
                //searchViewText = newText;
                return false;
            }
        });
        return true;
    }

    public String processSearchInput(String input) {
        String result = "";
        String[] separate = input.split(" ");
        int count = 0;
        for (String s : separate) {
            Log.i("test",s);
            Log.i("count",Integer.toString(count));
            count++;
        }
        return result;
    }

    // an back de tat search view khong thoat app
    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}