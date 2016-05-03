package com.coolweather.app.model;

/**
 * Created by Administrator on 2016-05-03.
 * 省份表的实体类
 */
public class Province {
    /**
     * 主键列,自动增长,省份编号
     */
    private int id;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 省份代码,从服务器获取的数据
     */
    private String provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
