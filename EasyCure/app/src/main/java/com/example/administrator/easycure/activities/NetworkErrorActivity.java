package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.CacheUtil;
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.NetworkUsable;
import com.example.administrator.easycure.utils.RegexUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2019/3/17 0017.
 */

//网络错误界面，当点击触发网络请求的时候会先检测用户当前的网络状态，如果没办法ping 通我们的服务器则认为网络异常
public class NetworkErrorActivity extends BaseActivity implements View.OnClickListener{

    private ImageView activity_network_error_back;
    private Button activity_network_error_btn;

    private Intent mIntent,intent;

    private int flag = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("倒是稍微执行以下啊");
            switch(msg.what){
                case 0:
                    flag = 0;
                    intent = new Intent(NetworkErrorActivity.this,DrawerActivity.class);
                    intent.putExtra("itemId",2);
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    flag = 0;
                    intent = new Intent(NetworkErrorActivity.this,DrawerActivity.class);
                    intent.putExtra("itemId",4);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error);

        init();

    }

    public void init(){

        mIntent = getIntent();

        activity_network_error_back = (ImageView)findViewById(R.id.activity_network_error_back);
        activity_network_error_btn = (Button)findViewById(R.id.activity_network_error_btn);

        activity_network_error_back.setOnClickListener(this);
        activity_network_error_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_network_error_btn:
                if(NetworkUsable.isNetworkConnected(this)){
                    //从前一个界面出现网络异常之后把网络正常时的目的界面标识传过来
                    switch(mIntent.getStringExtra("network_error")){
                        case "toFragmentPormotion": //表示去健康咨询模块网络出错或本地没有缓存，但现在有了
                            if(flag == 0){  //0表示当前没有在做请求，1表示正在做请求处理
                                flag = 1;
                                //0表示查所有的数据，调用该函数做网络请求并把请求到的数据写入缓存
                                getArticles(LangGetUtil.langGet(),0);
                            }
                            Toast.makeText(this,getResources().getString(R.string.checking_network),Toast.LENGTH_SHORT).show();
                            break;
                        case "toFragmentAdvice":
                            if(flag == 0){
                                flag = 1;
                                ///0表示查所有的数据，调用该函数做网络请求并把请求到的数据写入缓存
                                getAdvice(LangGetUtil.langGet(),0);
                            }
                            Toast.makeText(this,getResources().getString(R.string.checking_network),Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                break;
            case R.id.activity_network_error_back:
                finish();
                break;
        }
    }

    //通过网络请求拿到相关的文章
    public void getArticles(final String lang,final int count){
        new Thread(new Runnable() {

            InputStream is = null;
            @Override
            public void run() {
                try{
                    //这是我电脑在宿舍开wifi后的ip
                    String strUrl = "http://192.168.191.1:80/phpWorkspace/ECure-system/public/index.php/article/get_articles/";

                    URL url = new URL(strUrl + lang + "/" + count);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);
                    int code = con.getResponseCode();

                    if(code == 200){
                        is = con.getInputStream();
                        String jsonStr = StrUtil.stream2String(is);

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
                                CacheUtil.saveDataByCache(NetworkErrorActivity.this,cacheFileName1,json.getString("title"));
                                CacheUtil.saveDataByCache(NetworkErrorActivity.this,cacheFileName2,json.getString("content"));
                                CacheUtil.saveDataByCache(NetworkErrorActivity.this,cacheFileName3,getBitmap(json.getString("img1")));
                                CacheUtil.saveDataByCache(NetworkErrorActivity.this,cacheFileName4,getBitmap(json.getString("img2")));
                                CacheUtil.saveDataByCache(NetworkErrorActivity.this,cacheFileName5,getBitmap(json.getString("img3")));

                            }
                            //把缓存中的文章数量保存下来
                            CacheUtil.saveDataByCache(NetworkErrorActivity.this,"article_count",jsonList.size() + "");
                        }
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

    //根据url拿到网络图片
    public Bitmap getBitmap(String imgUrl){
        try{
            URL url = new URL(imgUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setConnectTimeout(5);
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

    //通过网络请求拿到相关的建议
    public void getAdvice(final String lang, final int count){
        new Thread(new Runnable() {

            InputStream is = null;
            @Override
            public void run() {
                try{
                    //这是我电脑在宿舍开wifi后的ip
                    String strUrl = "http://192.168.191.1:80/phpWorkspace/ECure-system/public/index.php/advice/get_advice/";

                    URL url = new URL(strUrl + lang + "/" + count);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);
                    int code = con.getResponseCode();

                    if(code == 200){
                        is = con.getInputStream();
                        String jsonStr = StrUtil.stream2String(is);

                        jsonStr = jsonStr.replace("[","").replace("]","");

                        //通过正则表达式把所有的json字符串截出并转换成json对象
                        List<JSONObject> jsonList = RegexUtil.parseJsonStr2JsonObjList(jsonStr);

                        if(jsonList.size() > 0){
                            for(JSONObject json : jsonList){

                                String cacheFileName = "advice" + json.getString("type_id") + json.getString("health_advice_id");

                                CacheUtil.saveDataByCache(NetworkErrorActivity.this,cacheFileName,json.getString("advice"));
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
}
