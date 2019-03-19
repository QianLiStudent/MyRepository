package com.example.administrator.easycure.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.DBControler;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/22 0022.
 */

public class FragmentScheduleRlvAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<Map<String,String>> list;

    /**
     * 数组的参数分别是：橘、绿、蓝
     */
    public String colors[] = {"#ff3600","#00c631","#4e83e3"};

    public interface OnItemClickListener{
        /**
         *
         * @param itemView      表示被点击的item本身
         * @param layoutPosition        表示被点击的item在列表中的位置序号
         */

        void onItemClick(View itemView, int layoutPosition);     //点击进入备忘录设置界面
        void onItemLongClick(View itemView, int layoutPosition);     //长按删除该备忘录
    }

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setUpClick(final MyViewHolder holder){
        if(this.onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentScheduleRlvAdapter.this.onItemClickListener.onItemClick(holder.itemView,holder.getLayoutPosition());
                }
            });
        }

        if(this.onItemClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    FragmentScheduleRlvAdapter.this.onItemClickListener.onItemLongClick(holder.itemView,holder.getLayoutPosition());
                    return true;
                }
            });
        }
    }

    public FragmentScheduleRlvAdapter(Context context, List<Map<String,String>> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.fragment_schedule_plan_rlv_item,null);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        /**
         *  v ：侧边装饰条，下面索引就拿 数据库 的条数来mod 3 再 -1
         *  tv1 ：一个月中的第几号
         * tv2 ：月份
         * tv3 ：备忘录标题
         * tv4 ：设置时间
         * tv5 ：备忘录内容
         */

        holder.v.setBackgroundColor(Color.parseColor(colors[position % 3]));
        holder.tv1.setText(list.get(position).get("tv1"));
        holder.tv2.setText(list.get(position).get("tv2"));
        holder.tv3.setText(list.get(position).get("tv3"));
        holder.tv4.setText(list.get(position).get("tv4"));
        holder.tv5.setText(list.get(position).get("tv5"));

        setUpClick(holder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //显示地在列表中删除item
    public void delete(int position){
        list.remove(position);

        notifyItemRemoved(position);
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    View v;
    TextView tv1,tv2,tv3,tv4,tv5;

    public MyViewHolder(View itemView) {
        super(itemView);

        v = (View)itemView.findViewById(R.id.fragment_schedule_plan_rlv_item_v);
        tv1 = (TextView)itemView.findViewById(R.id.fragment_schedule_plan_rlv_item_tv1);
        tv2 = (TextView)itemView.findViewById(R.id.fragment_schedule_plan_rlv_item_tv2);
        tv3 = (TextView)itemView.findViewById(R.id.fragment_schedule_plan_rlv_item_tv3);
        tv4 = (TextView)itemView.findViewById(R.id.fragment_schedule_plan_rlv_item_tv4);
        tv5 = (TextView)itemView.findViewById(R.id.fragment_schedule_plan_rlv_item_tv5);
    }
}



