package com.winorout.followme.personalCenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.winorout.followme.sports.DateTimeData;
import com.winorout.followme.sports.SaveTime;

import java.util.List;

/*
 * 对数据库进行增删改查
 */
public class PedometerDB {
	public static final String DB_NAME = "pedometer.db"; // 数据库名称
	public static final int VERSION = 1; // 数据库版本
	private static PedometerDB pedometerDB;
	private SQLiteDatabase db;
	private PedometerOpenHeleper pHelper;

	/*
	 * 将PedometerDB的构造方法设置为私有方法，在别的类中不能通过new来创建这个对象
	 * 
	 * @param context
	 */
	private PedometerDB(Context context) {
		pHelper = new PedometerOpenHeleper(context, DB_NAME, null, VERSION);
		db = pHelper.getWritableDatabase();
	}

	/*
	 * 使用单例模式创建数据库
	 */
	public synchronized static PedometerDB getInstance(Context context) {
		if (pedometerDB == null) {
			pedometerDB = new PedometerDB(context);
		}
		return pedometerDB;
	}

	/**
	 * 将实时数据保存到数据库
	 * 
	 * @param data
	 */
	public void insertNow(DateTimeData data) {
		ContentValues values = new ContentValues();
		values.put("startTime", data.date);
		values.put("totalTime", data.time);
		values.put("totalStep", data.step);
		values.put("totalCalorimetry", data.calorimetry);
		values.put("totalKilometer", data.kilometer);
		try {
			db.insert("now_table", null, values);
			Log.d("DBdatbase", "保存成功：" + data.step);
		} catch (Exception e) {
			Log.d("DBdatbase", "插入失败");
		}
	}

	/**
	 * 查找数据
	 * 
	 * @param data
	 * @return
	 */
	public DateTimeData queryNow(DateTimeData data) {
		DateTimeData datas = data;
		Cursor cursor = db.query("now_table", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			cursor.moveToLast();
			String currentTime = data.date;
			int time = cursor.getInt(cursor.getColumnIndex("totalTime"));
			int step = cursor.getInt(cursor.getColumnIndex("totalStep"));
			float calorimetry = cursor.getFloat(cursor.getColumnIndex("totalCalorimetry"));
			float kilometer = cursor.getFloat(cursor.getColumnIndex("totalCalorimetry"));
			Log.d("DBdatbase", "1____________当前步数____________________" + step);
			datas = new DateTimeData(currentTime, time, step, calorimetry, kilometer);
		}
		return datas;
	}

	public void insertRemind(String time, String depict) {
		if(selectSave(time)){
			Log.d("DBdatbase", "该时段已设置提醒");
			return;
		}
		ContentValues values = new ContentValues();
		values.put("time", time);
		values.put("depict", depict);
		try {
			db.insert("remind", null, values);
		} catch (Exception e) {
			Log.d("DBdatbase", "插入失败");
		}
	}

