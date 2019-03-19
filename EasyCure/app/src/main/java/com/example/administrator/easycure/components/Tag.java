package com.example.administrator.easycure.components;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.interfaces.OnOptListener;

/**
 * Created by Administrator on 2019/3/16 0016.
 */

//自定义标签
public class Tag extends LinearLayout {

    private TextView tv;
    private ImageView iv;

    private int tagStatus;

    private OnOptListener onOptListener = null;

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.example.administrator.easycure";

    public Tag(Context context) {
        this(context,null);
    }

    public Tag(Context context,AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Tag(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(context, R.layout.tag,this);

        tv = (TextView)findViewById(R.id.flowlayout_tv);
        iv = (ImageView)findViewById(R.id.flowlayout_iv);

    }

    public void setTagStatus(int tagStatus){
        this.tagStatus = tagStatus;
    }

    public int getTagStatus(){
        return this.tagStatus;
    }

    //标签设置文字
    public void setTagText(String tagText){
        tv.setText(tagText);
    }

    //获取标签文字
    public String getTagText(){
        return tv.getText().toString();
    }

    //标签设置字体大小
    public void setTagTextSize(int size){
        tv.setTextSize(size);
    }

    //标签设置选中状态
    public void setTagSelected(boolean isSelected){
        if(isSelected){
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundResource(R.drawable.tag_selected);
        }else{
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundResource(R.drawable.tag_unselected);
        }
    }

    //设置监听器
    public void setOnOptListener(OnOptListener onOptListener){
        this.onOptListener = onOptListener;

        tv.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                Tag.this.onOptListener.onChecked(Tag.this);
            }
        });

        iv.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                Tag.this.onOptListener.onClosed(Tag.this);
            }
        });
    }
}
