package com.winorout.followme.personalCenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.winorout.followme.R;
import com.winorout.followme.sports.PedometerDB;
import com.winorout.followme.sports.SaveTime;

import java.util.ArrayList;
import java.util.List;

public class Exercise extends Activity {
	TimeAdapter adapter;
	List<SaveTime> list = new ArrayList<>();
	ListView listView;
	Button add_time;
	PedometerDB db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exercise);
		db=PedometerDB.getInstance(this);
		list=db.queryRemind(list);
		add_time=(Button)findViewById(R.id.add_time);
		listView=(ListView)findViewById(R.id.remind_time_list);
		adapter=new TimeAdapter(this, R.layout.item_list,list);
		listView.setAdapter(adapter);
		//新建运动提醒
		add_time.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(Exercise.this,SettingTime.class);
				startActivity(intent);
			}
		});
		//更改运动提醒
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SaveTime st=list.get(position);
				Intent intent=new Intent(Exercise.this,SettingTime.class);
				intent.putExtra("time", st.time);
				intent.putExtra("date", st.depict);
				startActivity(intent);
			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				DeleteDialog dialog = new DeleteDialog(Exercise.this);
				dialog.setPosition(position);
				dialog.show();
				return true;
			}
		});
	}
	Handler handle=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){
				init();
				Log.d("zryservice", "当前提醒数："+list.size());
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		};
	};
	@Override
	public void onResume(){
		Log.d("zryservice", "恢复activity");
		handle.sendEmptyMessage(0);
		super.onResume();
		
	}
	private void init(){
		list.clear();
		list=db.queryRemind(list);
		adapter.notifyDataSetChanged();
	}
	/**
	 * 删除对话框
	 * 
	 * @author zhangruyi
	 *
	 */
	class DeleteDialog extends Dialog implements OnClickListener {
		private Button btn_delete;
		private int position;
		public DeleteDialog(Context context) {
			super(context);
		}
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// 设置不显示对话框标题栏
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			// 设置对话框显示的布局文件
			setContentView(R.layout.delete_dialog);
			btn_delete = (Button) findViewById(R.id.btn_delete);
			btn_delete.setOnClickListener(this);
		}
		public void setPosition(int position){
			this.position=position;
		}
		@Override
		public void onClick(View v) {
			String time=list.get(position).time;
			db.deleteRemind(time);
			handle.sendEmptyMessage(0);
			DeleteDialog.this.cancel();
		}
	}
}
