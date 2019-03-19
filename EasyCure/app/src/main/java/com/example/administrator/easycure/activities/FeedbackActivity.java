package com.example.administrator.easycure.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/5 0005.
 */

public class FeedbackActivity extends BaseActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    private ImageView activity_feedback_iv1;
    private TextView activity_feedback_tv1,activity_feedback_tv2;
    private EditText activity_feedback_et;
    private CheckBox activity_feedback_cb1,activity_feedback_cb2,activity_feedback_cb3,activity_feedback_cb4;

    //list保存4个checkbox的选中情况，true表示选中，false表示未选中
    private List<Boolean> list = new ArrayList<>();
    //mList保存checkbox的具体选中项，若第一个checkbox被选中则第一个元素保存为0，以此类推
    private List<Integer> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        init();

        initData();
    }

    public void init(){
        activity_feedback_iv1 = (ImageView)findViewById(R.id.activity_feedback_iv1);
        activity_feedback_tv1 = (TextView)findViewById(R.id.activity_feedback_tv1);
        activity_feedback_tv2 = (TextView)findViewById(R.id.activity_feedback_tv2);
        activity_feedback_et = (EditText)findViewById(R.id.activity_feedback_et);
        activity_feedback_cb1 = (CheckBox)findViewById(R.id.activity_feedback_cb1);
        activity_feedback_cb2 = (CheckBox)findViewById(R.id.activity_feedback_cb2);
        activity_feedback_cb3 = (CheckBox)findViewById(R.id.activity_feedback_cb3);
        activity_feedback_cb4 = (CheckBox)findViewById(R.id.activity_feedback_cb4);

        activity_feedback_iv1.setOnClickListener(this);
        activity_feedback_tv1.setOnClickListener(this);
        activity_feedback_cb1.setOnCheckedChangeListener(this);
        activity_feedback_cb2.setOnCheckedChangeListener(this);
        activity_feedback_cb3.setOnCheckedChangeListener(this);
        activity_feedback_cb4.setOnCheckedChangeListener(this);

        activity_feedback_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activity_feedback_tv2.setText(activity_feedback_et.getText().toString().length() + "/240");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void initData(){
        for(int i = 0;i < 4;i++){
            list.add(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_feedback_iv1:        //返回上一页
                //关闭软键盘
                closeSoftKeyBoard();
                finish();
                break;
            case R.id.activity_feedback_tv1:        //提交反馈信息
                //判断是否至少有一个CheckBox被选中，再判断输入框是否输入大于10个字符
                //把选中的CheckBox的相关内容上传到服务器端的数据库，并让相关的条目+1，表示多一个用户反馈这个内容

                int count = 0;
                for(int i = 0;i<list.size();i++){
                    if(!list.get(i)){
                        count++;
                    }else{
                        mList.add(i);
                    }
                }
                //count为4表示4个checkbox一个都没有选，反馈问题点至少要选一个
                if(count == 4){
                    Toast toast = Toast.makeText(this,getResources().getString(R.string.choose_feedback_issue),Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else{
                    //输入框中输入的字符串长度，如果checkbox至少选择一个了，那么就会判断输入框的字符长度是否大于10
                    int len = activity_feedback_et.getText().toString().trim().length();
                    if(len >= 10){
                        //如果checkbox有选择至少一个，输入框也输入合法，就维护到数据库中
                        System.out.println("可以维护到数据库了");
                    }else{
                        //输入框输入的数据长度小于10，提示至少输入10个字符
                        Toast toast = Toast.makeText(this,getResources().getString(R.string.input_length),Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()){
            case R.id.activity_feedback_cb1:
                list.set(0,isChecked);
                break;
            case R.id.activity_feedback_cb2:
                list.set(1,isChecked);
                break;
            case R.id.activity_feedback_cb3:
                list.set(2,isChecked);
                break;
            case R.id.activity_feedback_cb4:
                list.set(3,isChecked);
                break;
        }
    }

    //关闭软键盘
    public void closeSoftKeyBoard(){
        //判断软键盘是否打开
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if(imm.isActive() && getCurrentFocus()!=null){
            //拿到view的token 不为空
            if (getCurrentFocus().getWindowToken() != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                System.out.println("隐藏软键盘");
            }
        }
    }

}
