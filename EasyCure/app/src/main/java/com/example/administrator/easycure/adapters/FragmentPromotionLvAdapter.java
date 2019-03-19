package com.example.administrator.easycure.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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

public class FragmentPromotionLvAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,Object>> list;

    public FragmentPromotionLvAdapter(Context context, List<Map<String,Object>> list){
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

            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_promotion_lv_item,null);

            holder.iv = (ImageView)convertView.findViewById(R.id.fragment_promotion_lv_item_iv);
            holder.tv_title = (TextView) convertView.findViewById(R.id.fragment_promotion_lv_item_tv1);
            holder.tv_message = (TextView) convertView.findViewById(R.id.fragment_promotion_lv_item_tv2);

            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }

        holder.tv_title.setText((String)(list.get(position).get("title")));
        holder.tv_message.setText((String)(list.get(position).get("content")));
        holder.iv.setImageBitmap((Bitmap)(list.get(position).get("img")));

        return convertView;
    }

    class Holder{
        //Fragment_promotion_lv：ListView中item的左侧图片
        ImageView iv;
        //Fragment_promotion_lv：ListView中item的标题和正文内容
        TextView tv_title,tv_message;
    }
}
