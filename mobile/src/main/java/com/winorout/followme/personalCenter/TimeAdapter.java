package com.winorout.followme.personalCenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winorout.followme.R;
import com.winorout.followme.sports.SaveTime;

import java.util.List;

public class TimeAdapter extends ArrayAdapter<SaveTime> {
	private int resourceId;
	public TimeAdapter(Context context, int textViewResourceId, List<SaveTime> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		SaveTime st=getItem(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.layout_motion = (RelativeLayout) view.findViewById(R.id.layout_motion);
			viewHolder.motion_time = (TextView) view.findViewById(R.id.motion_time);
			viewHolder.division = (TextView) view.findViewById(R.id.division);
			viewHolder.motion_date = (TextView) view.findViewById(R.id.motion_date);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.motion_time.setText(st.time);
		viewHolder.motion_date.setText(st.depict);
		return view;
	}
	class ViewHolder {
		RelativeLayout layout_motion;
		TextView motion_time;
		TextView division;
		TextView motion_date;
	}
}
