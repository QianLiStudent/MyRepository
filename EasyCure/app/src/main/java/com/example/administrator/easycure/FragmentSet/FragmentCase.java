package com.example.administrator.easycure.FragmentSet;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.JavaBean.Illness;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.activities.TypeSelectionActivity;
import com.example.administrator.easycure.utils.GetCalendar;
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.NetworkUsable;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/23 0023.
 */

public class FragmentCase extends Fragment implements View.OnClickListener{

    private TextView activity_case_id;
    private TextView activity_case_illness_name;
    private TextView activity_case_illness_description;
    private TextView activity_case_illness_polytype;
    private TextView activity_case_clinical_feature;
    private TextView activity_case_drug_recommend;
    private TextView activity_case_create_time;

    private Button activity_case_save_btn;
    private Button activity_case_not_save_btn;

    private View fragment_case_vShow;   //蒙版
    private TextView fragment_case_tvShow;

    private Illness illness;

    private int fragmentId = 0; //用来保存fragment的索引，表示是第几个fragment，主要用来作为病例保存状态的存储key

    private int count = 0;      //用来保存fragment的数量

    private int restCount = 0;      //用来保存剩余待处理的病例

    private Map<String,String> map = new HashMap<>();

    private String message = "";

    private EasyCureTimer timer;
    private int countDown = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 0:

                    SpUtil.saveCaseUploadStatusTmp(getContext(),"fragment" + fragmentId,true);
                    //请求成功，病例成功写到服务器
                    Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                    activity_case_save_btn.setBackgroundColor(getResources().getColor(R.color.colorGray1));
                    activity_case_not_save_btn.setBackgroundColor(getResources().getColor(R.color.colorGray1));
                    activity_case_save_btn.setEnabled(false);
                    activity_case_not_save_btn.setEnabled(false);

