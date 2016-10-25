package com.winorout.entity;

/**
 *  运动数据
 * @author  ryzhang
 * @data 2016/10/25 14:30
 */
public class SportData {
    private int step;
    private int distance;
    private int goal;
    public SportData(int step,int distance,int goal){
        this.step=step;
        this.distance=distance;
        this.goal=goal;
    }

    /**
     * 将运动数据对象转换成json数据
     * @return
     */
    public String toJSON(){
        String json="{\"step\":%d,\"distance\":%d,\"goal\":%d}";
        json=String.format(json,step,distance,goal);
        return json;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }
}
