package com.example.a12541.wf;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import java.util.List;

import pku.qixiangyang.weather.Bean.City;
import pku.qixiangyang.weather.app.MyApplication;

/**
 * Created by 12541 on 2018/11/12.
 */

public class MyLocationListener extends BDAbstractLocationListener{
    public String recity;
    public String cityCode;
    List<City> mCityList;
    MyApplication myApplication;

    @Override
    public void onReceiveLocation(BDLocation location){
        String addr = location.getAddrStr();
        String country = location.getCountry();
        String provinve = location.getProvince();
        String city = location.getCity();
        String district = location.getDistrict();
        String street = location.getStreet();
        Log.d("location_city",city);
        recity = city.replace("市","");

        myApplication = MyApplication.getInstance();
        mCityList = myApplication.getCityList();

        for(City city1:mCityList){
            if(city1.getCity().equals(recity)){
                cityCode = city1.getNumber();
                Log.d("location_code",cityCode);
            }
        }

    }
}
