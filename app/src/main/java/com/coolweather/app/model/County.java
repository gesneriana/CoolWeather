package com.coolweather.app.model;

/**
 * Created by Administrator on 2016-05-03.
 * County表的实体类,区县表
 */
public class County {
    /**
     * 区县编号,自增长的标识列
     */
    private int id;
    /**
     * 区县名称
     */
    private String countyName;
    /**
     * 区县代码,从服务器获取的编号
     */
    private String countyCode;
    /**
     * 城市编号,外键
     */
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
