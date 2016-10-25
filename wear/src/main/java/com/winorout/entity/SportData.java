package com.winorout.entity;

/**
 *  运动数据
 * @author  ryzhang
 * @data 2016/10/25 14:30
 */
public class SportData {
    private int step;
    private double distance;
    public SportData(int step,double distance){
        this.step=step;
        this.distance=distance;
    }

    /**
     * 将运动数据对象转换成json数据
     * @return
     */
    public String toJSON(){
        String json="{\"step\":%d,\"distance\":\"%s\"}";
        json=String.format(json,step,distance+"");
        return json;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
