package com.example.administrator.easycure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.administrator.easycure.JavaBean.Illness;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public class TypeSelectionActivity extends BaseActivity implements View.OnClickListener{

    private ImageView activity_polytype_iv;
    private LinearLayout activity_polytype_list;
    private List<Illness> mList;    //大类下面的各种分型病症，数据来自上个界面

    private List<Integer> typeIdList = new ArrayList<>();   //存放type_id，表示病症的种类，我们会让某种大类病症下的第一个分型默认选中

    private List<View> mViewList = new ArrayList<>();   //存放所有的View

    private List<Illness> mResultList = new ArrayList<>();   //存放具体分型病症

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polytype_list);

        init();

        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void init(){

        activity_polytype_iv = (ImageView)findViewById(R.id.activity_polytype_iv);
        activity_polytype_list = (LinearLayout)findViewById(R.id.activity_polytype_list);

        activity_polytype_iv.setOnClickListener(this);
    }

    public void initData(){
        mIntent = getIntent();

        //大类病症下面的所有分型
        mList = (List<Illness>) mIntent.getSerializableExtra("checkedIllness");

        for(int i = 0;i < mList.size();i++){

            Illness illness = mList.get(i);

            int illnessType = Integer.parseInt(illness.getIllnessType());
            String illnessName = illness.getIllnessName();
            String illnessPolytype = illness.getIllnessPolytype();
            String clinicalFeature = illness.getClinicalFeature();

            //循环创建子控件（CardView）
            View v = LayoutInflater.from(this).inflate(R.layout.case_item,null);

            TextView tv_illnessType = v.findViewById(R.id.case_item_tv);
            TextView tv_illnessName = v.findViewById(R.id.case_item_tv2);
            TextView tv_illnessPolytype = v.findViewById(R.id.case_item_tv4);
            TextView tv_clinicalFeature = v.findViewById(R.id.case_item_tv6);
            RadioButton rb = v.findViewById(R.id.case_item_rb);

            tv_illnessType.setText(illnessType + "");
            tv_illnessName.setText(illnessName);
            tv_illnessPolytype.setText(illnessPolytype);
            tv_clinicalFeature.setText("\u3000\u3000" + clinicalFeature);

            if(!typeIdList.contains(illnessType)){

                TextView tv1 = v.findViewById(R.id.case_item_tv5);
                TextView tv2 = v.findViewById(R.id.case_item_tv6);

                tv1.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);

                typeIdList.add(illnessType);
                rb.setChecked(true);
            }

            mViewList.add(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //tv1是隐藏字段，用来存病症的类型id
                    TextView tv1 = v.findViewById(R.id.case_item_tv);
                    TextView tv2 = v.findViewById(R.id.case_item_tv5);
                    TextView tv3 = v.findViewById(R.id.case_item_tv6);
                    RadioButton rb = v.findViewById(R.id.case_item_rb);

                    int typeId = Integer.parseInt(tv1.getText().toString());
                    //点击卡片把隐藏字段显示，同时按钮选中
                    tv2.setVisibility(View.VISIBLE);
                    tv3.setVisibility(View.VISIBLE);
                    rb.setChecked(true);

                    //由于病症分型一次只能选择一种，因此需要把同组的其他分型反选

                    for(int i = 0;i < mViewList.size();i++){
                        View viewTmp = mViewList.get(i);

                        //只有不是当前点击的item才有必要判断是否相同
                        if(v != viewTmp){
                            TextView tvTmp = (TextView)viewTmp.findViewById(R.id.case_item_tv);
                            int typeIdTmp = Integer.parseInt(tvTmp.getText().toString());

                            if(typeId == typeIdTmp){
                                TextView tv5Tmp = viewTmp.findViewById(R.id.case_item_tv5);
                                TextView tv6Tmp = viewTmp.findViewById(R.id.case_item_tv6);
                                RadioButton rbTmp = viewTmp.findViewById(R.id.case_item_rb);

                                tv5Tmp.setVisibility(View.GONE);
                                tv6Tmp.setVisibility(View.GONE);
                                rbTmp.setChecked(false);
                            }
                        }
                    }
                }
            });

            activity_polytype_list.addView(v);
        }

        //加载只有一个按钮的布局
        View view = LayoutInflater.from(this).inflate(R.layout.case_item_btn,null);

        Button btn = view.findViewById(R.id.activity_polytype_list_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //找出所有被选中的卡片
                for(int i = 0;i < mViewList.size();i++){
                    RadioButton tmpRb = (RadioButton)mViewList.get(i).findViewById(R.id.case_item_rb);

                    if(tmpRb.isChecked()){
                        mResultList.add(mList.get(i));
                    }
                }

                Intent intent = new Intent(TypeSelectionActivity.this,CaseActivity.class);
                intent.putExtra("checkedIllness", (Serializable) mResultList);
                startActivity(intent);
                finish();
            }
        });

        activity_polytype_list.addView(view);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_polytype_iv:
                finish();
                break;

        }
    }
}
