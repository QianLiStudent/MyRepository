package com.example.administrator.easycure.FragmentSet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.activities.AdviceActivity;
import com.example.administrator.easycure.activities.DrawerActivity;
import com.example.administrator.easycure.activities.NetworkErrorActivity;
import com.example.administrator.easycure.adapters.FragmentAdviceLvAdapter;
import com.example.administrator.easycure.utils.NetworkUsable;
import com.mob.MobCommunicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/21 0021.
 */

public class FragmentAdvice extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{

    private ImageView fragment_advice_iv;
    private ListView fragment_advice_lv;

    private List<Map<String,Object>> list = new ArrayList<>();
    private Map<String,Object> map;

    private FragmentAdviceLvAdapter fragmentAdviceLvAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advice,container,false);

        init(view);

        initData();

        return view;
    }

    public void init(View v){

        fragment_advice_iv = (ImageView)v.findViewById(R.id.fragment_advice_iv);
        fragment_advice_lv = (ListView)v.findViewById(R.id.fragment_advice_lv);

        fragment_advice_iv.setOnClickListener(this);
        fragment_advice_lv.setOnItemClickListener(this);
    }

    public void initData(){
        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_advice_img);
        map.put("tv",getResources().getString(R.string.medicine_advice));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_advice_lv_item_img1);
        map.put("tv",getResources().getString(R.string.eyes_advice));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_advice_lv_item_img2);
        map.put("tv",getResources().getString(R.string.food_advice));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_advice_lv_item_img3);
        map.put("tv",getResources().getString(R.string.sports_advice));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_advice_lv_item_img4);
        map.put("tv",getResources().getString(R.string.sleep_advice));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_advice_lv_item_img5);
        map.put("tv",getResources().getString(R.string.check_the_body_advice));
        list.add(map);

        map = new HashMap<>();
        map.put("iv",R.mipmap.fragment_promotion_search);
        map.put("tv",getResources().getString(R.string.tea_drinking_advice));
        list.add(map);

        fragmentAdviceLvAdapter = new FragmentAdviceLvAdapter(getContext(),list);

        fragment_advice_lv.setAdapter(fragmentAdviceLvAdapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_advice_iv:
                DrawerActivity.openDrawer();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startGo(position,(String)(list.get(position).get("tv")));
    }

    //跳转界面到具体建议界面
    public void startGo(int position,String title){
        Intent intent = new Intent(getContext(),AdviceActivity.class);
        intent.putExtra("type_id",position + 1);
        intent.putExtra("title",title);
        getContext().startActivity(intent);
    }
}
