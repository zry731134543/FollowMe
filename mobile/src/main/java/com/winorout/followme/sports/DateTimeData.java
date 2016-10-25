package com.winorout.followme.sports;

import android.os.Bundle;
import android.os.Message;

import java.io.Serializable;

/**
 * 存放实时数据表数据
 * @author zhangruyi 3.20
 * 
 *
 */
public class DateTimeData implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 运动时间
	 */
	private String date;
	/**
	 * 总时间
	 */
	private int time;
	/**
	 * 总步数
	 */
	private int step;
	/**
	 * 总热量
	 */
	private double calorimetry;
	/**
	 * 总里数
	 */
	private double kilometer;

	/**
	 * 构造方法
	 * @param date 运动时间
	 * @param time 总时间
	 * @param step 步数
	 * @param calorimetry 总热量
	 * @param kilometer 总里数
	 */
	public DateTimeData(String date, int time, int step, double calorimetry,
						double kilometer) {
		this.date = date;
		this.time = time;
		this.step = step;
		this.calorimetry = calorimetry;
		this.kilometer = kilometer;
	}
	public DateTimeData(Message msg, String date){
		Bundle data=msg.getData();
		this.date = date;
		this.step=data.getInt("step");
		this.time=data.getInt("time");
		this.calorimetry=data.getDouble("calories");
		this.kilometer=data.getDouble("kile");
		
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public double getCalorimetry() {
		return calorimetry;
	}

	public void setCalorimetry(double calorimetry) {
		this.calorimetry = calorimetry;
	}

	public double getKilometer() {
		return kilometer;
	}

	public void setKilometer(double kilometer) {
		this.kilometer = kilometer;
	}
}
