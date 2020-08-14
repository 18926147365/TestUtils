package com.omen.bean;

/**
 * @author 李浩铭
 * @date 2020/8/14 16:13
 * @descroption
 */
public class LogModel {


    private long birthTime;//最近发生时间

    private long occurCount;//发生次数

    private long frequency;//发生频率（单位s） 52代表每52秒发生一次

    public LogModel(){}

    public LogModel(long birthTime, long occurCount, long frequency) {
        this.birthTime = birthTime;
        this.occurCount = occurCount;
        this.frequency = frequency;
    }

    public long getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(long birthTime) {
        this.birthTime = birthTime;
    }

    public long getOccurCount() {
        return occurCount;
    }

    public void setOccurCount(long occurCount) {
        this.occurCount = occurCount;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }
}
