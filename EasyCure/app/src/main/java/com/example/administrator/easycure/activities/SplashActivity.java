package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.CacheUtil;
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.NetworkUsable;
import com.example.administrator.easycure.utils.RegexUtil;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;
import com.example.administrator.easycure.utils.titanic.Titanic;
import com.example.administrator.easycure.utils.titanic.TitanicTextView;
import com.example.administrator.easycure.utils.titanic.Typefaces;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Administrator on 2018/11/4 0004.
 */

public class SplashActivity extends BaseActivity {

    //服务器端存的数据
    private int article_count;
    private int advice_count;

    //本地缓存的数据
    private int article_count_cache;
    private int advice_count_cache;

    private int countDown = 1;

    private TextView activity_splash_tv;
    private EasyCureTimer timer;

    //开关，当flag为1时则让用户可点击文章，为0不让点击，因为子线程的调用和网络请求都需要消耗一定的时间，
    // 因此只有充分把数据准备好后才让用户可操作

    private Intent intent;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 0:     //判断当前是否需要从服务器端爬到新的文章，如果不需要则什么都不做，让用户读缓存即可
                    if(SplashActivity.this.article_count > SplashActivity.this.article_count_cache){
                        //进入这里表示需要从服务器爬取数据
                        //需要爬取的记录条数
                        int countForGet = SplashActivity.this.article_count - SplashActivity.this.article_count_cache;
                        getArticles(LangGetUtil.langGet(),countForGet);
                    }else{
                        //如果到这里表示当前缓存中的数据和服务器数据是一致的，接下来对缓存建议数量与服务器建议数量比较
                        //表示已经成功从服务器拿到最新的文章了，接下来拿有关建议的内容
                        if(SplashActivity.this.advice_count > SplashActivity.this.advice_count_cache){
                            //进入这里表示需要从服务器爬取数据
                            int countForGet = SplashActivity.this.advice_count - SplashActivity.this.advice_count_cache;
                            getAdvice(LangGetUtil.langGet(),countForGet);
                        }else {
                            //进入这里表示缓存建议的数量和服务器是一样的，因此不需要去服务器拿建议了，直接跳界面
                            boolean isLoginNow = SpUtil.getLoginStatus(SplashActivity.this);
                            if (isLoginNow) {

                                intent = new Intent(SplashActivity.this, MainActivity.class);
                                intent.putExtra("welcome_back", true);
                                startActivity(intent);
                            } else {
                                intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            timer.cancel();
                            finish();
                        }
                    }
                    break;
                case 1:
                    //表示已经成功从服务器拿到最新的文章了，接下来拿有关建议的内容
                    if(SplashActivity.this.advice_count > SplashActivity.this.advice_count_cache){
                        //进入这里表示需要从服务器爬取数据
                        int countForGet = SplashActivity.this.advice_count - SplashActivity.this.advice_count_cache;
                        getAdvice(LangGetUtil.langGet(),countForGet);
                    }else {
                        //进入这里表示缓存建议的数量和服务器是一样的，因此不需要去服务器拿建议了，直接跳界面
                        boolean isLoginNow = SpUtil.getLoginStatus(SplashActivity.this);
                        if (isLoginNow) {
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra("welcome_back", true);
                            startActivity(intent);
                        } else {
                            intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        timer.cancel();
                        finish();
                    }
                    break;
                case 2:
                    //进入这里表示缓存建议的数量和服务器是一样的，因此不需要去服务器拿建议了，直接跳界面
                    boolean isLoginNow = SpUtil.getLoginStatus(SplashActivity.this);
                    if(isLoginNow){

                        intent = new Intent(SplashActivity.this,MainActivity.class);
                        intent.putExtra("welcome_back",true);
                        startActivity(intent);
                    }else{
                        intent = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                    timer.cancel();
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Drawable[] drawables = {getDrawable(R.drawable.splash_bg1),getDrawable(R.drawable.splash_bg2),getDrawable(R.drawable.splash_bg3)};

        RelativeLayout relativeLayout = findViewById(R.id.activity_splash_rl);
        //随机背景
        relativeLayout.setBackground(drawables[(int)(Math.round(Math.random()*2))]);

        TitanicTextView tv = (TitanicTextView) findViewById(R.id.my_text_view);

        // set fancy typeface
        tv.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));

        // start animation
        new Titanic().start(tv);

        activity_splash_tv = (TextView)findViewById(R.id.activity_splash_tv);

        timer = new EasyCureTimer(16000,500);
        timer.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(NetworkUsable.isNetworkConnected(this)){
            //获取服务器端存的文章数量和建议数量，并将数据写入缓存
            isNeedToVisitNetwork();
        }else{
            //网络不可用则直接弹出网络异常
            Toast.makeText(this,getResources().getString(R.string.network_anomaly),Toast.LENGTH_LONG).show();
        }

    }

