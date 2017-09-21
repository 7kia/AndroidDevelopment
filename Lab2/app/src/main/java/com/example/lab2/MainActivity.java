package com.example.lab2;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;



public class MainActivity extends AppCompatActivity{
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRssRecycleView;

    private InputStream mInputStream;
    private XmlPullParser mXmlPullParser;

    private List<RssModel> mRssModels;

    private static int SCROOL_SPEED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(
                MainActivity.this,
                R.string.swipe_down,
                Toast.LENGTH_LONG
        ).show();

        mInputStream = null;
        mXmlPullParser = null;

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mRssRecycleView = (RecyclerView) findViewById(R.id.rssRecycleVIew);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRssRecycleView.setLayoutManager( mLinearLayoutManager);
        mRssRecycleView.setAdapter( new MyRecycleViewAdapter(mRssModels) );

        setOnScrollListener();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRssRecycleView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                mRssRecycleView.clearOnScrollListeners();

                new restartRssFeedTask().execute();
            }
        });
    }

    private class restartRssFeedTask extends AsyncTask<Void , Void, Boolean> {
        protected Boolean doInBackground(Void... params) {
            try{
                if ( mRssModels != null){
                    mRssModels.clear();
                }
                if ( mInputStream != null){
                    try {
                        mInputStream.close();
                        mInputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if ( mXmlPullParser != null){
                    mXmlPullParser = null;
                }
                setOnScrollListener();
                Log.d("RefreshListener", "User refreshed rss feed");
                loadData();
                return true;
            } catch (Exception e){
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(
                    MainActivity.this,
                    "Trying to refresh RSS feed",
                    Toast.LENGTH_SHORT
            ).show();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(
                        MainActivity.this,
                        R.string.rss_success,
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        MainActivity.this,
                        R.string.rss_refresh_error,
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    private void loadData() {
        new RssLoader().execute((Void) null);
    }

    private void setOnScrollListener()
    {
        mRssRecycleView.setOnScrollListener(new EndlessRecyclerOnScrollListener( mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                loadData();
            }
        });
    }

    public List<RssModel> getNextNewsFromRssFeed() throws XmlPullParserException, IOException {
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
                Log.d("NewsLoader", "Loaded news count: " + items.size());
                Log.d("NewsLoader", "Reached end of RSS file");
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
            if (eventType == XmlPullParser.START_TAG) {
                if (name.equalsIgnoreCase("item")) {
                    isItem = true;
                    continue;
                }
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
        Log.d("NewsLoader", "Loaded news count: " + items.size());
        return items;
    }


    private class RssLoader extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if ( mInputStream == null)
                {
                    URL url = new URL(getString(R.string.rss_feed_url));
                    mInputStream = url.openConnection().getInputStream();
                }
                if ( mRssModels == null){
                    mRssModels = getNextNewsFromRssFeed();
                } else {
                    mRssModels.addAll(getNextNewsFromRssFeed());
                }
                return true;
            } catch (IOException e) {
                Log.e("MainActivity", "IO error", e);
            } catch (XmlPullParserException e) {
                Log.e("MainActivity", "XML parse error", e);
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                mRssRecycleView.setAdapter(new MyRecycleViewAdapter( mRssModels));
            } else {
                Toast.makeText(MainActivity.this,
                        R.string.check_connection,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
