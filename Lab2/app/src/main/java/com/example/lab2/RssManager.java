package com.example.lab2;

import android.content.Context;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.nio.channels.FileChannel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class RssManager {
    private static final int IS_IMPORTANCE_NUMBER = 4;
    private static final int IS_COMPLETE_NUMBER = 5;

    public List<RssModel> mRssModels;
    private InputStream mInputStream;
    private XmlPullParser mXmlPullParser;

    public String mPathToUrl;
    public String mPathToSaveFeed;
    private static int SCROOL_SPEED = 10;

    public RssManager(
            String pathToUrl,
            String pathToSaveFeed
    )
    {
        this.mPathToSaveFeed = pathToSaveFeed;
        this.mPathToUrl = pathToUrl;

        try {
            new File(mPathToSaveFeed).createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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


    public List<RssModel> getNextNewsFromRssFeed() throws XmlPullParserException, IOException {

        if ( mXmlPullParser == null) {
            mXmlPullParser = Xml.newPullParser();
            mXmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            mXmlPullParser.setInput( mInputStream, null);
        }

        mXmlPullParser.nextTag();

        List<RssModel> items = new ArrayList<RssModel>();
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


    public boolean needUpdate() throws XmlPullParserException, IOException {
        URL url = new URL(mPathToUrl);
        mInputStream = url.openConnection().getInputStream();

        mXmlPullParser = Xml.newPullParser();
        mXmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        mXmlPullParser.setInput( mInputStream, null);

        mXmlPullParser.nextTag();


        String title = null;
        String link = null;
        String pubDate = null;
        boolean isItem = false;

        String name = mXmlPullParser.getName();
        while (mXmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
//            if ( mXmlPullParser.next() == XmlPullParser.END_DOCUMENT) {
//                mInputStream.close();
//                return items;
//            }

            name = mXmlPullParser.getName();
            if (name == null) {
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
            if (mXmlPullParser.next() == XmlPullParser.TEXT) {
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

                    if (mRssModels.size() <= 0) {
                        return false;
                    }
                    return mRssModels.get(0) != item;
                }
            }

        }
        return false;

    }


    public void readRssModelsFromFile() throws XmlPullParserException, IOException {
        try{
            if ( mRssModels == null){
                mRssModels = getNextNewsFromRssFeed();
            } else {
                mRssModels.addAll(getNextNewsFromRssFeed());
            }

        } catch(FileNotFoundException ex){
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private static void copyFile(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }finally{
            sourceChannel.close();
            destChannel.close();
        }
    }

    public void saveFeedOnDevice(){
        try{
            copyFile(new File(mPathToUrl), new File(mPathToSaveFeed));
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public boolean compareFiles(){
        File newFeed = new File(mPathToUrl);
        File oldFeed = new File(mPathToSaveFeed);
        return newFeed.equals(oldFeed);
    }


}
