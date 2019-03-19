package com.example.administrator.easycure.FragmentSet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.adapters.FragmentPharmappContactLvAdapter;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.DBControler;
import com.example.administrator.easycure.utils.GetCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/21 0021.
 */

public class FragmentPharmappContact extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener {

    private EditText fragment_pharmapp_contact_et1,fragment_pharmapp_contact_et2;
    private ListView fragment_pharmapp_contact_lv;
    private Button fragment_pharmapp_contact_btn1,fragment_pharmapp_contact_btn2;

    public List<Map<String,String>> list = new ArrayList<>();
    private Map<String,String> map;

    private FragmentPharmappContactLvAdapter fragmentPharmappContactLvAdapter;

    private int mYear,mMonth,mDay,mHour,mMinute,index;

    private Bundle mBundle;

    private int schedulePlanItemId = -1;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pharmapp_contact,container,false);

        mBundle = getArguments();

        init(view);

        initData();

        return view;
    }

    public void init(View v){
        fragment_pharmapp_contact_et1 = (EditText)v.findViewById(R.id.fragment_pharmapp_contact_et1);
        fragment_pharmapp_contact_et2 = (EditText)v.findViewById(R.id.fragment_pharmapp_contact_et2);
        fragment_pharmapp_contact_lv = (ListView)v.findViewById(R.id.fragment_pharmapp_contact_lv);
        fragment_pharmapp_contact_btn1 = (Button)v.findViewById(R.id.fragment_pharmapp_contact_btn1);
        fragment_pharmapp_contact_btn2 = (Button)v.findViewById(R.id.fragment_pharmapp_contact_btn2);

        fragment_pharmapp_contact_lv.setOnItemClickListener(this);
        fragment_pharmapp_contact_btn1.setOnClickListener(this);
        fragment_pharmapp_contact_btn2.setOnClickListener(this);
    }

    public void initData(){

       /* 这里需要对切换来源做判断，当下面判断为true的时候表示是从schedule界面切过
            来的，应该是修改备忘录，否则就是从其他界面切过来，直接初始化
       */
        if(mBundle != null && mBundle.getString("title","").length() > 0){
            schedulePlanItemId = mBundle.getInt("schedulePlanItemId",-1);

            fragment_pharmapp_contact_et1.setText(mBundle.getString("title"));
            fragment_pharmapp_contact_et2.setText(mBundle.getString("message"));

            map = new HashMap<>();
            map.put("title",getActivity().getResources().getString(R.string.start));
            map.put("time",mBundle.getString("time","").split("-")[0]);
            list.add(map);

            map = new HashMap<>();
            map.put("title",getActivity().getResources().getString(R.string.end));
            map.put("time",mBundle.getString("time","").split("-")[1]);

            list.add(map);
        }else{
            map = new HashMap<>();
            map.put("title",getActivity().getResources().getString(R.string.start));
            map.put("time",GetCalendar.getTime());
            list.add(map);

            map = new HashMap<>();
            map.put("title",getActivity().getResources().getString(R.string.end));
            map.put("time",GetCalendar.getTime());
            list.add(map);
        }
        fragmentPharmappContactLvAdapter = new FragmentPharmappContactLvAdapter(getContext(),list);

        fragment_pharmapp_contact_lv.setAdapter(fragmentPharmappContactLvAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:     //开始：设置开始日期
                setDate(0);
                break;
            case 1:     //结束：设置结束时间
                setDate(1);
                break;
        }
    }
//      设置日期：年/月/日
    public void setDate(int index){

        this.index = index;
        fragment_pharmapp_contact_lv.setEnabled(false);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;

                //开始时间设置
                setTime(FragmentPharmappContact.this.index);
            }
        },mYear,mMonth,mDay);

        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                fragment_pharmapp_contact_lv.setEnabled(true);
            }
        });

        DatePicker datePicker = datePickerDialog.getDatePicker();

        if(FragmentPharmappContact.this.index == 1){
            if(GetCalendar.stringToDate(list.get(index - 1).get("time")) != null){
                datePicker.setMinDate(GetCalendar.stringToDate(list.get(index).get("time")).getTime() + 10000);
            }else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"The system is maintaining this feature.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            datePicker.setMinDate(System.currentTimeMillis() + 10000);
        }

        datePickerDialog.show();
    }
//      设置事件：时/分
    public void setTime(int index){

        this.index = index;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;

                //传递参数返回时间格式串
                String time = GetCalendar.getSettingTime(mYear,mMonth,mDay,mHour,mMinute);

                Map<String,String> map = new HashMap<>();

                map.put("time",time);

                if(FragmentPharmappContact.this.index == 0){
                    map.put("title",getActivity().getResources().getString(R.string.start));
                    list.set(FragmentPharmappContact.this.index,map);

                    map = new HashMap<>();

                    map.put("title",getActivity().getResources().getString(R.string.end));

                    map.put("time",time);

                    list.set(FragmentPharmappContact.this.index + 1,map);
                }else{
                    map.put("title",getActivity().getResources().getString(R.string.end));
                    list.set(FragmentPharmappContact.this.index,map);
                }

                fragmentPharmappContactLvAdapter.notifyDataSetChanged();

                fragment_pharmapp_contact_lv.setEnabled(true);
            }
        },mHour,mMinute,true);


        timePickerDialog.show();

        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                fragment_pharmapp_contact_lv.setEnabled(true);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_pharmapp_contact_btn1:   //提交备忘录
                if(fragment_pharmapp_contact_et1.getText().toString().trim().length() > 0){

                    HashMap<String,String> tmpMap = new HashMap<>();
                    tmpMap.put("title",fragment_pharmapp_contact_et1.getText().toString().trim());
                    tmpMap.put("message",fragment_pharmapp_contact_et2.getText().toString().trim());
                    tmpMap.put("createMonth",Constant.MONTH_ARRAY[Calendar.getInstance().get(Calendar.MONTH)]);
                    tmpMap.put("createDay",Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "");
                    tmpMap.put("time",list.get(0).get("time") + "-" + list.get(1).get("time"));

                    if(schedulePlanItemId != -1){
                        //执行这里表示更新数据
                        DBControler.updateScheduleItem(schedulePlanItemId,tmpMap);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),getResources().getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        //这里表示插入数据
                        DBControler.addScheduleItem(tmpMap);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),getResources().getString(R.string.successfully_added), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    //填写完毕并提交后直接切换到计划表即备忘录显示界面
                    FragmentSchedule fragmentSchedule = new FragmentSchedule();

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.activity_drawer_fl,fragmentSchedule).commit();
                }else{
                    Toast.makeText(getContext(),getResources().getString(R.string.title_no_empty), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fragment_pharmapp_contact_btn2:   //初始化填写的内容
                //初始化2个输入框
                fragment_pharmapp_contact_et1.setText("");
                fragment_pharmapp_contact_et2.setText("");
                //初始化时间

                list.clear();

                fragmentPharmappContactLvAdapter.notifyDataSetChanged();

                mBundle = null;
                initData();
                break;
        }
    }
}
