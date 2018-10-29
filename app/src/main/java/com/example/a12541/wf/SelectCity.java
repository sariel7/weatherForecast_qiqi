package com.example.a12541.wf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pku.qixiangyang.weather.Bean.City;
import pku.qixiangyang.weather.app.MyApplication;

/**
 * Created by 12541 on 2018/10/25.
 */

public class SelectCity extends AppCompatActivity implements View.OnClickListener{

    private ImageView buttonBack;
    private ListView cityListv;
    private List<City> mCityList;
    private MyApplication myApplication;
    private ArrayList<String> mArrayList;

    String updateCityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        initView();
    }

    public void initView(){
        buttonBack = (ImageView)findViewById(R.id.select_city_back);
        cityListv = (ListView)findViewById(R.id.select_city_list);
        buttonBack.setOnClickListener(this);

        myApplication = (MyApplication)getApplication();
        mCityList = myApplication.getCityList();
        mArrayList = new ArrayList<String>();
        for(int i=0;i<mCityList.size();i++){
            String cityName = mCityList.get(i).getCity();
            mArrayList.add(cityName);
        }
        cityListv = (ListView)findViewById(R.id.select_city_list);
        //背过背过背过
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,
                android.R.layout.simple_list_item_1,mArrayList);
        //把适配器安上去
        cityListv.setAdapter(adapter);
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id){
                updateCityCode = mCityList.get(position).getNumber();
                Log.d("update city code",updateCityCode);
                Intent i = new Intent();
                i.putExtra("cityCode",updateCityCode);
                setResult(RESULT_OK,i);
                finish();
            }
        };
        cityListv.setOnItemClickListener(itemClickListener);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.select_city_back:
                Intent i = new Intent();
                i.putExtra("cityCode",updateCityCode);
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }

}
