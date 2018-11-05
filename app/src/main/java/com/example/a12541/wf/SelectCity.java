package com.example.a12541.wf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pku.qixiangyang.weather.Bean.City;
import pku.qixiangyang.weather.Bean.Cn2Spell;
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
    private EditText mEditText;
    private ArrayList<String> mSearchResult;
    private Map<String,String> nameToCode;
    private Map<String,String>nameToChar;
    private Map<String,String>nameToFirstChar;

    String updateCityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mEditText = (EditText)findViewById(R.id.select_city_editText);

        initView();
    }

    public void initView(){
        mArrayList = new ArrayList<String>();
        nameToCode = new HashMap<>();
        nameToChar = new HashMap<>();
        nameToFirstChar = new HashMap<>();

        buttonBack = (ImageView)findViewById(R.id.select_city_back);
        cityListv = (ListView)findViewById(R.id.select_city_list);
        buttonBack.setOnClickListener(this);

        myApplication = (MyApplication)getApplication();
        mCityList = myApplication.getCityList();
        for(int i=0;i<mCityList.size();i++){
            String cityName = mCityList.get(i).getCity();
            String cityCode = mCityList.get(i).getNumber();
            String cityChar = Cn2Spell.getPinYin(cityName);
            String cityFirstChar = Cn2Spell.getPinYinHeadChar(cityName);
            String ch = Cn2Spell.getPinYinFirstLetter(cityName).toUpperCase();
            StringBuilder temp = new StringBuilder(cityChar);
            temp.replace(0,1,ch);
            //以下三种可查找：全小写拼音，全大写拼音，首字母大写的拼音
            cityChar = cityChar+cityChar.toUpperCase()+temp;
            //以下两种可查找：全小写首字母，全大写首字母
            cityFirstChar = cityFirstChar+cityFirstChar.toUpperCase();
            nameToCode.put(cityName,cityCode);
            nameToChar.put(cityName,cityChar);
            nameToFirstChar.put(cityName,cityFirstChar);
            mArrayList.add(cityName);
        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchResult = new ArrayList<String>();
                for(String str : nameToChar.keySet()){
                    //汉字、首字母或者拼音，可查出
                    if(str.contains(s)||nameToChar.get(str).contains(s)||nameToFirstChar.get(str).contains(s)){
                        mSearchResult.add(str);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,
                        android.R.layout.simple_list_item_1,mSearchResult);
                //把适配器安上去
                cityListv.setAdapter(adapter);

                String editViewContent = mEditText.getText().toString();
                if(editViewContent.equals("")) {
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(SelectCity.this,
                            android.R.layout.simple_list_item_1,mArrayList);
                    //把适配器安上去
                    cityListv.setAdapter(adapter2);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        cityListv = (ListView)findViewById(R.id.select_city_list);
        //背过背过背过
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,
                android.R.layout.simple_list_item_1,mArrayList);
        //把适配器安上去
        cityListv.setAdapter(adapter);
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id){
                String editViewContent = mEditText.getText().toString();
                if(editViewContent.equals("")) {
                    updateCityCode = mCityList.get(position).getNumber();
                }else{
                    String returnCityName = mSearchResult.get(position);
                    updateCityCode = nameToCode.get(returnCityName);
                }
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
