package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.JavaBean.Illness;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.adapters.CaseManagementAdapter;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.NetworkUsable;
import com.example.administrator.easycure.utils.RegexUtil;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/23 0023.
 */

public class CaseManagementActivity extends BaseActivity implements View.OnClickListener,
        ExpandableListView.OnGroupClickListener ,ExpandableListView.OnChildClickListener,ExpandableListView.OnGroupExpandListener{

    private ImageView activity_case_management_iv;
    private ExpandableListView activity_case_management_elv;

    private CaseManagementAdapter  caseManagementAdapter;

    private List<String> mParentList = new ArrayList<>();       //扩展列表父Item：显示日期

    private List<List<Map<String,String>>> mChildList = new ArrayList<>();      //扩展列表子item：显示病症名和其具体分型

    private List<Map<String,String>> mGroupList = new ArrayList<>();        //组列表，表示存放一组中所有的item

    private List<Map<String,Object>> mCaseList = new ArrayList<>();     //具体每条Item所对应的所有数据

    private View view = null;      //保存上一次点击的组的item对象
    private boolean isOpen = false;     //保存当前是否有展开的列表

    private int countDown = 1;

    private EasyCureTimer timer;

    private View activity_case_management_v;
    private TextView activity_case_management_tv;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 0:
                    caseManagementAdapter = new CaseManagementAdapter(mParentList,mChildList);
                    activity_case_management_elv.setAdapter(caseManagementAdapter);

                    activity_case_management_elv.setOnGroupClickListener(CaseManagementActivity.this);
                    activity_case_management_elv.setOnChildClickListener(CaseManagementActivity.this);
                    activity_case_management_elv.setOnGroupExpandListener(CaseManagementActivity.this);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_management);

        init();

        initData();

    }

    public void init(){
        activity_case_management_iv = (ImageView)findViewById(R.id.activity_case_management_iv);
        activity_case_management_elv = (ExpandableListView)findViewById(R.id.activity_case_management_elv);

        activity_case_management_v = (View)findViewById(R.id.activity_case_management_v);
        activity_case_management_tv = (TextView)findViewById(R.id.activity_case_management_tv);

        activity_case_management_iv.setOnClickListener(this);
    }

    public void initData(){

        timer = new EasyCureTimer(4000,500);
        timer.start();

        if(NetworkUsable.isNetworkConnected(this)){
            getDataFromServer();
        }else{
            Toast toast = Toast.makeText(this,getResources().getString(R.string.network_anomaly),Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.activity_case_management_iv:
                finish();
                break;
        }
    }

    public void getDataFromServer(){
        new Thread(new Runnable() {

            String urlStr = "http://119.23.208.63/ECure-system/public/index.php/getCase";

            @Override
            public void run() {
                try{
                    URL url = new URL(urlStr);

                    HttpURLConnection con = (HttpURLConnection)url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);

                    String data = "phonenumber=" + URLEncoder.encode(SpUtil.getPhonenumber(CaseManagementActivity.this));

                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    con.setRequestProperty("Content-Length",data.length() + "");

                    con.setDoOutput(true);

                    OutputStream os = con.getOutputStream();

                    os.write(data.getBytes());

                    int code = con.getResponseCode();

                    if(code == 200){
                        String jsonStr = StrUtil.stream2String(con.getInputStream());

                        //去除掉非法字符，这是由于dom造成的
                        if (jsonStr.startsWith("\ufeff")) {
                            jsonStr = jsonStr.substring(1);
                        }

                        JSONObject json = new JSONObject(jsonStr);

                        if(Integer.parseInt(json.getString("code")) == 200){
                            jsonStr = json.getString("data").replace("[","").replace("]","");

                            List<JSONObject> jsons = RegexUtil.parseJsonStr2JsonObjList(jsonStr);

                            for(JSONObject jsonTmp : jsons){
                                String username = jsonTmp.getString("username");
                                String createTime = jsonTmp.getString("createTime");
                                String illnessName = jsonTmp.getString("illnessName");
                                String illnessDescription = jsonTmp.getString("illnessDescription");
                                String illnessPolytype = jsonTmp.getString("illnessPolytype");
                                String clinicalFeature = jsonTmp.getString("clinicalFeature");
                                String drugRecommend = jsonTmp.getString("drugRecommend");

                                Illness illness = new Illness();

                                illness.setIllnessName(illnessName);
                                illness.setIllnessDescription(illnessDescription);
                                illness.setIllnessPolytype(illnessPolytype);
                                illness.setClinicalFeature(clinicalFeature);
                                illness.setDrugRecommend(drugRecommend);

                                Map<String,Object> map1 = new HashMap<>();
                                map1.put("username",username);
                                map1.put("createTime",createTime);
                                map1.put("illness",illness);

                                mCaseList.add(map1);     //把所有病例中的数据全都封装进去，当用户点击的时候就取某一条来显示
                                //----------------------------------------------------------------------------------

                                Map<String,String> map2 = new HashMap<>();
                                map2.put("illnessName",illnessName);
                                map2.put("illnessPolytype",illnessPolytype);

                                String createData = createTime.substring(0,7);      //把具体时间中的“年-月”截取下来

                                if(!mParentList.contains(createData)){
                                    mParentList.add(createData);    //表示新增一个父Item
                                    if(mGroupList.size() > 0){
                                        mChildList.add(mGroupList);     //表示这是一个新的item组，就把之前的保存起来，接下来存一个新组
                                    }
                                    mGroupList = new ArrayList<>(); //表示这是一个新的分组
                                    mGroupList.add(map2);
                                }else{
                                    mGroupList.add(map2);
                                }
                            }

                            mChildList.add(mGroupList);

                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            handler.sendMessageDelayed(msg,10);
                        }else{
                            final String message = json.getString("msg_" + LangGetUtil.langGet());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CaseManagementActivity.this,message,Toast.LENGTH_SHORT);
                                }
                            });
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    //父Item点击事件，效果：点击后展开下拉列表，同时箭头旋转
    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

        if(view != null && view != v){

            isOpen = true;

            //表示这一次点击的和上一次不一样，即点击了不同的item，那么当前item展开，之前item关闭
            //关闭之前的Item
            AnimationSet animationSet1 = new AnimationSet(true);
            RotateAnimation rotateAnimation1 = new RotateAnimation(90,0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation1.setDuration(500);
            animationSet1.setFillEnabled(true);
            animationSet1.setFillAfter(true);
            animationSet1.setFillBefore(false);
            animationSet1.addAnimation(rotateAnimation1);
            ImageView iv1 = view.findViewById(R.id.expang_list_parent_item_iv);
            iv1.startAnimation(animationSet1);

            //展开现在的item
            AnimationSet animationSet2 = new AnimationSet(true);
            RotateAnimation rotateAnimation2 = new RotateAnimation(0,90, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation2.setDuration(500);
            animationSet2.setFillEnabled(true);
            animationSet2.setFillAfter(true);
            animationSet2.setFillBefore(false);
            animationSet2.addAnimation(rotateAnimation2);
            ImageView iv2 = v.findViewById(R.id.expang_list_parent_item_iv);
            iv2.startAnimation(animationSet2);

            view = v;

        }else if(view != null && view == v){
            //上一次点击和这一次是同一个
            if(isOpen){
                AnimationSet animationSet = new AnimationSet(true);
                RotateAnimation rotateAnimation = new RotateAnimation(90,0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                rotateAnimation.setDuration(500);
                animationSet.setFillEnabled(true);
                animationSet.setFillAfter(true);
                animationSet.setFillBefore(false);
                animationSet.addAnimation(rotateAnimation);
                ImageView iv = view.findViewById(R.id.expang_list_parent_item_iv);
                iv.startAnimation(animationSet);

                isOpen = false;
            }else{
                AnimationSet animationSet = new AnimationSet(true);
                RotateAnimation rotateAnimation = new RotateAnimation(0,90, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                rotateAnimation.setDuration(500);
                animationSet.setFillEnabled(true);
                animationSet.setFillAfter(true);
                animationSet.setFillBefore(false);
                animationSet.addAnimation(rotateAnimation);
                ImageView iv = view.findViewById(R.id.expang_list_parent_item_iv);
                iv.startAnimation(animationSet);

                isOpen = true;
            }
        }else if(view == null){
            //展开现在的item
            AnimationSet animationSet = new AnimationSet(true);
            RotateAnimation rotateAnimation = new RotateAnimation(0,90, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation.setDuration(500);
            animationSet.setFillEnabled(true);
            animationSet.setFillAfter(true);
            animationSet.setFillBefore(false);
            animationSet.addAnimation(rotateAnimation);
            ImageView iv = v.findViewById(R.id.expang_list_parent_item_iv);
            iv.startAnimation(animationSet);

            view = v;

            isOpen = true;
        }

        return false;
    }

    //子Item点击事件点击后跳转到病例展示界面，供用户查看具体内容
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        int count = 0;

         for(int i = 0;i < groupPosition;i++){
             count += mChildList.get(i).size();
         }

         count += childPosition;

        Intent intent = new Intent(this,CaseShowActivity.class);

        intent.putExtra("illness",(Serializable) ((Illness)mCaseList.get(count).get("illness")));
        intent.putExtra("username",(String)(mCaseList.get(count).get("username")));
        intent.putExtra("createTime",(String)(mCaseList.get(count).get("createTime")));

        startActivity(intent);

        return true;
    }


    @Override
    public void onGroupExpand(int groupPosition) {
        for(int i = 0;i< caseManagementAdapter.getGroupCount();i++){
            if(groupPosition != i && activity_case_management_elv.isGroupExpanded(groupPosition)){
                activity_case_management_elv.collapseGroup(i);
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
                    activity_case_management_v.setVisibility(View.VISIBLE);
                    activity_case_management_tv.setVisibility(View.VISIBLE);
                    activity_case_management_tv.setText(getResources().getString(R.string.loading) + ".");
                    countDown++;
                    break;
                case 2:
                    activity_case_management_tv.setText(getResources().getString(R.string.loading) + "..");
                    countDown++;
                    break;
                case 3:
                    activity_case_management_tv.setText(getResources().getString(R.string.loading) + "...");
                    countDown++;
                    break;
                case 4:
                    activity_case_management_tv.setText(getResources().getString(R.string.loading) + "....");
                    countDown = 1;
                    break;
            }
        }

        @Override
        public void onFinish() {
            activity_case_management_tv.setVisibility(View.GONE);
            activity_case_management_v.setVisibility(View.GONE);
        }
    }
}
