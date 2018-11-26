package com.example.a12541.wf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pku.qixiangyang.weather.Bean.TodayWeather;
import pku.qixiangyang.weather.app.MyApplication;
import pku.qixiangyang.weather.util.NetUtil;

/**
 * Created by 12541 on 2018/10/24.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RotateAnimation animation;

    private static final int UPDATE_TODAY_WEATHER = 1;

    private String cityCode;

    private ImageView buttManager, buttLocation, buttShare, buttUpdate, imgFell, imgWeatherKind;
    private TextView textTitleCityName, textCityName, textPushTime, texthHumidity, text_PM_title,
            text_PM_number, text_PM_fell, textDate, textTemperature, textWeatherKind, textWindPower;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();


    private Handler mHandler = new Handler() {
        //这是主线程
        //handler是Android给我们提供用来更新UI的一套机制，也是一套消息处理机制
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        //在onCreate里面，需要先用super.onCreate()来初始化，
        // 你可能会用setContentView(int)来定义一个布局，或者用findViewById()来操纵一个控件
        super.onCreate(saveInstanceState);
        //调用父类的onCreate构造函数
        //savedInstanceState参数是保存当前Activity的状态信息
        setContentView(R.layout.weather_info);
        //Bundle类型的数据与Map类型的数据相似，以key-value的形式存储数据
        //saveInsanceState参数是指保存实例状态即保存Activity(活动)的状态

        buttManager = (ImageView) findViewById(R.id.title_city_manager);
        buttLocation = (ImageView) findViewById(R.id.title_location);
        buttShare = (ImageView) findViewById(R.id.title_share);
        buttUpdate = (ImageView) findViewById(R.id.title_update);
        //找到相关控件ById，然后赋给某个变量，便于操纵
        buttManager.setOnClickListener(this);
        buttLocation.setOnClickListener(this);
        buttShare.setOnClickListener(this);
        buttUpdate.setOnClickListener(this);
        //与之匹配的是onClick()

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);

        //判断有没有网，这也就是为什么要把getNetworkState的原因
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            //记录在日志文件里
            Log.d("netState", "网络ok");
            //show函数是为了将Toast展示出来
            //第二个参数是Toast显示的内容；第三个参数是Toast显示的时长
            Toast.makeText(MainActivity.this, "别看了别看了，网好着呢！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("newState", "没网");
            Toast.makeText(MainActivity.this, "你家又没网了！快去交网费啊！", Toast.LENGTH_LONG).show();
        }

        initView();
        initLocation();

    }

    void initView() {
        imgFell = (ImageView) findViewById(R.id.content_pm2_5_fell_img);
        imgWeatherKind = (ImageView) findViewById(R.id.content_weather_img);
        textTitleCityName = (TextView) findViewById(R.id.title_city_name);
        textCityName = (TextView) findViewById(R.id.content_city_name);
        textPushTime = (TextView) findViewById(R.id.content_push_time);
        texthHumidity = (TextView) findViewById(R.id.content_humidity);
        text_PM_title = (TextView) findViewById(R.id.content_pm2_5_title);
        text_PM_number = (TextView) findViewById(R.id.content_pm2_5_number);
        text_PM_fell = (TextView) findViewById(R.id.content_pm2_5_fell_text);
        textDate = (TextView) findViewById(R.id.content_date);
        textTemperature = (TextView) findViewById(R.id.content_temperature);
        textWeatherKind = (TextView) findViewById(R.id.content_weather_kind);
        textWindPower = (TextView) findViewById(R.id.content_wind_power);

        textTitleCityName.setText("你猜这是啥");
        textCityName.setText("猜不到了吧");
        textPushTime.setText("hhhhh");
        texthHumidity.setText("这是个app啊！");
        text_PM_title.setText("你猜这啥app啊");
        text_PM_number.setText("别卸，别卸");
        text_PM_fell.setText("我错了");
        textDate.setText("我好好说话");
        textTemperature.setText("哎呀这是天气预报嘛");
        textWeatherKind.setText("你问天气在哪？");
        textWindPower.setText("那你点刷新啊！");

        cityCode = "101010100";
        animation = new RotateAnimation(0, 359, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_location) {
            if (mLocationClient.isStarted()) {
                mLocationClient.stop();
            }
            mLocationClient.start();
            final Handler BDHandler = new Handler() {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case DB:
                            if (msg.obj != null) {
                                if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORK_NONE) {
                                    Log.d("myWeather", "网络ok");
                                    queryWeatherCode(myListener.cityCode);
                                } else {
                                    Log.d("myWeather", "网络挂了");
                                    Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        default:
                            break;
                    }
                }
            };
        }
        if (view.getId() == R.id.title_share){
        }
        if (view.getId() == R.id.title_update) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            /*
            Log.d("当前cityCode：", cityCode);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            */

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("netState", "网络ok");
                queryWeatherCode(cityCode);
            } else {
                Log.d("newState", "没网");
                Toast.makeText(MainActivity.this, "都说了没网了……你到底交不交网费啊……", Toast.LENGTH_LONG).show();
            }
        }
        if (view.getId() == R.id.title_city_manager) {
            //页面跳转，先背过再说
            Intent i = new Intent(this, SelectCity.class);
            //原来是startActivitu(i)来着,也能跳转
            startActivityForResult(i, 1);
        }

    }

    //这个函数可以获取到网络数据
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("address:", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    //创建一个URL，获取连接对象，没有建立连接
                    URL url = new URL(address);
                    //设置连接
                    con = (HttpURLConnection) url.openConnection();
                    //设置请求方法，必须大写
                    con.setRequestMethod("GET");
                    //设置内容的时间超时
                    con.setConnectTimeout(8000);
                    //读取时间超时
                    con.setReadTimeout(8000);
                    //拿到服务器返回的输入流
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("str:", str);
                    }
                    String responseStr = response.toString();
                    Log.d("responseStr:", responseStr);
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("todatWeather:", todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    //解析读取到的网络数据
    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather) {
        textTitleCityName.setText(todayWeather.getCity() + "天气");
        textCityName.setText(todayWeather.getCity());
        textPushTime.setText(todayWeather.getUpdatetime() + "发布");
        texthHumidity.setText("湿度" + todayWeather.getShidu());
        text_PM_title.setText("PM2.5");
        text_PM_number.setText(todayWeather.getPm25());
        text_PM_fell.setText(todayWeather.getQuality());
        textDate.setText(todayWeather.getDate());
        textTemperature.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        textWeatherKind.setText(todayWeather.getType());
        textWindPower.setText("风力:" + todayWeather.getFengli());
        Toast.makeText(MainActivity.this, "呐，最新的天气哦！", Toast.LENGTH_SHORT).show();

    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        // 就是这个方法设置为 true ，才能获取当前的位置信息
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");
        // 可选，默认 gcj02 ，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);
        // 可选，默认 0 ，即仅定位一次，设置发起定 位请求的间隔需要大于等于 1000ms 才是有效的
        mLocationClient.setLocOption(option);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cityCode = data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为" + cityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "大哥，你网掉了……", Toast.LENGTH_LONG).show();
            }
        }
    }

}
