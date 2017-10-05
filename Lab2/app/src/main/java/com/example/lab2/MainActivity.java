package com.example.lab2;

import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;

import org.xmlpull.v1.XmlPullParserException;

import android.widget.Toast;

import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRssRecycleView;

    private RssManager mRssManager;

    private Handler mRssUpdateHandler;
    private Intent mService;

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
                this.getString(R.string.rss_feed_url)
        );

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRssRecycleView.setLayoutManager( mLinearLayoutManager);
        mRssRecycleView.setAdapter( new MyRecycleViewAdapter(mRssManager.mRssModels) );

        setOnScrollListener();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRssRecycleView();
            }
        });

        setupService();
        setUpdateEvent();
        refreshRssRecycleView();
    }

    private void refreshRssRecycleView()
    {
        mRssRecycleView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRssRecycleView.clearOnScrollListeners();

        new RestartRssFeedTask().execute();
    }

    private void setupService()
    {
        try {
            mService = new Intent(this, RssService.class);
            mService.putExtra("pubData", "0");
            startService(mService);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            System.out.println("setupService throw Exception");
        }
    }

    public void setUpdateEvent()
    {
        mRssUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                mRssRecycleView.setLayoutManager( mLinearLayoutManager);
                mRssRecycleView.setAdapter( new MyRecycleViewAdapter(mRssManager.mRssModels) );
                mRssRecycleView.getAdapter().notifyDataSetChanged();

                Toast.makeText(MainActivity.this,
                        R.string.feed_update,
                        Toast.LENGTH_LONG).show();
            }
        };
        Timer timer = new Timer("timer");
        timer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    updateNews();
                }
            },
            1000,
            10000
        );
    }

    public void  updateNews()
    {
        mRssUpdateHandler.sendEmptyMessage(0);
    }

    private class RestartRssFeedTask extends AsyncTask<Void , Void, Boolean> {
        protected Boolean doInBackground(Void... params) {
            try{
                mRssManager.clearFeed();
                setOnScrollListener();
                loadData();
                return true;
            } catch (Exception e){
                System.out.println("RestartRssFeedTask throw IOException");
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

    private void updateServiceData()
    {
        if(mRssManager.mRssModels == null)
        {
            mService.putExtra("pubData", "0");
        }
        else if(mRssManager.mRssModels.size() <= 0)
        {
            mService.putExtra("pubData", "0");
        }
        else
        {
            mService.putExtra(
                    "pubData",
                    mRssManager
                            .mRssModels.get(mRssManager.mRssModels.size() - 1)
                            .mPublicationDate
            );
        }
    }

    private class RssLoader extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                mRssManager.loadFeed();
                updateServiceData();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("RssLoader throw IOException");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                System.out.println("RssLoader throw XmlPullParserException");
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
