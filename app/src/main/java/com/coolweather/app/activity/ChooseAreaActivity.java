package com.coolweather.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-05-03.
 */
public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    /**
     * 进度条
     */
    private ProgressDialog progressDialog;

    /**
     * 标题显示的文本
     */
    private TextView titleText;

    /**
     * 显示省份,城市,区县等信息的列表视图控件
     */
    private ListView listView;

    /**
     * 加载省份
     */
    private Button loadProvince;

    /**
     * listview对象的数据适配器
     */
    private ArrayAdapter<String> adapter;

    /**
     * 操作数据库数据的工具类
     */
    private CoolWeatherDB coolWeatherDB;

    /**
     * 字符串ArrayList集合,存放一些临时数据,例如 省份名称,城市名称,区县名称 等信息
     */
    private List<String> dataList = new ArrayList<>();

    /**
     * 省份列表
     */
    private List<Province> provinceList;

    /**
     * 城市列表
     */
    private List<City> cityList;

    /**
     * 区县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别,默认状态的值为0
     */
    private int currentLevel;

    /**
     * 是否从WeatherActivity中跳转过来
     */
    private boolean isFromWeatherActivity;

    /**
     * 此方法如果自动重写父类的为2个参数,则不会执行,必须手动去掉一个参数
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取天气信息显示活动跳转时传递的数据
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // 已经选择了城市 并且不是从WeatherActivity中跳转过来,才会在当前活动启动时立即跳转到天气信息显示的活动 WeatherActivity
        if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
        // 直接跳过加载布局,显示天气
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        loadProvince = (Button) findViewById(R.id.btn_load_province);
        /**
         * 创建了listView的数据适配器实例,但是当前的 dataList 的集合大小为0,没有添加数据
         */
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);      // 静态方法获取静态的唯一的 CoolWeatherDB类对象
        /**
         * 点击ListView控件的子项的监听器
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug", "点击了ListView控件的子项");
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);    // 获取当前选中的省份
                    // 根据当前选中的省份查询所有城市
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);    // 获取当前选中的城市
                    // 根据当前选中的城市查询所有区县
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String countyCode = countyList.get(position).getCountyCode();     // 获取当前选中的区县代码
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        /**
         * 加载省份按钮的点击事件
         */
        loadProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryProvinces();   // 加载省份数据
            }
        });
        queryProvinces();   // 加载省份数据
        Toast.makeText(this, "活动已创建", Toast.LENGTH_SHORT).show();
    }




    /**
     * 查询全国所有的省份,优先从数据库查询,如果没有查询到再去服务器查询
     */
    private void queryProvinces(){
        provinceList=coolWeatherDB.loadProvinces();
        if(provinceList!=null&&provinceList.size()>0){
            // 从数据库获取省份列表数据
            Log.d("ChooseAreaActivity","从数据库获取省份数据");

            dataList.clear();   // 清空临时数据
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());   // 保存所有省份的名称
            }
            adapter.notifyDataSetChanged();         // 因为dataList添加了省份名称,刷新显示
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else {
            // 从服务器获取省份列表数据
            Log.d("ChooseAreaActivity","从服务器获取省份数据");
            queryFromServer(null,"province");
        }
    }




    /**
     * 查询选中的省份的所有的城市名称,优先从数据库查询,如果没有查询到再去服务去查询
     */
    private void queryCities(){
        cityList=coolWeatherDB.loadCities(selectedProvince.getId());
        if(cityList!=null&&cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else {
            /**
             * 从服务器获取某个省份的城市列表, 参数code为省份代码, 参数type为请求的数据是什么类型的
             */
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }


    /**
     * 查询选中的城市的所有的区县,优先从数据库查询,如果没有找到再去服务器查询
     */
    private void queryCounties(){
        countyList=coolWeatherDB.loadCounties(selectedCity.getId());
        if(countyList!=null&&countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }else {
            /**
             * 从服务器获取某个城市的区县列表,参数code为城市代码, 参数type为请求的数据的类型
             */
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }


    /**
     * 从服务器请求省份或者城市数据
     * @param code  省份或者城市以及区县代码,如果为null表示获取省份列表,否则为获取城市和区县
     * @param type  类型,表示是省份或者是城市以及区县,省份和城市的链接地址类似,只不过代码不同 例如 苏州 1904 昆山 190404
     */
    private void queryFromServer(final String code,@NonNull final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else {
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        // 显示进度条
        showProgressDialog();

        /**
         * 请求网络数据的工具类
         */
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            /**
             * 请求服务器数据成功的回调方法
             * @param response      服务器响应的数据
             */
            @Override
            public void onFinish(String response) {
                boolean result=false;

                /**
                 * 从服务器获取数据,根据类型判断调用那个方法保存到数据库的表中
                 */
                if("province".equals(type)){
                    result= Utility.handleProvincesResponse(coolWeatherDB,response);
                }else if("city".equals(type)){
                    result=Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }


                if(result){
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();   // 递归调用此方法,继续执行之前获取省份列表的操作
                            }else if("city".equals(type)){
                                queryCities();      // 递归调用此方法,继续执行之前获取某个选中省份的城市列表的操作
                            }else if("county".equals(type)){
                                queryCounties();    // 递归调用此方法,继续之前获取某个选中的城市的区县列表的操作
                            }
                        }
                    });
                }

            }


            /**
             * 请求服务器数据出现异常的回调方法
             * @param e
             */
            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }




    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }



    /**
     * 关闭进度条对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }


    /**
     * back键点击事件的回调方法
     * 根据当前选中的节点级别判断是返回上一级还是直接退出
     */
    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {
            // 表示从天气信息显示的活动返回到城市选择活动,再次点击返回事件,会继续进入显示天气信息的活动
            if(isFromWeatherActivity){
                Intent intent=new Intent(this,WeatherActivity.class);
                startActivity(intent);      // 表示用户进入了当前活动,却没有选择城市,而是直接返回了天气信息活动
            }
            finish();
        }
    }
}