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

    private static int SCROLL_SPEED = 10;
    private boolean mFoundItem = false;
    private RssModel mLastReadModel;
    public RssManager(String pathToUrl)
    {
        this.mPathToUrl = pathToUrl;
        mLastReadModel = new RssModel();
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
        fillListRssModels(items);
        return items;
    }

    private void fillListRssModels(List<RssModel> items) throws XmlPullParserException, IOException
    {
        int currentNewId = 0;
        mFoundItem = false;
        while (currentNewId < SCROLL_SPEED) {
            if ( mXmlPullParser.next() == XmlPullParser.END_DOCUMENT) {
                mInputStream.close();
                return;
            }

            String name = mXmlPullParser.getName();
            if (name == null)
            {
                continue;
            }

            if(checkEventType(name))
            {
                continue;
            }
            searchModelData(name);

            if (mLastReadModel.isFilled() && mFoundItem) {
                RssModel item = new RssModel(
                        mLastReadModel.mTitle,
                        mLastReadModel.mLink,
                        mLastReadModel.mPublicationDate
                );
                items.add(item);
                currentNewId++;
                mFoundItem = false;
            }
        }
    }

    private boolean checkEventType(String name) throws XmlPullParserException
    {
        int eventType = mXmlPullParser.getEventType();
        if (eventType == XmlPullParser.END_TAG) {
            if (name.equalsIgnoreCase("item")) {
                mFoundItem = false;
            }
            return true;
        }
        if ((eventType == XmlPullParser.START_TAG)
                && name.equalsIgnoreCase("item")) {
            mFoundItem = true;
            return true;
        }
        return false;
    }

    private void searchModelData(String name) throws XmlPullParserException, IOException
    {
        String result = "";
        if ( mXmlPullParser.next() == XmlPullParser.TEXT) {
            result = mXmlPullParser.getText();
            mXmlPullParser.nextTag();
        }

        if (name.equalsIgnoreCase("title")) {
            mLastReadModel.mTitle = result;
        } else if (name.equalsIgnoreCase("link")) {
            mLastReadModel.mLink = result;
        } else if (name.equalsIgnoreCase("pubDate")) {
            mLastReadModel.mPublicationDate = result;
        }
    }

}
