package com.example.administrator.easycure.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.administrator.easycure.JavaBean.SchedulePlan;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.SpUtil;

/**
 * Created by Administrator on 2018/11/2 0002.
 */

public abstract class RadioDialog extends Dialog implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{

    private RadioGroup radioGroup;
    private RadioButton radioButton1,radioButton2;
    private TextView tv;

    private Context context;

    //默认单选按钮2(never)被选中
    private boolean isRb2Checked = true;

    public RadioDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radio_dialog);

        init();

    }

    public void init(){
        radioGroup = (RadioGroup)findViewById(R.id.radio_dialog_rg);
        radioButton1 = (RadioButton)findViewById(R.id.radio_dialog_rb1);
        radioButton2 = (RadioButton)findViewById(R.id.radio_dialog_rb2);
        tv = (TextView)findViewById(R.id.radio_dialog_tv1);

        radioGroup.setOnCheckedChangeListener(this);
        tv.setOnClickListener(this);

        if(SpUtil.getAutomaticUpdateStatus(context).equals(context.getResources().getString(R.string.only_wifi))){
            radioButton1.setChecked(true);  //only WiFi
            isRb2Checked = false;
        }else{
            radioButton2.setChecked(true);  //never
            isRb2Checked = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.radio_dialog_tv1:
                dismiss();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch(checkedId){
            case R.id.radio_dialog_rb1:     //only WiFi
                if(isRb2Checked){
                    //把自动更新功能写入SP中进行保存，同时关闭对话框，并更新原本item出的显示信息
                    SpUtil.saveAutomaticUpdateStatus(context,context.getResources().getString(R.string.only_wifi));
                    setStatus(context.getResources().getString(R.string.only_wifi));
                    dismiss();
                }
                break;
            case R.id.radio_dialog_rb2:     //never
                if(!isRb2Checked){
                    //把自动更新功能写入SP中进行保存，同时关闭对话框，并更新原本item出的显示信息
                    SpUtil.saveAutomaticUpdateStatus(context,context.getResources().getString(R.string.never));
                    setStatus(context.getResources().getString(R.string.never));
                    dismiss();
                }
                break;
        }
    }

    public abstract void setStatus(String status);
}
