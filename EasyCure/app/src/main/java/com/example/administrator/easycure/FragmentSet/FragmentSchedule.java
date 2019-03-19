package com.example.administrator.easycure.FragmentSet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.easycure.JavaBean.SchedulePlan;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.activities.DrawerActivity;
import com.example.administrator.easycure.adapters.FragmentScheduleRlvAdapter;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.DBControler;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/22 0022.
 */

public class FragmentSchedule extends Fragment implements View.OnClickListener,FragmentScheduleRlvAdapter.OnItemClickListener {

    private TextView fragment_schedule_tv1;
    private ImageView fragment_schedule_iv,fragment_schedule_iv2;
    private RecyclerView fragment_schedule_rlv;

    private List<Map<String,String>> list = new ArrayList<>();
    private Map<String,String> map;

    private FragmentScheduleRlvAdapter fragmentScheduleRlvAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule,container,false);

        init(view);

        readDB();

        return view;
    }

    public void init(View v){

        fragment_schedule_tv1 = (TextView)v.findViewById(R.id.fragment_schedule_tv1);

        fragment_schedule_iv = (ImageView)v.findViewById(R.id.fragment_schedule_iv);

        fragment_schedule_iv2 = (ImageView)v.findViewById(R.id.fragment_schedule_iv2);

        fragment_schedule_rlv = (RecyclerView)v.findViewById(R.id.fragment_schedule_rlv);

        fragment_schedule_iv.setOnClickListener(this);
        fragment_schedule_iv2.setOnClickListener(this);
    }

    //从数据库读取备忘录数据

    public void readDB(){

        int schedulePlanItemCount = DBControler.getTableItemCount("SchedulePlan");
        if(schedulePlanItemCount != 0){
            fragment_schedule_tv1.setVisibility(View.GONE);

            for(int i = schedulePlanItemCount;i >= 1;i--){
                SchedulePlan plan = DBControler.selectScheduleItem(i);

                if(plan != null){
                    initSchedulePlanItem(plan.getTitle(),plan.getTime(),plan.getCreateMonth(),plan.getCreateDay(),plan.getMessage());
                }

            }
        }else{
            fragment_schedule_tv1.setVisibility(View.VISIBLE);
        }

        fragmentScheduleRlvAdapter = new FragmentScheduleRlvAdapter(getContext(),list);
        fragment_schedule_rlv.setAdapter(fragmentScheduleRlvAdapter);

        fragmentScheduleRlvAdapter.setOnItemClickListener(this);

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        fragment_schedule_rlv.setLayoutManager(linearLayoutManager);

        fragment_schedule_rlv.setItemAnimator(new DefaultItemAnimator());

    }

    public void initSchedulePlanItem(String title,String time,String createMonth,String createDay,String message){
        map = new HashMap<>();
        map.put("tv1",createDay);
        map.put("tv2",createMonth);
        map.put("tv3",title);
        map.put("tv4",time);
        map.put("tv5",message);
        list.add(map);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_schedule_iv:
//                左侧图标按钮打开侧栏
                DrawerActivity.openDrawer();
                break;
            case R.id.fragment_schedule_iv2:
//                右侧图标按钮打开添加备忘录界面
                FragmentPharmapp fragmentPharmapp = FragmentPharmapp.getInstance(1);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_drawer_fl,fragmentPharmapp).commit();
                break;
        }

    }

    @Override
    public void onItemClick(View itemView, int layoutPosition) {

        //由于我们把备忘录反向排序，即最新创建的总是在第一条，但是显示效果与数据库完全是相反的排序，因此这里要做特殊取值处理
        SchedulePlan plan = DBControler.selectScheduleItem(DBControler.getTableItemCount(Constant.TABLE_NAME_SCHEDULE_PLAN) - layoutPosition );

        Bundle bundle = new Bundle();
        bundle.putInt("schedulePlanItemId",plan.getId());
        bundle.putInt("itemId",1);
        bundle.putString("title",plan.getTitle());
        bundle.putString("time",plan.getTime());
        bundle.putString("createMonth",plan.getCreateMonth());
        bundle.putString("createDay",plan.getCreateDay());
        bundle.putString("message",plan.getMessage());

        //点击item切换到备忘录设置界面，准备对该条备忘录重新编辑
        FragmentPharmapp fragmentPharmapp = new FragmentPharmapp();

        fragmentPharmapp.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_drawer_fl,fragmentPharmapp).commit();
        //接着把用户点击的item对应的内容映射到备忘录中准备编辑，这里写个方法把当前item的位置传过去，让数据库修改该条item
    }

    @Override
    public void onItemLongClick(View itemView, final int layoutPosition) {
        //长按item表示准备删除该条备忘录
        new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(getResources().getString(R.string.delete_memo))
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //这一句是删除逻辑，但是我们还得更新数据库才能真正删除该条数据
                        fragmentScheduleRlvAdapter.delete(layoutPosition);
                        //更新数据库，当判断到数据库没有记录的时候把nothing to show显示出来
//                        数据库id从1开始
                        if(DBControler.deleteScheduleItem(layoutPosition + 1)){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),getResources().getString(R.string.successfully_deleted),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        dialog.dismiss();
                        if(DBControler.getTableItemCount(Constant.TABLE_NAME_SCHEDULE_PLAN) == 0){
                            fragment_schedule_tv1.setVisibility(View.VISIBLE);
                        }
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }
}
