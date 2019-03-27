package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.administrator.easycure.FragmentSet.FragmentAdvice;
import com.example.administrator.easycure.FragmentSet.FragmentPharmapp;
import com.example.administrator.easycure.FragmentSet.FragmentPromotion;
import com.example.administrator.easycure.FragmentSet.FragmentSchedule;
import com.example.administrator.easycure.FragmentSet.FragmentIllnessSelection;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.adapters.DrawerSideBarLvAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/20 0020.
 */

public class DrawerActivity extends FragmentActivity implements AdapterView.OnItemClickListener {

    private static DrawerLayout activity_drawer;
    private static ListView activity_drawer_lv;

    public List<Map<String,Object>> list = new ArrayList<>();
    public Map<String,Object> map;

    private Intent intent;

    private int fragmentFunctionChooseItemId;

    private FragmentManager fragmentManager;
    private FragmentPharmapp fragmentPharmapp;

    private DrawerSideBarLvAdapter drawerSideBarLvAdapter;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_drawer);

        init();

        initData();

    }

    public void init(){
        activity_drawer = (DrawerLayout)findViewById(R.id.activity_drawer);
        activity_drawer_lv = (ListView)findViewById(R.id.activity_drawer_lv);

        intent = getIntent();
        fragmentFunctionChooseItemId = intent.getIntExtra("itemId",6);

        fragmentManager = getSupportFragmentManager();


        switch(fragmentFunctionChooseItemId){
            case 0:     //contact
                fragmentPharmapp = FragmentPharmapp.getInstance(1);
                changeFragment(fragmentPharmapp);
                break;
            case 1:     //shop
                FragmentIllnessSelection fragmentIllnessSelection = new FragmentIllnessSelection();
                changeFragment(fragmentIllnessSelection);
                break;
            case 2:     //promotion
                FragmentPromotion fragmentPromotion = new FragmentPromotion();
                changeFragment(fragmentPromotion);
                break;
            case 3:     //schedule
                FragmentSchedule fragmentSchedule = new FragmentSchedule();
                changeFragment(fragmentSchedule);
                break;
            case 4:     //advice
                FragmentAdvice fragmentAdvice = new FragmentAdvice();
                changeFragment(fragmentAdvice);
                break;
            case 5:     //where
                fragmentPharmapp = FragmentPharmapp.getInstance(2);
                changeFragment(fragmentPharmapp);
                break;
            case 6:     //about us
                fragmentPharmapp = FragmentPharmapp.getInstance(0);
                changeFragment(fragmentPharmapp);
                break;
        }

    }

    public void changeFragment(Fragment fragment){
        fragmentManager.beginTransaction().replace(R.id.activity_drawer_fl,fragment).commit();
    }

    public static void openDrawer(){
        activity_drawer.openDrawer(activity_drawer_lv);
    }

    //侧栏item的点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //先判断用户点击的侧栏列表中的item是否正是当前正在显示中的界面，是的话直接关闭侧栏
        isSamePage(position);

       switch(position){
           case 0:      //MAIN：主界面
               intent = new Intent(this,MainActivity.class);
               startActivity(intent);
               break;
           case 1:      //CONTACT：备忘录设置
               fragmentPharmapp = FragmentPharmapp.getInstance(1);

               changeFragment(fragmentPharmapp);
               break;
           case 2:      //SHOP：商店
               FragmentIllnessSelection fragmentIllnessSelection = new FragmentIllnessSelection();

               changeFragment(fragmentIllnessSelection);
               break;
           case 3:      //Schedule：备忘录计划表
               FragmentSchedule fragmentSchedule = new FragmentSchedule();

               changeFragment(fragmentSchedule);
               break;
           case 4:      //ADVICE：健康常识
               FragmentAdvice fragmentAdvice = new FragmentAdvice();

               changeFragment(fragmentAdvice);
               break;
           case 5:      //PROMOTION：健康资讯
               FragmentPromotion fragmentPromotion = new FragmentPromotion();

               changeFragment(fragmentPromotion);
               break;
           case 6:      //ABOUT US：关于我们
               fragmentPharmapp = FragmentPharmapp.getInstance(0);

               changeFragment(fragmentPharmapp);
               break;
       }
        //切换主界面内容后关闭侧栏
        activity_drawer.closeDrawer(activity_drawer_lv);
    }

    public void isSamePage(int id){
        if(fragmentFunctionChooseItemId == id){
            activity_drawer.closeDrawer(activity_drawer_lv);
        }
    }

    public void initData(){

        map = new HashMap<>();
        map.put("img",R.mipmap.icon_main_cross);
        map.put("title",getResources().getString(R.string.main));
        list.add(map);

        map = new HashMap<>();
        map.put("img",R.mipmap.fragment_contact_img);
        map.put("title",getResources().getString(R.string.contact));
        list.add(map);

        map = new HashMap<>();
        map.put("img",R.mipmap.fragment_shop_img);
        map.put("title",getResources().getString(R.string.disease_diagnosis));
        list.add(map);

        map = new HashMap<>();
        map.put("img",R.mipmap.icon_main_dcotor);
        map.put("title",getResources().getString(R.string.schedule));
        list.add(map);

        map = new HashMap<>();
        map.put("img",R.mipmap.fragment_advice_img);
        map.put("title",getResources().getString(R.string.health_knowledge));
        list.add(map);

        map = new HashMap<>();
        map.put("img",R.mipmap.fragment_promotion_search);
        map.put("title",getResources().getString(R.string.health_information));
        list.add(map);

        map = new HashMap<>();
        map.put("img",R.mipmap.fragment_aboutus_communicate);
        map.put("title",getResources().getString(R.string.about_us));
        list.add(map);

        drawerSideBarLvAdapter = new DrawerSideBarLvAdapter(getApplicationContext(),list);

        activity_drawer_lv.setAdapter(drawerSideBarLvAdapter);

        activity_drawer_lv.setOnItemClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}

