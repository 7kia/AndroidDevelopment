package com.example.lab2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyRecycleViewAdapter extends RecyclerView.Adapter<RssModelViewHolder> {
    private List<RssModel> mRssModels;

    public MyRecycleViewAdapter(List<RssModel> rssModels) {
        this.mRssModels = rssModels;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RssModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_rss_feed,
                        parent,
                        false
                );
        RssModelViewHolder holder = new RssModelViewHolder(v);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RssModelViewHolder holder, int position) {
        final RssModel rssModel = mRssModels.get(position);
        ((TextView)holder.mRssItemView.findViewById(R.id.titleText)).setText(rssModel.mTitle);
        ((TextView)holder.mRssItemView.findViewById(R.id.pubDate)).setText(rssModel.mPublicationDate);
        ((TextView)holder.mRssItemView.findViewById(R.id.linkText)).setText(rssModel.mLink);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mRssModels == null) {
            return 0;
        }
        return mRssModels.size();
    }
}
