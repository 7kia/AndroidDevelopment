package com.example.lab2;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import android.util.Xml;

public class RssManager {
    public List<RssModel> mRssModels;
    private InputStream mInputStream;
    private XmlPullParser mXmlPullParser;

    private String mPathToUrl;
    private String mPathToSaveFeed;
    private static int SCROOL_SPEED = 10;

    public RssManager(
            String pathToUrl,
            String pathToSaveFeed
    )
    {
        this.mPathToUrl = pathToUrl;
        this.mPathToSaveFeed = pathToSaveFeed;
    }

    public void clearFeed() throws IOException
    {
        if ( mRssModels != null){
            mRssModels.clear();
        }
        if ( mInputStream != null){
            mInputStream.close();
            mInputStream = null;
        }
        if ( mXmlPullParser != null){
            mXmlPullParser = null;
        }
    }

    public void loadFeed() throws IOException, XmlPullParserException
    {
        if ( mInputStream == null)
        {
            URL url = new URL(mPathToUrl);
            mInputStream = url.openConnection().getInputStream();
        }
        if ( mRssModels == null){
            mRssModels = getNextNewsFromRssFeed();
        } else {
            mRssModels.addAll(getNextNewsFromRssFeed());
        }
    }


    private List<RssModel> getNextNewsFromRssFeed() throws XmlPullParserException, IOException {

        if ( mXmlPullParser == null) {
            mXmlPullParser = Xml.newPullParser();
            mXmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            mXmlPullParser.setInput( mInputStream, null);
        }

        mXmlPullParser.nextTag();

        List<RssModel> items = new ArrayList<>();
        String title = null;
        String link = null;
        String pubDate = null;
        boolean isItem = false;
        int currentNewId = 0;

        while (currentNewId < SCROOL_SPEED) {
            if ( mXmlPullParser.next() == XmlPullParser.END_DOCUMENT) {
                mInputStream.close();
                return items;
            }

            String name = mXmlPullParser.getName();
            if (name == null)
            {
                continue;
            }

            int eventType = mXmlPullParser.getEventType();
            if (eventType == XmlPullParser.END_TAG) {
                if (name.equalsIgnoreCase("item")) {
                    isItem = false;
                }
                continue;
            }
            if ((eventType == XmlPullParser.START_TAG)
                    && name.equalsIgnoreCase("item")) {
                isItem = true;
                continue;
            }

            String result = "";
            if ( mXmlPullParser.next() == XmlPullParser.TEXT) {
                result = mXmlPullParser.getText();
                mXmlPullParser.nextTag();
            }

            if (name.equalsIgnoreCase("title")) {
                title = result;
            } else if (name.equalsIgnoreCase("link")) {
                link = result;
            } else if (name.equalsIgnoreCase("pubDate")) {
                pubDate = result;
            }

            if ((title != null)
                    && (link != null)
                    && (pubDate != null)
                    ) {
                if (isItem) {
                    RssModel item = new RssModel(title, link, pubDate);
                    items.add(item);
                    currentNewId++;
                }

                title = null;
                link = null;
                pubDate = null;
                isItem = false;
            }
        }
        return items;
    }
}
