package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.FragmentSet.FragmentPromotion;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.adapters.FragmentAdviceLvAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/22 0022.
 */

public class FunctionChooseActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private ImageView activity_functionchoose_iv;
    private ListView activity_functionchoose_lv;
    private TextView activity_functionchoose_tv;

    private Intent mIntent;

    private Intent mIntentError = null;

    private List<Map<String,Object>> list = new ArrayList<>();
    private Map<String,Object> map;

    private FragmentAdviceLvAdapter fragmentAdviceLvAdapter;

    //服务器端存的数据
    private int article_count;
    private int advice_count;

    //本地缓存的数据
    private int article_count_cache;
    private int advice_count_cache;

    //开关，当flag为1时则让用户可点击文章或建议，为0不让点击，因为子线程的调用和网络请求都需要消耗一定的时间，
    // 因此只有充分把数据准备好后才让用户可操作
    private int flag = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:     //判断当前是否需要从服务器端爬到新的文章，如果不需要则什么都不做，让用户读缓存即可
                    if(FunctionChooseActivity.this.article_count > FunctionChooseActivity.this.article_count_cache){
                        //进入这里表示需要从服务器爬取数据
                        //需要爬取的记录条数
                        int countForGet = FunctionChooseActivity.this.article_count - FunctionChooseActivity.this.article_count_cache;
                        getArticles(LangGetUtil.langGet(),countForGet);
                    }else{
                        //如果到这里表示当前缓存中的数据和服务器数据是一致的
                        FunctionChooseActivity.this.flag = 1;
                    }
                    break;
                case 1:     //判断当前是否需要从服务器端爬到新的建议，如果不需要则什么都不做，让用户读缓存即可
                    if(FunctionChooseActivity.this.advice_count > FunctionChooseActivity.this.advice_count_cache){
                        //进入这里表示需要从服务器爬取数据
                        int countForGet = FunctionChooseActivity.this.advice_count - FunctionChooseActivity.this.advice_count_cache;
                        getAdvice(LangGetUtil.langGet(),countForGet);
                    }
                    break;
                case 2:
                    FunctionChooseActivity.this.flag = 1;
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functionchoose);

        init();

        initData();
    }

    //在这个界面刚启动的时候就去请求网络，把用到的数据如：文章、各种建议等先爬下来
    @Override
    protected void onStart() {
        super.onStart();
        isNeedToVisitNetwork();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mIntentError = null;
        this.flag = 0;
        isNeedToVisitNetwork();
    }

    //判断文章和建议是否都做了缓存，只要有一种没做缓存则认为全都没做缓存，重新请求一次
    public void isNeedToVisitNetwork(){
        if(CacheUtil.getCacheData(this,"article_count","String") != null && CacheUtil.getCacheData(this,"advice_count","String") != null){
            //进入这里表示有一些文章存在缓存中
            //1、拿到缓存中存的文章和建议的数量
            this.article_count_cache = Integer.parseInt((String)(CacheUtil.getCacheData(this,"article_count","String")));
            this.advice_count_cache = Integer.parseInt((String)(CacheUtil.getCacheData(this,"advice_count","String")));
            //2、请求服务器拿到当前最新的文章数量
            //2.1、首先判断网络是否可用
            if(NetworkUsable.isNetworkConnected(this)){
                //网络可用
                //2.2、获取服务器端存的文章数量和建议数量，然后再handleMessage中进行判断，看看本地需不需要请求最新的文章或者建议
                getDataCount();
            }else{
                //如果网络不可用则直接在下个界面显示缓存数据
                this.flag = 1;
            }
        }else{
            this.article_count_cache = 0;
            this.advice_count_cache = 0;
            //进入这里表示缓存中没有保存文章，需要从网络获取
            //1、首先判断网络是否可用
            if(NetworkUsable.isNetworkConnected(this)){
                //获取服务器端存的文章数量和建议数量，并将数据写入缓存
                getDataCount();
            }else{
                //网络不可用则直接弹出网络异常
                mIntentError = new Intent(this,NetworkErrorActivity.class);
            }
        }

    }

    //获取服务器端存的文章数量和建议数量
    public void getDataCount(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://192.168.191.1:80/phpWorkspace/ECure-system/public/data.json");

                    HttpURLConnection con = (HttpURLConnection)(url.openConnection());

                    con.setRequestMethod("GET");
                    con.setConnectTimeout(2000);
                    int code = con.getResponseCode();

                    if(code == 200){
                        InputStream is = con.getInputStream();

                        String strJson = StrUtil.stream2String(is);

                        JSONObject json = new JSONObject(strJson);

                        //从服务器端拿到最新文章数量
                        FunctionChooseActivity.this.article_count = Integer.parseInt(json.getString("article_count"));
                        //把从服务器端拿到的文章数量保存在缓存中
                        CacheUtil.saveDataByCache(FunctionChooseActivity.this,"article_count",FunctionChooseActivity.this.article_count + "");
                        //从服务器端拿到最新建议数量
                        FunctionChooseActivity.this.advice_count = Integer.parseInt(json.getString("advice_count"));
                        //把从服务器端拿到的建议数量保存在缓存中
                        CacheUtil.saveDataByCache(FunctionChooseActivity.this,"advice_count",FunctionChooseActivity.this.advice_count + "");

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
                                CacheUtil.saveDataByCache(FunctionChooseActivity.this,cacheFileName1,json.getString("title"));
                                CacheUtil.saveDataByCache(FunctionChooseActivity.this,cacheFileName2,json.getString("content"));
                                CacheUtil.saveDataByCache(FunctionChooseActivity.this,cacheFileName3,getBitmap(json.getString("img1")));
                                CacheUtil.saveDataByCache(FunctionChooseActivity.this,cacheFileName4,getBitmap(json.getString("img2")));
                                CacheUtil.saveDataByCache(FunctionChooseActivity.this,cacheFileName5,getBitmap(json.getString("img3")));

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

    //通过网络请求拿到相关的建议，只拿本地缓存没有的数据
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

                        jsonStr = jsonStr.replace("[","").replace("]","").replaceAll("\n","").replaceAll("\r","");

                        //通过正则表达式把所有的json字符串截出并转换成json对象
                        List<JSONObject> jsonList = RegexUtil.parseJsonStr2JsonObjList(jsonStr);


                        if(jsonList.size() > 0){
                            for(JSONObject json : jsonList){

                                String cacheFileName = "advice" + json.getString("type_id") + "_" + json.getString("health_advice_id");

                                CacheUtil.saveDataByCache(FunctionChooseActivity.this,cacheFileName,json.getString("advice"));

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


    public void init(){
        activity_functionchoose_tv = (TextView)findViewById(R.id.activity_functionchoose_tv);
        activity_functionchoose_iv = (ImageView)findViewById(R.id.activity_functionchoose_iv);
        activity_functionchoose_lv = (ListView)findViewById(R.id.activity_functionchoose_lv);

        activity_functionchoose_tv.setOnClickListener(this);
        activity_functionchoose_iv.setOnClickListener(this);
        activity_functionchoose_lv.setOnItemClickListener(this);

    }

    public void initData(){
        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_contact_img);
        map.put("tv",getResources().getString(R.string.contact));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_shop_img);
        map.put("tv",getResources().getString(R.string.disease_diagnosis));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_promotion_search);
        map.put("tv",getResources().getString(R.string.health_information));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_advice_lv_item_img4);
        map.put("tv",getResources().getString(R.string.schedule));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.icon_main_dcotor);
        map.put("tv",getResources().getString(R.string.health_knowledge));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_advice_lv_item_img3);
        map.put("tv",getResources().getString(R.string.where));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_aboutus_communicate);
        map.put("tv",getResources().getString(R.string.about_us));
        list.add(map);

        //这里只是复用而已，因为item里面的格式是一样的，故直接复用advice的适配器
        fragmentAdviceLvAdapter = new FragmentAdviceLvAdapter(this,list);

        activity_functionchoose_lv.setAdapter(fragmentAdviceLvAdapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_functionchoose_tv:
                mIntent = new Intent(this,PersonalActivity.class);
                startActivity(mIntent);
                break;
            case R.id.activity_functionchoose_iv:
                //返回上一个界面
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mIntent = new Intent(this,DrawerActivity.class);

        switch(position){
            case 0:     //contact：备忘录设置
                passItemId(position);
                break;
            case 1:     //shop：商店
                passItemId(position);
                break;
            case 2:     //promotion：健康资讯（文章展示）
                if(flag == 1){  //表示数据准备完毕（要么从网络请求然后写到缓存，要么之前本身就有缓存了）
                    passItemId(position);
                }else{  //表示数据还没有准备好或根本没有数据
                    if(mIntentError != null){
                        //没有数据走这里
                        mIntentError.putExtra("network_error","toFragmentPormotion");
                        startActivity(mIntentError);
                    }else{
                        //有数据但数据还没准备好
                        Toast toast = Toast.makeText(this,getResources().getString(R.string.data_getting),Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
                break;
            case 3:     //schedule：时间计划表
                passItemId(position);
                break;
            case 4:     //advice：关于身体健康的建议
                if(flag == 1){  //表示数据准备完毕（要么从网络请求然后写到缓存，要么之前本身就有缓存了）
                    passItemId(position);
                }else{  //表示数据还没有准备好或根本没有数据
                    if(mIntentError != null){
                        //没有数据走这里
                        mIntentError.putExtra("network_error","toFragmentAdvice");
                        startActivity(mIntentError);
                    }else{
                        //有数据但数据还没准备好
                        Toast toast = Toast.makeText(this,getResources().getString(R.string.data_getting),Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
                break;
            case 5:     //where：定位
                passItemId(position);
                break;
            case 6:     //about us：关于我们
                passItemId(position);
                break;
        }
    }

    public void passItemId(int position){
        mIntent.putExtra("itemId",position);
        startActivity(mIntent);
    }
}
