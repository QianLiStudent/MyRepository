package com.example.administrator.easycure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.easycure.JavaBean.User;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.DBControler;
import com.example.administrator.easycure.utils.SpUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/21 0021.
 */

public class UserSettingLvAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,Object>> list;

    public UserSettingLvAdapter(Context context, List<Map<String,Object>> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            holder = new Holder();

            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_advice_lv_item,null);

            holder.iv = (ImageView)convertView.findViewById(R.id.fragment_advice_lv_iv);
            holder.tv = (TextView)convertView.findViewById(R.id.fragment_advice_lv_tv);
            holder.tv1 = (TextView)convertView.findViewById(R.id.fragment_advice_lv_tv1);


            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        holder.iv.setImageResource((Integer)(list.get(position).get("iv")));
        holder.tv.setText((String)(list.get(position).get("tv")));
        holder.tv1.setText("");

        if(position == 3){
            //loginAccount：是登录的账号（即手机号）
            String loginAccount = (SpUtil.getUserInfo(context)).get(Constant.PHONENUMBER);
            System.out.println(loginAccount);
            User user = DBControler.selectAccountItem(loginAccount);
            if(user != null){
                holder.tv1.setText(user.getSecurityPhoneNumber());
                holder.tv1.setTextColor(context.getResources().getColor(R.color.colorGray));
            }

        }

        return convertView;
    }

    class Holder{
        /**
         * iv：item图标
         * tv：item文字标识
         * tv1：item右侧显示内容
         */
        ImageView iv;
        TextView tv,tv1;
    }
}
