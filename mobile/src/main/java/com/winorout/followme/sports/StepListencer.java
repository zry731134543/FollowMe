package com.winorout.followme.sports;

import android.os.Message;

public interface StepListencer {
//	void onprogress(int step,long time,double kile,double calories);
	void onprogress(Message msg);
}
