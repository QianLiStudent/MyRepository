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
 * Created by Administrator on 2018/11/2 0002.
 */

public class AboutAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,String>> list;

    public AboutAdapter(Context context, List<Map<String,String>> list){
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
            convertView = View.inflate(context, R.layout.about_lv_item,null);

            holder.tv1 = (TextView)convertView.findViewById(R.id.about_lv_item_tv1);
            holder.tv2 = (TextView)convertView.findViewById(R.id.about_lv_item_tv2);
            holder.iv = (ImageView)convertView.findViewById(R.id.about_lv_item_iv);

            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }

        holder.tv1.setText(list.get(position).get("title"));

        if(position == 0 || position == 4){
            holder.tv2.setText(list.get(position).get("message"));
        }
        if(position != 0){
            holder.iv.setImageResource(R.mipmap.icon_back2);
        }else{
            holder.iv.setImageResource(0);
        }

        return convertView;
    }

    class Holder{
        /**
         * tv1：每条item左侧的标题
         * tv2：每条item左侧的说明或者图标
         * iv：item右侧的箭头图标
         */
        TextView tv1,tv2;
        ImageView iv;
    }
}
