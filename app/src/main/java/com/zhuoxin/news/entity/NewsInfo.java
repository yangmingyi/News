package com.zhuoxin.news.entity;

/**
 * Created by Administrator on 2016/12/1.
 */

public class NewsInfo {
    String url;
    String imageURL;
    String largeImageURL;
    String title;
    String type;

    public NewsInfo(String url, String imageURL, String largeImageURL, String title, String type) {
        this.url = url;
        this.imageURL = imageURL;
        this.largeImageURL = largeImageURL;
        this.title = title;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLargeImageURL() {
        return largeImageURL;
    }

    public void setLargeImageURL(String largeImageURL) {
        this.largeImageURL = largeImageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
