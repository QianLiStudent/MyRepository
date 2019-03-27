package com.example.administrator.easycure.FragmentSet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.JavaBean.Illness;
import com.example.administrator.easycure.activities.DrawerActivity;
import com.example.administrator.easycure.activities.NetworkErrorActivity;
import com.example.administrator.easycure.activities.TypeSelectionActivity;
import com.example.administrator.easycure.components.Tag;
import com.example.administrator.easycure.interfaces.OnOptListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.NetworkUsable;
import com.example.administrator.easycure.utils.RegexUtil;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/10/25 0025.
 */

//病症选择
public class FragmentIllnessSelection extends Fragment implements View.OnClickListener,OnOptListener {

    //设置标签最多选中数
    public final int maxSelected = 3;
    //设置计数器，判断当前已经选中多少个标签
    private int count = 0;

    /**
     * fragment_illness_selection_iv1：侧栏按钮
     * fragment_illness_selection_iv3：查询按钮
     * fragment_illness_selection_iv4：清空输入按钮
     * fragment_illness_selection_iv5：清空历史记录按钮
     */
    private ImageView fragment_illness_selection_iv1,fragment_illness_selection_iv3,fragment_illness_selection_iv4,fragment_illness_selection_iv5;
    private AutoCompleteTextView fragment_illness_selection_actv;
    private Button fragment_illness_selection_btn;
    private TextView fragment__illness_selection_tv1;   //历史记录和热门搜索共用的文字
    private TextView fragment_illness_selection_tv; //加载中字样

    private View bg_cover;      //背景覆盖蒙版

    private HashSet<String> mHashSet = new HashSet<>();
    private List<String> mQueryList = new ArrayList<>();    //查询列表，只有所有病症的名称，作为AutoCompleteTextView的输入提示文本
    private List<Map<String,String>> mList = new ArrayList<>(); //全局List，存放所有记录的数据
    private List<String> checkedIllnessNameList = new ArrayList<>();    //被选中的标签的名字会保存进行来，用于判断具体选中的是哪种病症
    private List<Illness> mIllnessList = new ArrayList<>();  //专门存放病症（具体到分型）的对象，在下个界面做数据渲染

    private EasyCureTimer timer;    //计时器，用来做正在搜索的效果
    private int countDown = 1;  //计时器及时次数

    //装标签的容器（即所有标签的父View）
    private ViewGroup tagContainer;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 0:
                    Iterator<String> iterator = mHashSet.iterator();

                    while(iterator.hasNext()){
                        mQueryList.add(iterator.next());
                    }