                    break;
                case 1:
                    //请求失败
                    Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_case,container,false);

        init(v);

        Bundle bundle = getArguments();

        illness = (Illness)bundle.getSerializable("illness");

        fragmentId = bundle.getInt("fragmentId");

        count = bundle.getInt("count");

        restCount = count;

        boolean statusTmp = SpUtil.getCaseUploadStatusTmp(getContext(),"fragment" + fragmentId);

        if(statusTmp) {
            //之前操作过，选择保存或不保存不重要，只要之前做过一次选择就标识成已操作
            activity_case_save_btn.setBackgroundColor(getResources().getColor(R.color.colorGray1));
            activity_case_not_save_btn.setBackgroundColor(getResources().getColor(R.color.colorGray1));
            activity_case_save_btn.setClickable(false);
            activity_case_not_save_btn.setClickable(false);
        }

        initData();

        return v;

    }

    public void init(View v){

        activity_case_id = (TextView)v.findViewById(R.id.activity_case_id);
        activity_case_illness_name = (TextView)v.findViewById(R.id.activity_case_illness_name);
        activity_case_illness_description = (TextView)v.findViewById(R.id.activity_case_illness_description);
        activity_case_illness_polytype = (TextView)v.findViewById(R.id.activity_case_illness_polytype);
        activity_case_clinical_feature = (TextView)v.findViewById(R.id.activity_case_clinical_feature);
        activity_case_drug_recommend = (TextView)v.findViewById(R.id.activity_case_drug_recommend);
        activity_case_create_time = (TextView)v.findViewById(R.id.activity_case_create_time);

        activity_case_save_btn = (Button)v.findViewById(R.id.activity_case_save_btn);
        activity_case_not_save_btn = (Button)v.findViewById(R.id.activity_case_not_save_btn);

        fragment_case_vShow = (View)v.findViewById(R.id.fragment_case_vShow);
        fragment_case_tvShow = (TextView)v.findViewById(R.id.fragment_case_tvShow);

        activity_case_save_btn.setOnClickListener(this);
        activity_case_not_save_btn.setOnClickListener(this);
    }

    public void initData(){

        activity_case_id.setText(SpUtil.getUsername(getContext()));

        activity_case_illness_name.setText(illness.getIllnessName());
        activity_case_illness_description.setText("\u3000\u3000" + illness.getIllnessDescription());
        activity_case_illness_polytype.setText(illness.getIllnessPolytype());
        activity_case_clinical_feature .setText("\u3000\u3000" + illness.getClinicalFeature());

        String[] plans = illness.getDrugRecommend().split("/");

        String drugRecommend = "";

        for(int i = 0;i < plans.length;i++){
            drugRecommend += "\u3000\u3000" + getResources().getString(R.string.illness_plan) + (i + 1) + ": " + plans[i] + "\n";
        }

        activity_case_drug_recommend.setText(drugRecommend);

        String createTime = GetCalendar.getStandardTime();
        activity_case_create_time.setText(createTime);

        map.put("phonenumber", SpUtil.getPhonenumber(getContext()));
        map.put("username",SpUtil.getUsername(getContext()));
        map.put("illnessName",illness.getIllnessName());
        map.put("illnessDescription",illness.getIllnessDescription());
        map.put("illnessPolytype",illness.getIllnessPolytype());
        map.put("clinicalFeature",illness.getClinicalFeature());
        map.put("drugRecommend",illness.getDrugRecommend());
        map.put("createTime",createTime);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_case_save_btn:
                if(NetworkUsable.isNetworkConnected(getContext())){
                    timer = new EasyCureTimer(4000,500);
                    timer.start();
                    //点击按钮可保存病例，实际上这里应该将病例中的信息写入数据库然后进行保存
                    saveCaseInfoOnServer(map);
                }else{
                    Toast.makeText(getContext(),getResources().getString(R.string.network_anomaly),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_case_not_save_btn:
                SpUtil.saveCaseUploadStatusTmp(getContext(),"fragment" + fragmentId,true);
                activity_case_save_btn.setBackgroundColor(getResources().getColor(R.color.colorGray1));
                activity_case_not_save_btn.setBackgroundColor(getResources().getColor(R.color.colorGray1));
                activity_case_save_btn.setClickable(false);
                activity_case_not_save_btn.setClickable(false);

                boolean[] operatingStatus = SpUtil.getAllCaseOperatingStatus(getContext());

                int i;

                restCount = count;

                for(i = 0;i < count;i++){
                    if(operatingStatus[i]){
                        restCount--;
                    }
                }
                if(restCount > 0){
                    //表示还有未处理的病例
                    if(LangGetUtil.langGet().equals("zh")){
                        Toast.makeText(getContext(),"还有" + restCount + "张" + getResources().getString(R.string.resetCase),Toast.LENGTH_SHORT).show();
                    }else if(LangGetUtil.langGet().equals("en")){
                        if(restCount > 1){
                            Toast.makeText(getContext(),restCount + " cases" + getResources().getString(R.string.resetCase),Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(),"1 case" + getResources().getString(R.string.resetCase),Toast.LENGTH_SHORT).show();
                        }

                    }
                }else{
                    SpUtil.removeAllCaseOperatingRecord(getContext());
                    //表示没有剩余未处理的病例了，就关闭界面
                    getActivity().finish();
                }
                break;
        }
    }

    //上传病例
    public void saveCaseInfoOnServer(final Map<String,String> map){

        SpUtil.saveCaseUploadStatusTmp(getContext(),"fragment" + fragmentId,true);

        new Thread(new Runnable() {

            String urlStr = "http://119.23.208.63/ECure-system/public/index.php/saveCase";

            InputStream is;
            @Override
            public void run() {
                try{
                    URL url = new URL(urlStr);

                    HttpURLConnection con =(HttpURLConnection)url.openConnection();

                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);

                    String phonenumber = map.get("phonenumber");
                    String username = map.get("username");
                    String illnessName = map.get("illnessName");
                    String illnessDescription = map.get("illnessDescription");
                    String illnessPolytype = map.get("illnessPolytype");
                    String clinicalFeature = map.get("clinicalFeature");
                    String drugRecommend = map.get("drugRecommend");
                    String createTime = map.get("createTime");

                    String data = "phonenumber=" + URLEncoder.encode(phonenumber) +
                            "&username=" + URLEncoder.encode(username) +
                            "&illnessName=" + URLEncoder.encode(illnessName) +
                            "&illnessDescription=" + URLEncoder.encode(illnessDescription) +
                            "&illnessPolytype=" + URLEncoder.encode(illnessPolytype) +
                            "&clinicalFeature=" + URLEncoder.encode(clinicalFeature) +
                            "&drugRecommend=" + URLEncoder.encode(drugRecommend) +
                            "&createTime=" + URLEncoder.encode(createTime);

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

                        JSONObject json = new JSONObject(jsonStr);

                        int statusCode = Integer.parseInt(json.getString("code"));
                        message = json.getString("msg_" + LangGetUtil.langGet());
                        Message msg = handler.obtainMessage();

                        if(statusCode == 200){
                            //请求成功
                            msg.what = 0;
                        }else if(statusCode == 500){
                            //请求失败
                            msg.what = 1;
                        }
                        handler.sendMessageDelayed(msg,10);
                    }
                }catch (Exception e){
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
                    fragment_case_vShow.setVisibility(View.VISIBLE);
                    fragment_case_tvShow.setVisibility(View.VISIBLE);
                    fragment_case_tvShow.setText(getResources().getString(R.string.uploading) + ".");
                    countDown++;
                    break;
                case 2:
                    fragment_case_tvShow.setText(getResources().getString(R.string.uploading) + "..");
                    countDown++;
                    break;
                case 3:
                    fragment_case_tvShow.setText(getResources().getString(R.string.uploading) + "...");
                    countDown++;
                    break;
                case 4:
                    fragment_case_tvShow.setText(getResources().getString(R.string.uploading) + "....");
                    countDown = 1;
                    break;
            }
        }

        @Override
        public void onFinish() {
            fragment_case_tvShow.setVisibility(View.GONE);
            fragment_case_vShow.setVisibility(View.GONE);

            boolean[] operatingStatus = SpUtil.getAllCaseOperatingStatus(getContext());

            int i;

            restCount = count;

            for(i = 0;i < count;i++){
                if(operatingStatus[i]){
                    restCount--;
                }
            }
            if(restCount > 0){
                //表示还有未处理的病例
                if(LangGetUtil.langGet().equals("zh")){
                    Toast.makeText(getContext(),"还有" + restCount + "张" + getResources().getString(R.string.resetCase),Toast.LENGTH_SHORT).show();
                }else if(LangGetUtil.langGet().equals("en")){
                    if(restCount > 1){
                        Toast.makeText(getContext(),restCount + " cases" + getResources().getString(R.string.resetCase),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"1 case" + getResources().getString(R.string.resetCase),Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                SpUtil.removeAllCaseOperatingRecord(getContext());
                //表示没有剩余未处理的病例了，就关闭界面
                getActivity().finish();
            }
        }
    }
}
