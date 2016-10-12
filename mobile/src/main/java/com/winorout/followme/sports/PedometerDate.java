package com.winorout.followme.sports;

import android.content.Context;
import android.widget.TextView;

public class PedometerDate {
	private TextView distanceText;
	private TextView caloriesText;
	private TextView velocityText;
	private TextView timeTextText;
	private CircleBar circleBar;
	private Context context;
	
	public PedometerDate(TextView distance, TextView calories, TextView velocity, TextView timeText,
						 CircleBar circleBar, Context context) {
		distanceText=distance;
		caloriesText=calories;
		velocityText=velocity;
		timeTextText=timeText;
		this.circleBar=circleBar;
		this.context=context;
	}
	}
