package com.example.administrator.easycure.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.easycure.JavaBean.VersionInfo;
import com.example.administrator.easycure.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/3 0003.
 */

public class VersionInformationAdapter extends BaseAdapter {

    private Context context;
    private List<VersionInfo> list;

    public VersionInformationAdapter(Context context, List<VersionInfo> list){
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

            convertView = View.inflate(context, R.layout.version_information_item,null);

            holder.tv1 = convertView.findViewById(R.id.version_information_item_tv1);
            holder.tv2 = convertView.findViewById(R.id.version_information_item_tv2);
            holder.v = convertView;

            convertView.setTag(holder);

        }else{
            holder = (Holder)convertView.getTag();
        }

        holder.tv1.setText(list.get(position).getVersionName());
        holder.tv2.setText(list.get(position).getVersionNum());

        if(position % 2 == 0){
            System.out.println(position);
            holder.v.setBackgroundColor(context.getResources().getColor(R.color.colorGray1));
        }

        return convertView;
    }

    class Holder{
        /**
         * tv1：版本名称
         * tv2：版本号
         * v：整条item
         */
        TextView tv1,tv2;
        View v;
    }
}
