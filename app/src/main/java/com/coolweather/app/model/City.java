package com.coolweather.app.model;

/**
 * Created by Administrator on 2016-05-03.
 * 城市表的实体类
 */
public class City {
    /**
     * 主键,自增长的标识列,城市编号
     */
    private int id;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 城市代码,从服务器获取的数据
     */
    private String cityCode;
    /**
     * 省份编号,外键
     */
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
