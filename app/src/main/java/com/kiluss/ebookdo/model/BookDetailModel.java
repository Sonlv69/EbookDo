package com.kiluss.ebookdo.model;

import java.io.Serializable;

public class BookDetailModel implements Serializable {

    private String bookTitle;
    private String author;
    private String downloadUrl;
    private String bookUrl;
    private String cover;
    private String description;
    private String downloads;
    private String lang;
    private String release;
    private String bookNo;

    public BookDetailModel(){
    }

    public BookDetailModel(String bookTitle, String author, String downloadUrl, String bookUrl, String cover, String description, String downloads, String lang, String release, String bookNo) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.downloadUrl = downloadUrl;
        this.bookUrl = bookUrl;
        this.cover = cover;
        this.description = description;
        this.downloads = downloads;
        this.lang = lang;
        this.release = release;
        this.bookNo = bookNo;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }
}