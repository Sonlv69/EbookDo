package com.example.ebookdo.model;

import java.io.Serializable;

public class BookPreviewModel implements Serializable {
    private String bookName;
    private String author;
    private String url;
    private String cover;
    private String description;
    private String rating;

    public BookPreviewModel(){
    }
    public BookPreviewModel(String bookName, String author, String url, String cover, String description, String rating) {
        this.bookName = bookName;
        this.author = author;
        this.url = url;
        this.cover = cover;
        this.description = description;
        this.rating = rating;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
