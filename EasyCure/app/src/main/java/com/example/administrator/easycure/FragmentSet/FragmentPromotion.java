package com.example.administrator.easycure.FragmentSet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.activities.ArticleActivity;
import com.example.administrator.easycure.activities.DrawerActivity;
import com.example.administrator.easycure.adapters.FragmentPromotionLvAdapter;
import com.example.administrator.easycure.utils.CacheUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/20 0020.
 */

//Promotion：文章界面
public class FragmentPromotion extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener {

    private List<Map<String,Object>> list = new ArrayList<>();
    private Map<String,Object> map;

    private AutoCompleteTextView fragment_promotion_actv;
    private ImageView fragment_promotion_iv1,fragment_promotion_iv2,fragment_promotion_iv3;
    private ListView fragment_promotion_lv;

    private FragmentPromotionLvAdapter fragmentPromotionLvAdapter;

    private List<String> mTitleList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotion,container,false);

        init(view);

        initData();

        return view;
    }

    public void init(View view){
        //侧滑菜单图标
        fragment_promotion_iv1 = (ImageView)view.findViewById(R.id.fragment_promotion_iv1);
        //搜索按钮
        fragment_promotion_iv2 = (ImageView)view.findViewById(R.id.fragment_promotion_iv2);
        //清除输入的内容图标
        fragment_promotion_iv3 = (ImageView)view.findViewById(R.id.fragment_promotion_iv3);
        //AutoCompleteTextView输入框
        fragment_promotion_actv = (AutoCompleteTextView)view.findViewById(R.id.fragment_promotion_actv);
        //ListView文章列表
        fragment_promotion_lv = (ListView)view.findViewById(R.id.fragment_promotion_lv);

        //侧栏按钮
        fragment_promotion_iv1.setOnClickListener(this);
        //搜索按钮
        fragment_promotion_iv2.setOnClickListener(this);
        //清除输入框
        fragment_promotion_iv3.setOnClickListener(this);

        fragment_promotion_lv.setOnItemClickListener(this);

    }

    public void initData(){

        readCacheAboutArticles();

        fragment_promotion_actv.setAdapter(new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,mTitleList));

        fragmentPromotionLvAdapter = new FragmentPromotionLvAdapter(getContext(),list);

        fragment_promotion_lv.setAdapter(fragmentPromotionLvAdapter);
    }

    //如果能进到这个界面说明缓存中已经有数据了，所以就直接从缓存中读数据
    public void readCacheAboutArticles(){
        int article_count = Integer.parseInt((String)(CacheUtil.getCacheData(getContext(),"article_count","String")));

        for(int i = article_count;i >= 1 ;i--){
            String title = (String)(CacheUtil.getCacheData(getContext(),"article" + i + "_title","String"));
            String content = (String)(CacheUtil.getCacheData(getContext(),"article" + i + "_content","String"));
            content = content.replaceAll("&","\n").replaceAll("@","\u3000\u3000");
            Bitmap img1 = (Bitmap)(CacheUtil.getCacheData(getContext(),"article" + i + "_img1","Bitmap"));
            Bitmap img2 = (Bitmap)(CacheUtil.getCacheData(getContext(),"article" + i + "_img2","Bitmap"));
            Bitmap img3 = (Bitmap)(CacheUtil.getCacheData(getContext(),"article" + i + "_img3","Bitmap"));

            Bitmap[] imgs = {img1,img2,img3};

            map = new HashMap<>();

            map.put("title",title);
            map.put("content",content);
            map.put("img",imgs[(int)(Math.round(Math.random()*2))]);

            list.add(map);

            mTitleList.add(title);
        }
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.fragment_promotion_iv1:   //打开侧栏的按钮
                DrawerActivity.openDrawer();
                break;
            case R.id.fragment_promotion_iv2:   //搜索按钮
                if(mTitleList.contains(fragment_promotion_actv.getText().toString())){
                    //到这里表示搜索框中的标题是存在的，所以可以去查标题对应的内容
                    Intent intent = new Intent(getContext(),ArticleActivity.class);

                    for(int i = 0;i < mTitleList.size();i++){
                        if(fragment_promotion_actv.getText().toString().equals(mTitleList.get(i))){
                            intent.putExtra("article_id",mTitleList.size() - i);
                            break;
                        }
                    }
                    startActivity(intent);
                }else{
                    fragment_promotion_actv.setText("");
                    Toast.makeText(getContext(),getResources().getString(R.string.article_lose),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fragment_promotion_iv3:   //清空输入框按钮
                fragment_promotion_actv.setText("");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(),ArticleActivity.class);

        int article_count = Integer.parseInt((String)(CacheUtil.getCacheData(getContext(),"article_count","String")));

        /**
         * 因为缓存中是以“article(文章id)”的形式保存的，因此只要拿到文章的id就可以了，这里的第二个参数
         * 这么写是因为数据表查询是降序排序，总是把最新的数据置顶
         */

        intent.putExtra("article_id",article_count - position);

        startActivity(intent);
    }
}