	public List<SaveTime> queryRemind(List<SaveTime> list) {
		Cursor cursor = db.query("remind", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String depict = cursor.getString(cursor.getColumnIndex("depict"));
			list.add(new SaveTime(time, depict));
		}
		return list;
	}
	private boolean selectSave(String time){
		Cursor cursor = db.query("remind", null,"time=?",new String[] {time}, null, null, null);
		if(!cursor.moveToFirst()){
			Log.d("zryservice", "未查找到该数据");
			return false;
		}
		return true;
	}
	public void deleteRemind(String time) {
			db.delete("remind", "time=?", new String[] { time });
	}
	public void updateRemind(String oder_time, String time, String depict) {
			if(selectSave(oder_time)){
				deleteRemind(oder_time);
				insertRemind(time,depict);
		}
	}
	public String selectGoals(){
		Cursor cursor = db.query("goals", null,null,null, null, null, null);
		if(!cursor.moveToFirst()){
			Log.d("zryservice", "未设置步数");
//			insertGoals(10000+"");
			return null;
		}
		return cursor.getString(0);
	}
	public void insertGoals(String step) {
		Log.d("zryservice", "设置步数"+step);
		ContentValues values = new ContentValues();
		values.put("step", step);
		if(selectGoals()!=null){
			Log.d("zryservice", "步数不为空"+selectGoals());
			db.update("goals", values, null, null);
			return;
		}
		try {
			db.insert("goals", null, values);
			Log.d("zryservice", "插入成功");
		} catch (Exception e) {
			Log.d("DBdatbase", "插入失败");
		}
	}
	// /*
	// * 增加User表里的数据
	// */
	// public void saveUser(User user) {
	// if (user != null) {
	// ContentValues values = new ContentValues();
	// values.put("objectId", user.getObjectId());
	// values.put("name", user.getName());
	// values.put("sex", user.getSex());
	// values.put("weigth", user.getWeight());
	// values.put("step_length", user.getStep_length());
	// values.put("picture", user.getPicture());
	// values.put("groupId", user.getGroupId());
	// values.put("today_step", user.getToday_step());
	// db.insert("user", null, values);
	// }
	// }
	//
	// /*
	// * 根据用户的Id删除User表的数据
	// */
	//
	// public void deleteUser(User user) {
	// if (user != null) {
	// db.delete("user", "objectId= ? ", new String[] { user.getObjectId() });
	// }
	// }
	//
	// /*
	// * 升级user表中的数据
	// */
	// public void updateUser(User user) {
	// if (user != null) {
	// ContentValues values = new ContentValues();
	// values.put("objectId", user.getObjectId());
	// values.put("name", user.getName());
	// values.put("sex", user.getSex());
	// values.put("weigth", user.getWeight());
	// values.put("step_length", user.getStep_length());
	// values.put("picture", user.getPicture());
	// values.put("groupId", user.getGroupId());
	// values.put("today_step", user.getToday_step());
	// db.update("user", values, "objectId= ? ", new String[] {
	// user.getObjectId() });
	// }
	// }
	//
	// /*
	// * 升级User表中的数据
	// */
	//
	// public void changeObjectId(User user) {
	// if (user != null) {
	// ContentValues values = new ContentValues();
	// values.put("ObjectId", user.getObjectId());
	// db.update("user", values, null, null);
	// }
	// }
	//
	// /*
	// * 增加Step中的数据
	// */
	// public void SaveStep(Step step) {
	// if (step != null) {
	// ContentValues values = new ContentValues();
	// values.put("number", step.getNumber());
	// values.put("date", step.getDate());
	// values.put("userId", step.getUserId());
	// db.insert("step", null, values);
	// }
	// }
	//
	// /*
	// * 升级Step中的数据
	// */
	// public void updateStep(Step step) {
	// if (step != null) {
	// ContentValues values = new ContentValues();
	// values.put("number", step.getNumber());
	// values.put("date", step.getDate());
	// values.put("userId", step.getUserId());
	// db.update("step", values, "userId = ? and date = ?", new String[] {
	// step.getUserId(), step.getDate() });
	// }
	// }
	//
	// /*
	// * 升级Step表的数据
	// */
	// public void changeStep(Step step) {
	// if (step != null) {
	// ContentValues values = new ContentValues();
	// values.put("userId", step.getUserId());
	// db.update("step", values, null, null);
	// }
	// }
	//
	// /*
	// * 增加group中的数据
	// */
	// public void saveGroup(Group group) {
	// if (group != null) {
	// ContentValues values = new ContentValues();
	// values.put("total_number", group.getTotal_number());
	// values.put("menber_number", group.getMember_number());
	// db.insert("group", null, values);
	// }
	// }
	//
	// /*
	// * 修改step中的数据
	// */
	// public void updateGroup(Group group) {
	// if (group != null) {
	// ContentValues values = new ContentValues();
	// values.put("total_number", group.getTotal_number());
	// values.put("member_number", group.getMember_number());
	// db.update("group", values, "Id = ?", new String[] {
	// String.valueOf(group.getID()) });
	// }
	// }

}