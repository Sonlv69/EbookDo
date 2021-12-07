package com.kiluss.ebookdo.viewmodel;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.kiluss.ebookdo.adapter.BookPreviewAdapter;
import com.kiluss.ebookdo.model.BookDetailModel;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragmentViewModel extends ViewModel {
    private int bookNumber;
    private ArrayList<BookDetailModel> listBook;
    private boolean isLoading = false;
    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setListBook(ArrayList<BookDetailModel> listBook) {
        //this.listBook.clear();
        this.listBook = listBook;
        this.listBook.removeIf(Objects::isNull);
    }

    public ArrayList<BookDetailModel> getBookList() {
        if (listBook == null) {
            listBook = new ArrayList<>();
        }
        return listBook;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
