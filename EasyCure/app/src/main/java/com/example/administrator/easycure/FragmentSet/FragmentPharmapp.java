package com.example.administrator.easycure.FragmentSet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.activities.DrawerActivity;

/**
 * Created by Administrator on 2018/10/20 0020.
 */

public class FragmentPharmapp extends Fragment implements View.OnClickListener{

    private ImageView fragment_pharmapp_iv1,fragment_pharmapp_iv2;
    private TextView fragment_pharmapp_tv0,fragment_pharmapp_tv1,fragment_pharmapp_tv2,fragment_pharmapp_tv3;
    private View fragment_pharmapp_v1,fragment_pharmapp_v2,fragment_pharmapp_v3;
    private TextPaint paint;

    private FragmentManager fragmentManager;
    /**
     * id 表示的是3个tab：about us 、 contact 、where
     */
    private int id;

    private Bundle mBundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pharmapp,container,false);

        mBundle = getArguments();

//        这个id是当其他fragment想要切到这个fragment的时候传的值，表示想要展示这个fragment的哪个子tab
//        如果其他fragment传过来的只是单纯的itemId的话直接getInstance初始化即可，否则需要用Bundle自己传itemId

        id = mBundle.getInt("itemId",0);

        init(view);
        return view;
    }

    public void init(View view){
        fragment_pharmapp_iv1 = (ImageView)view.findViewById(R.id.fragment_pharmapp_iv1);
        fragment_pharmapp_iv2 = (ImageView)view.findViewById(R.id.fragment_pharmapp_iv2);
        fragment_pharmapp_tv0 = (TextView)view.findViewById(R.id.fragment_pharmapp_tv0);
        fragment_pharmapp_tv1 = (TextView)view.findViewById(R.id.fragment_pharmapp_tv1);
        fragment_pharmapp_tv2 = (TextView)view.findViewById(R.id.fragment_pharmapp_tv2);
        fragment_pharmapp_tv3 = (TextView)view.findViewById(R.id.fragment_pharmapp_tv3);

        fragment_pharmapp_v1 = view.findViewById(R.id.fragment_pharmapp_v1);
        fragment_pharmapp_v2 = view.findViewById(R.id.fragment_pharmapp_v2);
        fragment_pharmapp_v3 = view.findViewById(R.id.fragment_pharmapp_v3);

        fragment_pharmapp_iv1.setOnClickListener(this);
        fragment_pharmapp_tv1.setOnClickListener(this);
        fragment_pharmapp_tv2.setOnClickListener(this);
        fragment_pharmapp_tv3.setOnClickListener(this);

        fragmentManager = getActivity().getSupportFragmentManager();

        switch(id){
            case 0:     //about us
                changeToAoutus();
                break;
            case 1:     //contact
                changeToContact();
                break;
            case 2:     //where
                changeToWhere();
                break;
        }

    }

    public static FragmentPharmapp getInstance(int itemId){

        Bundle bundle = new Bundle();
        bundle.putInt("itemId",itemId);

        FragmentPharmapp fragmentPharmapp = new FragmentPharmapp();
        fragmentPharmapp.setArguments(bundle);

        return fragmentPharmapp;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_pharmapp_iv1:
                DrawerActivity.openDrawer();
                break;
            case R.id.fragment_pharmapp_tv1:

                changeToAoutus();

                break;
            case R.id.fragment_pharmapp_tv2:

                changeToContact();

                break;
            case R.id.fragment_pharmapp_tv3:

                changeToWhere();

                break;
        }
    }

    public void changeToAoutus(){
        fragment_pharmapp_tv0.setText(getContext().getResources().getString(R.string.about_us));

        fragment_pharmapp_iv2.setBackground(getResources().getDrawable(R.mipmap.fragment_aboutus_communicate));

        fragment_pharmapp_tv1.setTextColor(Color.parseColor("#034fce"));
        fragment_pharmapp_v1.setBackgroundColor(Color.parseColor("#034fce"));
        paint = fragment_pharmapp_tv1.getPaint();
        paint.setFakeBoldText(true);
        fragment_pharmapp_tv2.setTextColor(Color.parseColor("#000000"));
        fragment_pharmapp_v2.setBackgroundColor(Color.parseColor("#ffffff"));
        paint = fragment_pharmapp_tv2.getPaint();
        paint.setFakeBoldText(false);
        fragment_pharmapp_tv3.setTextColor(Color.parseColor("#000000"));
        fragment_pharmapp_v3.setBackgroundColor(Color.parseColor("#ffffff"));
        paint = fragment_pharmapp_tv3.getPaint();
        paint.setFakeBoldText(false);

        FragmentPharmappAboutus fragmentPharmappAboutus = new FragmentPharmappAboutus();
        fragmentManager.beginTransaction().replace(R.id.fragment_pharmapp_fl,fragmentPharmappAboutus).commit();
    }

    public void changeToContact(){
        fragment_pharmapp_tv0.setText(getContext().getResources().getString(R.string.contact));

        fragment_pharmapp_iv2.setBackground(getResources().getDrawable(R.mipmap.fragment_contact_img));

        fragment_pharmapp_tv1.setTextColor(Color.parseColor("#000000"));
        fragment_pharmapp_v1.setBackgroundColor(Color.parseColor("#ffffff"));
        paint = fragment_pharmapp_tv1.getPaint();
        paint.setFakeBoldText(false);
        fragment_pharmapp_tv2.setTextColor(Color.parseColor("#034fce"));
        fragment_pharmapp_v2.setBackgroundColor(Color.parseColor("#034fce"));
        paint = fragment_pharmapp_tv2.getPaint();
        paint.setFakeBoldText(true);
        fragment_pharmapp_tv3.setTextColor(Color.parseColor("#000000"));
        fragment_pharmapp_v3.setBackgroundColor(Color.parseColor("#ffffff"));
        paint = fragment_pharmapp_tv3.getPaint();
        paint.setFakeBoldText(false);

        FragmentPharmappContact fragmentPharmappContact = new FragmentPharmappContact();

        Bundle bundle = this.mBundle;

        fragmentPharmappContact.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.fragment_pharmapp_fl,fragmentPharmappContact).commit();
    }

    public void changeToWhere(){
        fragment_pharmapp_tv0.setText(getContext().getResources().getString(R.string.where));

        fragment_pharmapp_iv2.setBackground(getResources().getDrawable(R.mipmap.icon_main_heart));

        fragment_pharmapp_tv1.setTextColor(Color.parseColor("#000000"));
        fragment_pharmapp_v1.setBackgroundColor(Color.parseColor("#ffffff"));
        paint = fragment_pharmapp_tv1.getPaint();
        paint.setFakeBoldText(false);
        fragment_pharmapp_tv2.setTextColor(Color.parseColor("#000000"));
        fragment_pharmapp_v2.setBackgroundColor(Color.parseColor("#ffffff"));
        paint = fragment_pharmapp_tv2.getPaint();
        paint.setFakeBoldText(false);
        fragment_pharmapp_tv3.setTextColor(Color.parseColor("#034fce"));
        fragment_pharmapp_v3.setBackgroundColor(Color.parseColor("#034fce"));
        paint = fragment_pharmapp_tv3.getPaint();
        paint.setFakeBoldText(true);

        FragmentPharmappWhere fragmentPharmappWhere = new FragmentPharmappWhere();
        fragmentManager.beginTransaction().replace(R.id.fragment_pharmapp_fl,fragmentPharmappWhere).commit();
    }

}