    //判断文章和建议是否都做了缓存，只要有一种没做缓存则认为全都没做缓存，重新请求一次
    public void isNeedToVisitNetwork(){
        if(CacheUtil.getCacheData(this,"article_count","String") != null && CacheUtil.getCacheData(this,"advice_count","String") != null){
            //进入这里表示有一些文章和建议存在缓存中
            this.article_count_cache = Integer.parseInt((String)(CacheUtil.getCacheData(this,"article_count","String")));
            this.advice_count_cache = Integer.parseInt((String)(CacheUtil.getCacheData(this,"advice_count","String")));

            getDataCount();
        }else{
            //进入这里表示缓存中没有保存文章，需要从网络获取
            this.article_count_cache = 0;
            this.advice_count_cache = 0;

            getDataCount();
        }
    }

    //获取服务器端存的文章数量和建议数量
    public void getDataCount(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://119.23.208.63/ECure-system/public/data.json");

                    HttpURLConnection con = (HttpURLConnection)(url.openConnection());

                    con.setRequestMethod("GET");
                    con.setConnectTimeout(5000);
                    int code = con.getResponseCode();

                    if(code == 200){
                        InputStream is = con.getInputStream();

                        String jsonStr = StrUtil.stream2String(is);

                        if (jsonStr.startsWith("\ufeff")) {
                            jsonStr = jsonStr.substring(1);
                        }

                        JSONObject json = new JSONObject(jsonStr);

                        //从服务器端拿到最新文章数量
                        SplashActivity.this.article_count = Integer.parseInt(json.getString("article_count"));
                        SplashActivity.this.advice_count = Integer.parseInt(json.getString("advice_count"));
                        //把从服务器端拿到的文章数量保存在缓存中
                        CacheUtil.saveDataByCache(SplashActivity.this,"article_count",SplashActivity.this.article_count + "");
                        //从服务器端拿到最新建议数量
                        CacheUtil.saveDataByCache(SplashActivity.this,"advice_count",SplashActivity.this.advice_count + "");

                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        handler.sendMessageDelayed(msg,10);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //通过网络请求拿到相关的文章
    public void getArticles(final String lang,final int count){
        new Thread(new Runnable() {

            InputStream is = null;
            @Override
            public void run() {
                try{
                    String strUrl = "http://119.23.208.63/ECure-system/public/index.php/get_articles/";

                    URL url = new URL(strUrl);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);

                    String data = "lang=" + URLEncoder.encode(lang) + "&count=" + URLEncoder.encode(count + "");

                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    con.setRequestProperty("Content-Length",data.length() + "");

                    con.setDoOutput(true);

                    OutputStream os = con.getOutputStream();

                    os.write(data.getBytes());

                    int code = con.getResponseCode();

                    if(code == 200){
                        is = con.getInputStream();
                        String jsonStr = StrUtil.stream2String(is);

                        if(jsonStr.startsWith("\ufeff")){
                            jsonStr = jsonStr.substring(1);
                        }

                        jsonStr = jsonStr.replace("[","").replace("]","");

                        //通过正则表达式把所有的json字符串截出并转换成json对象
                        List<JSONObject> jsonList = RegexUtil.parseJsonStr2JsonObjList(jsonStr);

                        if(jsonList.size() > 0){
                            for(JSONObject json : jsonList){

                                String cacheFileName1 = "article" + json.getString("article_id") + "_title";
                                String cacheFileName2 = "article" + json.getString("article_id") + "_content";
                                String cacheFileName3 = "article" + json.getString("article_id") + "_img1";
                                String cacheFileName4 = "article" + json.getString("article_id") + "_img2";
                                String cacheFileName5 = "article" + json.getString("article_id") + "_img3";

                                //把查到的数据写入缓存
                                CacheUtil.saveDataByCache(SplashActivity.this,cacheFileName1,json.getString("title"));
                                CacheUtil.saveDataByCache(SplashActivity.this,cacheFileName2,json.getString("content"));
                                CacheUtil.saveDataByCache(SplashActivity.this,cacheFileName3,getBitmap(json.getString("img1")));
                                CacheUtil.saveDataByCache(SplashActivity.this,cacheFileName4,getBitmap(json.getString("img2")));
                                CacheUtil.saveDataByCache(SplashActivity.this,cacheFileName5,getBitmap(json.getString("img3")));

                            }
                        }

                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        handler.sendMessageDelayed(msg,10);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //根据url拿到网络图片
    public Bitmap getBitmap(String imgUrl){
        try{
            URL url = new URL(imgUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            int code = con.getResponseCode();

            if(code == 200){
                InputStream is = con.getInputStream();

                return BitmapFactory.decodeStream(is);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //通过网络请求拿到相关的建议，只拿本地缓存没有的数据
    public void getAdvice(final String lang, final int count){
        new Thread(new Runnable() {

            InputStream is = null;
            @Override
            public void run() {
                try{
                    //这是我电脑在宿舍开wifi后的ip
                    String strUrl = "http://119.23.208.63/ECure-system/public/index.php/get_advice/";

                    URL url = new URL(strUrl);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);

                    String data = "lang=" + URLEncoder.encode(lang) + "&count=" + URLEncoder.encode(count + "");

                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    con.setRequestProperty("Content-Length",data.length() + "");

                    con.setDoOutput(true);

                    OutputStream os = con.getOutputStream();

                    os.write(data.getBytes());

                    int code = con.getResponseCode();

                    if(code == 200){
                        is = con.getInputStream();
                        String jsonStr = StrUtil.stream2String(is);

                        if(jsonStr.startsWith("\ufeff")){
                            jsonStr = jsonStr.substring(1);
                        }

                        jsonStr = jsonStr.replace("[","").replace("]","").replaceAll("\n","").replaceAll("\r","");

                        //通过正则表达式把所有的json字符串截出并转换成json对象
                        List<JSONObject> jsonList = RegexUtil.parseJsonStr2JsonObjList(jsonStr);


                        if(jsonList.size() > 0){
                            for(JSONObject json : jsonList){

                                String cacheFileName = "advice" + json.getString("type_id") + "_" + json.getString("health_advice_id");

                                CacheUtil.saveDataByCache(SplashActivity.this,cacheFileName,json.getString("advice"));

                            }
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessageDelayed(msg,10);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class EasyCureTimer extends CountDownTimer {

        public EasyCureTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            switch(countDown){
                case 1:
                    activity_splash_tv.setText(getResources().getString(R.string.init_data) + ".");
                    countDown++;
                    break;
                case 2:
                    activity_splash_tv.setText(getResources().getString(R.string.init_data) + "..");
                    countDown++;
                    break;
                case 3:
                    activity_splash_tv.setText(getResources().getString(R.string.init_data) + "...");
                    countDown++;
                    break;
                case 4:
                    activity_splash_tv.setText(getResources().getString(R.string.init_data) + "....");
                    countDown = 1;
                    break;
            }
        }

        @Override
        public void onFinish() {
            activity_splash_tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
