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
 * Created by Administrator on 2018/10/21 0021.
 */

public class FragmentAdviceLvAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,Object>> list;

    public FragmentAdviceLvAdapter(Context context, List<Map<String,Object>> list){
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

            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        holder.iv.setImageResource((Integer)(list.get(position).get("iv")));
        holder.tv.setText((String)(list.get(position).get("tv")));

        return convertView;
    }

    class Holder{
        /**
         * iv：item图标
         * tv：item文字标识
         */
        ImageView iv;
        TextView tv;
    }
}
