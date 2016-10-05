package com.winorout.adapter;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winorout.followme.R;

/**
 * Created by xwangch on 16/10/5.
 */

public class FollowRVAdapter extends WearableListView.Adapter {

    private Context mContext;
    private String[] item_string= {"数据", "弹幕", "防盗"};
    private int[] item_imgId = {R.drawable.sportdata, R.drawable.barrage, R.drawable.domsteal};

    public FollowRVAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public FollowRVAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_follow_rv, parent, false));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        ((ItemViewHolder)holder).itemIv.setImageResource(item_imgId[position]);
        ((ItemViewHolder)holder).itemTv.setText(item_string[position]);
    }

    @Override
    public int getItemCount() {
        return item_string.length;
    }

    public static class ItemViewHolder extends WearableListView.ViewHolder{

        ImageView itemIv;
        TextView itemTv;
        LinearLayout itemLl;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemIv = (ImageView) itemView.findViewById(R.id.item_iv);
            itemTv = (TextView) itemView.findViewById(R.id.item_tv);
            itemLl = (LinearLayout) itemView.findViewById(R.id.item_ll);
        }
    }
}
