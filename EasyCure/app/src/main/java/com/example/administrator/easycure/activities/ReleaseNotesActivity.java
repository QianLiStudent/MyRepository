package com.example.administrator.easycure.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;

/**
 * Created by Administrator on 2018/11/3 0003.
 */

public class ReleaseNotesActivity extends BaseActivity implements View.OnClickListener{

    private ImageView activity_release_notes_iv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_notes);

        init();
    }

    public void init(){
        activity_release_notes_iv1 = (ImageView)findViewById(R.id.activity_release_notes_iv1);

        activity_release_notes_iv1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_release_notes_iv1:
                finish();
                break;
        }
    }
}
