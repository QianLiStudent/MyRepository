package com.example.administrator.easycure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.easycure.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

public class CaseManagementAdapter extends BaseExpandableListAdapter {

    private List<String> mParentList;     //父Item
    private List<List<Map<String,String>>> mChildList;     //子Item

    public CaseManagementAdapter(List<String> mParentList,List<List<Map<String,String>>> mChildList){
        this.mParentList = mParentList;
        this.mChildList = mChildList;
    }

    @Override
    public int getGroupCount() {
        return mParentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mChildList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder parentViewHolder;

        if (convertView == null){
            parentViewHolder = new ParentViewHolder();

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_lsit_parent_item,parent,false);

            parentViewHolder.iv = (ImageView)convertView.findViewById(R.id.expang_list_parent_item_iv);
            parentViewHolder.tv = (TextView)convertView.findViewById(R.id.expang_list_parent_item_tv);

            convertView.setTag(parentViewHolder);
        }else {
            parentViewHolder = (ParentViewHolder)convertView.getTag();
        }

        parentViewHolder.tv.setText(mParentList.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChilcViewHolder childViewHolder;

        if (convertView==null){
            childViewHolder = new ChilcViewHolder();

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_lsit_child_item,parent,false);

            childViewHolder.tv1 = (TextView)convertView.findViewById(R.id.expand_list_child_item_tv1);
            childViewHolder.tv2 = (TextView)convertView.findViewById(R.id.expand_list_child_item_tv2);

            convertView.setTag(childViewHolder);

        }else {
            childViewHolder = (ChilcViewHolder) convertView.getTag();
        }
        childViewHolder.tv1.setText(mChildList.get(groupPosition).get(childPosition).get("illnessName"));
        childViewHolder.tv2.setText(mChildList.get(groupPosition).get(childPosition).get("illnessPolytype"));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ParentViewHolder{
        ImageView iv;
        TextView tv;
    }
    
    class ChilcViewHolder{
        TextView tv1,tv2;
    }
}
