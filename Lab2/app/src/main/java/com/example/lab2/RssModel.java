package com.example.lab2;

public class RssModel {
    public String mTitle;
    public String mLink;
    public String mPublicationDate;


    public RssModel()
    {

    }

    public RssModel(String title, String link, String pubDate) {
        this.mTitle = title;
        this.mLink = link;
        this.mPublicationDate = pubDate;
    }

    public boolean isFilled()
    {
        return (mTitle != null) && (mLink != null) && (mPublicationDate != null);
    }

    public void clear()
    {
        this.mTitle = null;
        this.mLink = null;
        this.mPublicationDate = null;

    }
}
