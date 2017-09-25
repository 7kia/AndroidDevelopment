package com.example.lab2;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;

import java.net.URL;

import org.xmlpull.v1.XmlPullParserException;

import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;



public class MainActivity extends AppCompatActivity{
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRssRecycleView;

    private RssManager mRssManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(
                MainActivity.this,
                R.string.swipe_down,
                Toast.LENGTH_LONG
        ).show();

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mRssRecycleView = (RecyclerView) findViewById(R.id.rssRecycleVIew);

        mRssManager = new RssManager(
                this.getString(R.string.rss_feed_url),
                this.getString(R.string.feedFileName)
        );

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRssRecycleView.setLayoutManager( mLinearLayoutManager);
        mRssRecycleView.setAdapter( new MyRecycleViewAdapter(mRssManager.mRssModels) );


        //startService(new Intent(this, RssService.class));
        setOnScrollListener();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRssRecycleView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                mRssRecycleView.clearOnScrollListeners();

                new RestartRssFeedTask().execute();
            }
        });
    }

    private class RestartRssFeedTask extends AsyncTask<Void , Void, Boolean> {
        protected Boolean doInBackground(Void... params) {
            try{
                mRssManager.clearFeed();
                setOnScrollListener();
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

    private class RssLoader extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                mRssManager.loadFeed();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
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
                mRssRecycleView.setAdapter(new MyRecycleViewAdapter(mRssManager.mRssModels));
            } else {
                Toast.makeText(MainActivity.this,
                        R.string.check_connection,
                        Toast.LENGTH_LONG).show();
            }
        }
    }


}
