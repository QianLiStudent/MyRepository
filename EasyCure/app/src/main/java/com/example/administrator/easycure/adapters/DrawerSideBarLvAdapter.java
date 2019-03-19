package com.example.administrator.easycure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.easycure.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/20 0020.
 */

public class DrawerSideBarLvAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,Object>> list;

    public DrawerSideBarLvAdapter(Context context, List<Map<String,Object>> list){
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

            convertView = LayoutInflater.from(context).inflate(R.layout.sidebar_lv_item,null);

            holder.iv = (ImageView)convertView.findViewById(R.id.sidebar_lv_item_iv);
            holder.tv = (TextView) convertView.findViewById(R.id.sidebar_lv_item_tv);

            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }

        holder.iv.setImageResource((Integer)(list.get(position).get("img")));
        holder.tv.setText((String)(list.get(position).get("title")));


        return convertView;
    }

    class Holder{
        //sidebar_lv：ListView中item的左侧图片
        ImageView iv;
        //sidebar_lv：ListView中item的标题
        TextView tv;
    }
}
