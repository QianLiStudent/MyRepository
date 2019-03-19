package com.example.administrator.easycure.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.easycure.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/5 0005.
 */

public class HelpAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,Object>> list;

    public HelpAdapter(Context context, List<Map<String,Object>> list){
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

            convertView = View.inflate(context, R.layout.help_lv_item,null);

            holder.iv = (ImageView)convertView.findViewById(R.id.help_lv_item_iv1);
            holder.tv1 = (TextView)convertView.findViewById(R.id.help_lv_item_tv1);
            holder.tv2 = (TextView)convertView.findViewById(R.id.help_lv_item_tv2);

            convertView.setTag(holder);

        }else{
            holder = (Holder)convertView.getTag();
        }

        holder.iv.setBackgroundResource((Integer)(list.get(position).get("icon")));
        holder.tv1.setText((String)(list.get(position).get("title")));
        holder.tv2.setText((String)(list.get(position).get("msg")));

        return convertView;
    }

    class Holder{
        ImageView iv;
        TextView tv1,tv2;
    }
}
