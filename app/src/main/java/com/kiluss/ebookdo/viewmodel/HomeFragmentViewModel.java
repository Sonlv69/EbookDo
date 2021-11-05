package com.kiluss.ebookdo.viewmodel;

import androidx.lifecycle.ViewModel;

import com.kiluss.ebookdo.adapter.BookPreviewAdapter;
import com.kiluss.ebookdo.model.BookDetailModel;

import java.util.ArrayList;

public class HomeFragmentViewModel extends ViewModel {
    private int bookNumber;

    private ArrayList<BookDetailModel> listBook;

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public void setListBook(ArrayList<BookDetailModel> listBook) {
        this.listBook = listBook;
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
}
