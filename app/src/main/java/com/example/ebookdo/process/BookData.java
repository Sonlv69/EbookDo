package com.example.ebookdo.process;

import com.example.ebookdo.model.BookDetailModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

// class lay data tu trang chinh cua 1 ebook, can truyen url cua trang nay vao ham getItemBook(url), return BookDetailModel
public class BookData {
    private BookDetailModel book;
    private String bookUrl;

    public BookData() {
    }

    public BookData(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public BookDetailModel getItemBook(String bookLink) {
        book = new BookDetailModel();
        Document document = null;
        try {
            document = (Document) Jsoup.connect(bookLink).get();
            if (document != null) {
                //Lấy  html có thẻ như sau: div#latest-news > div.row > div.col-md-6 hoặc chỉ cần dùng  div.col-md-6
                Element element = document.selectFirst(
                                "div.page_content " +
                                "> div.page-body " +
                                "> div#tabs-wrapper " +
                                "> div#tabs");
                if (element != null) {
                    Element getInfo = element.selectFirst("div#bibrec > div > table.bibrec > tbody");
                    Elements getDownloadUrl = element.select("div#download > div > table.files > tbody > tr");
                    if (getInfo != null) {
                        Element getAuthor = element.selectFirst("[itemprop=creator]");
                        Element getTitle = element.selectFirst("[itemprop=headline]");
                        Element getLang = element.selectFirst("[property=dcterms:language]");
                        Element getDate = element.selectFirst("[itemprop=datePublished]");
                        Element getDownloads = element.selectFirst("[itemprop=interactionCount]");
                        //Parse to model

                        if (getAuthor != null) { //tac gia
                            book.setAuthor(getAuthor.text());
                        }
                        if (getTitle != null) { // ten sach
                            String au = getTitle.text();
                            book.setBookTitle(au);
                        }
                        if (getLang != null) { // ngon ngu
                            book.setLang(getLang.text().replace("Language",""));
                        }
                        book.setBookNo(bookLink.replace("https://www.gutenberg.org/ebooks/",""));
                        if (getDate != null) {
                            book.setRelease(getDate.text());
                        }
                        if (getDownloads != null) { //so luong download
                            book.setDownloads("Downloads: " + getDownloads.text().replace(" downloads in the last 30 days.",""));
                        }
                    }
                    if (getDownloadUrl != null) {
                        for (Element row : getDownloadUrl) {
                            if (row.attr("about").contains("epub.images")) {
                                book.setDownloadUrl(row.attr("about"));
                                break;
                            } else if (row.attr("about").contains("epub.noimages")) {
                                book.setDownloadUrl(row.attr("about"));
                                break;
                            } else {
                                book.setDownloadUrl(null);
                            }
                        }
                    }

                }
                // get cover
                Element getCover = document.selectFirst(
                                "div.page_content " +
                                "> div.page-body " +
                                "> div#cover-social-wrapper " +
                                "> div#cover " +
                                "> img.cover-art");
                if(getCover != null) {
                    book.setCover(getCover.attr("src"));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }
}
