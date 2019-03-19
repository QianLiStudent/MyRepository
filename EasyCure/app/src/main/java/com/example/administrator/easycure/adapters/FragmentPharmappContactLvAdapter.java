package com.example.administrator.easycure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.easycure.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/21 0021.
 */

public class FragmentPharmappContactLvAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,String>> list;

    public FragmentPharmappContactLvAdapter(Context context,List<Map<String,String>> list){
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

            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_pharmapp_contact_lv_item,null);

            holder.title = (TextView)convertView.findViewById(R.id.fragment_pharmapp_contact_lv_tv1);
            holder.time = (TextView)convertView.findViewById(R.id.fragment_pharmapp_contact_lv_tv2);

            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }

        holder.title.setText(list.get(position).get("title"));
        holder.time.setText(list.get(position).get("time"));
        return convertView;
    }

    class Holder{
        TextView title,time;
    }
}
