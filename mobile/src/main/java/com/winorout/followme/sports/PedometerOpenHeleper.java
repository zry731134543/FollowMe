package com.winorout.followme.sports;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PedometerOpenHeleper extends SQLiteOpenHelper {
	
	public static final String NOW_TABLE="create table now_table("
			+ "startTime text primary key,"
			+ "totalTime integer,"
			+ "totalStep integer,"
			+ "totalCalorimetry real,"
			+ "totalKilometer real)";
	public static final String REMIND_TABLE="create table remind("
			+ "time text primary key,"
			+ "depict text)";
	public static final String GOALS_TABLE="create table goals(step integer)";
	/*
	 * 创建Step表
	 */
	public static final String CREATE_STEP="create table step("
			+"id integer primary key cutoincrement,"
			+"number text,"
			+"date integer,"
			+"userId text)";
	/*
	 * 创建group表
	 */
    public static final String CREATE_GROUP="create table group("
    		+"id integer primary key cutoincrement,"
    		+"total_number,"
    		+"member_number)";
    
    /*
     * 创建user表
     */
    public static final String CREATE_USER="create table user("
    		+"objectId  text,"
    		+"name text,"
    		+"sex text,"
    		+"weight integer,"
    		+"step_length integer,"
    		+"picture blob,"
    		+"groupId interger,"
    		+"today_step integer)";
    
    /*
     * 带参数的PedometerOpenHeleper的构造函数
     * @param context 
     * @param name
     * @param factory 
     * @param version
     */
	public PedometerOpenHeleper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		
	}
     
    
	@Override
	/*
	 * 创建数据库
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(NOW_TABLE);
		db.execSQL(REMIND_TABLE);
		db.execSQL(GOALS_TABLE);
		Log.d("DBdatbase", "创建数据库成功");
//		db.execSQL(CREATE_STEP);
//		db.execSQL(CREATE_GROUP);
//		db.execSQL(CREATE_USER);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}