                    fragment_illness_selection_actv.setAdapter(new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,mQueryList));

                    break;

                case 1:

                    fragment_illness_selection_iv3.setEnabled(false);
                    fragment_illness_selection_actv.setEnabled(false);

                    timer = new EasyCureTimer(4000,500);
                    timer.start();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_illness_selection,container,false);

        init(view);

        initData();
        
        return view;
    }

    public void init(View v){
        tagContainer = (ViewGroup)v.findViewById(R.id.wly_lyt_warp);
        bg_cover = (View)v.findViewById(R.id.bg_cover);

        fragment__illness_selection_tv1 = (TextView)v.findViewById(R.id.fragment__illness_selection_tv1);
        fragment_illness_selection_iv1 = (ImageView)v.findViewById(R.id.fragment_illness_selection_iv1);
        fragment_illness_selection_iv3 = (ImageView)v.findViewById(R.id.fragment_illness_selection_iv3);
        fragment_illness_selection_iv4 = (ImageView)v.findViewById(R.id.fragment_illness_selection_iv4);
        fragment_illness_selection_iv5 = (ImageView)v.findViewById(R.id.fragment_illness_selection_iv5);
        fragment_illness_selection_actv = (AutoCompleteTextView)v.findViewById(R.id.fragment_illness_selection_actv);
        fragment_illness_selection_btn = (Button)v.findViewById(R.id.fragment_illness_selection_btn);

        fragment_illness_selection_tv = (TextView)v.findViewById(R.id.fragment_illness_selection_tv);

        fragment_illness_selection_iv1.setOnClickListener(this);
        fragment_illness_selection_iv3.setOnClickListener(this);
        fragment_illness_selection_iv4.setOnClickListener(this);
        fragment_illness_selection_iv5.setOnClickListener(this);
        fragment_illness_selection_btn.setOnClickListener(this);
    }

    public void initData(){

        if(NetworkUsable.isNetworkConnected(getContext())){
            getIllnessData(LangGetUtil.langGet(),0);
        }else{
            Toast toast = Toast.makeText(getContext(),getResources().getString(R.string.network_anomaly),Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

        //读取历史搜索记录
        List<String> list = readSpAboutDiseaseRecord();

        if(list.size() > 0){
            //表示有历史记录
            fillTags(list);
        }else{
            //表示没有历史记录，接下来将推荐热门搜索的内容

            String tag1 = getResources().getString(R.string.tag1);
            String tag2 = getResources().getString(R.string.tag2);
            String tag3 = getResources().getString(R.string.tag3);
            String tag4 = getResources().getString(R.string.tag4);
            String tag5 = getResources().getString(R.string.tag5);
            String tag6 = getResources().getString(R.string.tag6);
            String tag7 = getResources().getString(R.string.tag7);
            String tag8 = getResources().getString(R.string.tag8);

            List<String> autoTags = Arrays.asList(tag1,tag2,tag3,tag4,tag5,tag6,tag7,tag8);

            fillTags(autoTags);

            fragment__illness_selection_tv1.setText(getResources().getString(R.string.popular_searches));
            fragment_illness_selection_iv5.setVisibility(View.GONE);
        }
    }

    public void fillTags(List<String> tags){

        for(int i = 0;i<tags.size();i++){
            //自定义标签
            Tag tag = new Tag(getContext());

            tag.setTagStatus(0);
            tag.setTagText(tags.get(i));
            tag.setTagSelected(false);
            tag.setTagTextSize(15);

            //设置选中和关闭监听器
            tag.setOnOptListener(FragmentIllnessSelection.this);

            tagContainer.addView(tag);
        }
    }

    public void getIllnessData(final String lang,final int count){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String strUrl = "http://119.23.208.63/ECure-system/public/index.php/get_diseases/";

                InputStream is;
                try{
                    URL url = new URL(strUrl);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();

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

                        if (jsonStr.startsWith("\ufeff")) {
                            jsonStr = jsonStr.substring(1);
                        }

                        jsonStr = jsonStr.replace("[","").replace("]","");

                        List<JSONObject> jsonList = RegexUtil.parseJsonStr2JsonObjList(jsonStr);

                        for(int i = 0;i <jsonList.size();i++){
                            JSONObject json = jsonList.get(i);

                            //这个HashSet集合只用来保存病症的名字，然而在查询过程中是把所有数据查出来，因此会出现有多条同病症名的数据，这是用Set集合可以去掉重复元素
                            mHashSet.add(json.getString("illness_name"));

                            //这里需要拿到其他的数据加以保存，因为用户搜索选中或查询某些病症的时候在下一个界面需要用到这些数据
                            Map<String,String> map = new HashMap<>();

                            map.put("illness_type",json.getString("illness_type"));
                            map.put("illness_name",json.getString("illness_name"));
                            map.put("illness_description",json.getString("illness_description"));
                            map.put("illness_polytype",json.getString("illness_polytype"));
                            map.put("clinical_feature",json.getString("clinical_feature"));
                            map.put("drug_recommend",json.getString("drug_recommend"));

                            mList.add(map);
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

    public void onClick(View v){
        switch(v.getId()){
            case R.id.fragment_illness_selection_iv1:   //打开侧栏
                DrawerActivity.openDrawer();
                break;
            case R.id.fragment_illness_selection_iv3:   //查询输入的内容，只考虑用户填写在输入框的内容，标签的内容有特殊按钮
                //用户输入的内容
                String str = fragment_illness_selection_actv.getText().toString();

                if(str.equals("")){
                    Toast.makeText(getContext(),getResources().getString(R.string.not_empty),Toast.LENGTH_SHORT).show();
                }else{
                    int index = -1; //索引，用来判断用户输入的是否能在我们提供的病症中找到

                    for(int i = 0;i < mQueryList.size();i++){
                        if(str.equals(mQueryList.get(i))){
                            //进入这里表示用户输入的病症找到了
                            //如果用户在输入框自己输入病症名称，然后通过点击搜索查找，则只查找该病症的相关信息，不考虑标签的选中状态

                            for(Map<String,String> map : mList){
                                //一个map对象表示存放了一条记录
                                if(str.equals(map.get("illness_name"))){

                                    Illness illness = new Illness();

                                    illness.setIllnessType(map.get("illness_type"));
                                    illness.setIllnessName(map.get("illness_name"));
                                    illness.setIllnessDescription(map.get("illness_description"));
                                    illness.setIllnessPolytype(map.get("illness_polytype"));
                                    illness.setClinicalFeature(map.get("clinical_feature"));
                                    illness.setDrugRecommend(map.get("drug_recommend"));

                                    mIllnessList.add(illness);
                                }
                            }

                            index = 0;

                            List<String> recordLsit = SpUtil.getAllDiseaseHistoryRecord(getContext());
                            int flag = 0;   //标识，如果为0表示，如果为1表示
                            for(String record : recordLsit){
                                if(str.equals(record)){
                                    //表示用户输入的内容已经存在历史记录中了
                                    flag = 1;
                                    break;
                                }
                            }
                            if(flag == 0){
                                //还没有写入sp文件，把用户输入写入sp文件中，然后跳转界面显示相关数据
                                SpUtil.addHistoryRecord(getContext(),str);
                            }

                            //关闭软键盘
                            closeSoftKeyBoard();

                            Message msg = handler.obtainMessage();
                            msg.what = 1;
                            handler.sendMessage(msg);
                            break;
                        }
                    }
                    if(index == -1){
                        //进入这里表示用户数据找不到（不是我们提供的病症之一）
                        Toast toast = Toast.makeText(getContext(),getResources().getString(R.string.word_poor),Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
                break;
            case R.id.fragment_illness_selection_iv4:   //清空输入框
                fragment_illness_selection_actv.setText("");
                break;
            case R.id.fragment_illness_selection_iv5:   //清空历史记录
                tagContainer.removeAllViews();
                SpUtil.removeAllDiseaseHistoryRecord(getContext());
                break;
            case R.id.fragment_illness_selection_btn:
                if(checkedIllnessNameList.size() > 0){

                    for(String illnessName : checkedIllnessNameList){
                        for(Map<String,String> map : mList){

                            if(illnessName.equals(map.get("illness_name"))){

                                Illness illness = new Illness();

                                illness.setIllnessType(map.get("illness_type"));
                                illness.setIllnessName(map.get("illness_name"));
                                illness.setIllnessDescription(map.get("illness_description"));
                                illness.setIllnessPolytype(map.get("illness_polytype"));
                                illness.setClinicalFeature(map.get("clinical_feature"));
                                illness.setDrugRecommend(map.get("drug_recommend"));

                                mIllnessList.add(illness);
                            }
                        }
                    }

                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }else{
                    Toast toast = Toast.makeText(getContext(),getResources().getString(R.string.select_tags),Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                break;
        }
    }

    //读历史搜索记录
    public List<String> readSpAboutDiseaseRecord(){
        return SpUtil.getAllDiseaseHistoryRecord(getContext());
    }

    //点击标签的回调
    @Override
    public void onChecked(Object obj) {
        Tag t = (Tag)obj;

        if(t.getTagStatus() == 0){
            if(count < maxSelected){
                count++;
                t.setTagStatus(1);
                t.setTagSelected(true);
                //选中的标签的名字会加入集合
                checkedIllnessNameList.add(t.getTagText());
            }else{
                Toast.makeText(getContext(),"最多选" + maxSelected + "个哦",Toast.LENGTH_SHORT).show();
            }
        }else{
            count--;
            t.setTagStatus(0);
            t.setTagSelected(false);

            checkedIllnessNameList.remove(t.getTagText());
        }
    }

    //点击标签右上角的x可以关闭该标签
    @Override
    public void onClosed(Object obj) {
        Tag t = (Tag)obj;
        SpUtil.removeDiseaseHistoryRecord(getContext(),t.getTagText());
        if(t.getTagStatus() == 1){
            count--;
        }

        //如果标签被选中了就会加入到选中的集合中，但是这时候我们这个操作是想删除标签，
        // 这时候就需要判断这个标签是否选中，如果是就需要删除选中集合中的该标签名字
        for(String illnessName : checkedIllnessNameList){
            if(illnessName.equals(t.getTagText())){
                checkedIllnessNameList.remove(t.getTagText());
                break;
            }
        }

        tagContainer.removeView(t);
    }

    //关闭软键盘
    public void closeSoftKeyBoard(){
        //判断软键盘是否打开
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if(imm.isActive() && getActivity().getCurrentFocus()!=null){
            //拿到view的token 不为空
            if (getActivity().getCurrentFocus().getWindowToken() != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    class EasyCureTimer extends CountDownTimer {

        public EasyCureTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            switch(countDown){
                case 1:
                    bg_cover.setVisibility(View.VISIBLE);
                    fragment_illness_selection_tv.setVisibility(View.VISIBLE);
                    fragment_illness_selection_tv.setText(getResources().getString(R.string.searching) + ".");
                    countDown++;
                    break;
                case 2:
                    fragment_illness_selection_tv.setText(getResources().getString(R.string.searching) + "..");
                    countDown++;
                    break;
                case 3:
                    fragment_illness_selection_tv.setText(getResources().getString(R.string.searching) + "...");
                    countDown++;
                    break;
                case 4:
                    fragment_illness_selection_tv.setText(getResources().getString(R.string.searching) + "....");
                    countDown = 1;
                    break;
            }
        }

        @Override
        public void onFinish() {
            fragment_illness_selection_tv.setVisibility(View.GONE);
            bg_cover.setVisibility(View.GONE);
            //界面跳转
            Intent intent = new Intent(getContext(),TypeSelectionActivity.class);
            //把用户输入的病症对应的数据拿到并封装后传到目标界面
            intent.putExtra("checkedIllness",(Serializable)mIllnessList);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
