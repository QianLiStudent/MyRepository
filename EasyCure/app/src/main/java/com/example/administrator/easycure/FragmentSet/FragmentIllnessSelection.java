package com.example.administrator.easycure.FragmentSet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.easycure.activities.DrawerActivity;
import com.example.administrator.easycure.components.Tag;
import com.example.administrator.easycure.interfaces.OnOptListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;

import org.json.JSONObject;

import static java.lang.System.in;

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

    private List<String> mList;

    //装标签的容器（即所有标签的父View）
    private ViewGroup tagContainer;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_illness_selection,container,false);

        init(view);

        initData();
        
        return view;
    }

    public void init(View v){
        tagContainer = (ViewGroup)v.findViewById(R.id.wly_lyt_warp);

        fragment_illness_selection_iv1 = (ImageView)v.findViewById(R.id.fragment_illness_selection_iv1);
        fragment_illness_selection_iv3 = (ImageView)v.findViewById(R.id.fragment_illness_selection_iv3);
        fragment_illness_selection_iv4 = (ImageView)v.findViewById(R.id.fragment_illness_selection_iv4);
        fragment_illness_selection_iv5 = (ImageView)v.findViewById(R.id.fragment_illness_selection_iv5);
        fragment_illness_selection_actv = (AutoCompleteTextView)v.findViewById(R.id.fragment_illness_selection_actv);

        //AutoCompleteTextView设置的是测试数据，这部分数据到时候需要从数据库读
        fragment_illness_selection_actv.setAdapter(new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,mList));

        fragment_illness_selection_iv1.setOnClickListener(this);
        fragment_illness_selection_iv3.setOnClickListener(this);
        fragment_illness_selection_iv4.setOnClickListener(this);
        fragment_illness_selection_iv5.setOnClickListener(this);
    }


    public void initData(){
        readDB();
        readSpAboutDiseaseRecord();

        for(int i = 0;i<mList.size();i++){
            //自定义标签
            Tag tag = new Tag(getContext());

            tag.setTagStatus(0);
            tag.setTagText(mList.get(i));
            tag.setTagSelected(false);
            tag.setTagTextSize(15);

            //设置选中和关闭监听器
            tag.setOnOptListener(this);

            tagContainer.addView(tag);
        }

    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.fragment_illness_selection_iv1:   //打开侧栏
                DrawerActivity.openDrawer();
                break;
            case R.id.fragment_illness_selection_iv3:   //查询输入的内容
                //把查询的东西保存在SP文件中
                SpUtil.addHistoryRecord(getContext(),fragment_illness_selection_actv.getText().toString());
                //去数据库查这个信息相关的病症描述，查到就返回读取到的数据然后转到新界面进行数据展示

                break;
            case R.id.fragment_illness_selection_iv4:   //清空输入框
                fragment_illness_selection_actv.setText("");
                break;
            case R.id.fragment_illness_selection_iv5:   //清空历史记录
                tagContainer.removeAllViews();
                SpUtil.removeAllDiseaseHistoryRecord(getContext());
                break;
        }
    }

    public void readDB(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                HttpURLConnection con = null;

                try {
                    URL url = new URL("http://192.168.191.1:80/phpWorkspace/ECure-system/public/index.php/disease/all_disease");
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");
                    con.setConnectTimeout(5000);
                    int code = con.getResponseCode();
                    if(code == 200){
                        is = con.getInputStream();
                        String jsonStr = StrUtil.stream2String(is);
                        jsonStr = jsonStr.replace("[","").replace("]","");

                        String regex = "\\{.*?\\}";
                        Pattern p = Pattern.compile(regex);
                        Matcher m = p.matcher(jsonStr);

                        while(m.find()){
                            JSONObject json = new JSONObject(m.group());
                            mList.add(json.getString("name"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    try {
                        if(is != null){
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void readSpAboutDiseaseRecord(){
        mList = SpUtil.getAllDiseaseHistoryRecord(getContext());
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
            }else{
                Toast.makeText(getContext(),"最多选" + maxSelected + "个哦",Toast.LENGTH_SHORT).show();
            }
        }else{
            count--;
            t.setTagStatus(0);
            t.setTagSelected(false);
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
        tagContainer.removeView(t);
    }
}
