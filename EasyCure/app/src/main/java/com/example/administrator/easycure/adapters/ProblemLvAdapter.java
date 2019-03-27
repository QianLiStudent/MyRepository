package com.example.administrator.easycure.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.easycure.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/23 0023.
 */

public class ProblemLvAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,String>> mList;

    private List<View> views = new ArrayList<>();

    public ProblemLvAdapter(Context context,List<Map<String,String>> list){
        this.context = context;
        this.mList = list;
    }

    public List<View> getAllItems(){
        return this.views;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
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

            convertView = View.inflate(context, R.layout.customer_service_lv_item,null);
            holder.problem_name = convertView.findViewById(R.id.customer_service_lv_tv1);
            holder.problem_solution = convertView.findViewById(R.id.customer_service_lv_tv2);

            convertView.setTag(holder);

        }else{
            holder = (Holder)convertView.getTag();
        }

        holder.problem_name.setText((mList.get(position)).get("problem_name"));
        holder.problem_solution.setText("\u3000\u3000" + (mList.get(position)).get("problem_solution"));

        int i = 0;

        for(i = 0;i < views.size();i++){
            if(views.get(i) == convertView){
                break;
            }
        }

        if(i == views.size()){
            //表示convertView不在集合中，那就加入集合，如果在集合中就不用管了
            views.add(convertView);
        }

        return convertView;
    }

    class Holder{
        TextView problem_name,problem_solution;
    }
}
