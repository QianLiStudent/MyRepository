package com.example.administrator.easycure.FragmentSet;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.activities.DrawerActivity;
import com.example.administrator.easycure.activities.FunctionChooseActivity;
import com.example.administrator.easycure.activities.LocationActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/21 0021.
 */

public class FragmentPharmappWhere extends Fragment {

    private FloatingActionButton activity_drawer_fab;
    private int centerX;    //揭露动画起点坐标x，为悬浮按钮圆心x坐标
    private int centerY;    //揭露动画起点坐标y，为悬浮按钮圆心y坐标
    private Intent intent0; //意图，从当前Fragment所在DrawerActivity切换到LocationActivity

//    private MapView fragment_pharmapp_where_bmv;
    private TextureMapView fragment_pharmapp_where_bmv;
    public LocationClient mLocationClient;
//    public MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.COMPASS;  //以罗盘方式定位
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLocationClient = new LocationClient(getActivity());
        mLocationClient.registerLocationListener(new MyLocationListener());
        View view = inflater.inflate(R.layout.fragment_pharmapp_where,container,false);
        init(view);

        return view;
    }

    public void init(View v){

//        fragment_pharmapp_where_bmv = (MapView) v.findViewById(R.id.fragment_pharmapp_where_bmv);   //百度地图
        fragment_pharmapp_where_bmv = (TextureMapView) v.findViewById(R.id.fragment_pharmapp_where_bmv);   //百度地图
        activity_drawer_fab = (FloatingActionButton)v.findViewById(R.id.activity_drawer_fab);   //悬浮按钮

        activity_drawer_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int[] vLocation = new int[2];
                //构建这个数组并以悬浮按钮的x、y坐标初始化
                activity_drawer_fab.getLocationInWindow(vLocation);
                centerX = vLocation[0] + activity_drawer_fab.getMeasuredWidth() / 2;
                centerY = vLocation[1] + activity_drawer_fab.getMeasuredHeight() / 2;

                //!!!!!!!!!intent乃全局，一定要点击的时候才构造，不然乱了，跳转不了!!!!!!!!!!
                intent0 = new Intent(FragmentPharmappWhere.this.getActivity(), LocationActivity.class);
                intent0.putExtra("cx",centerX);
                intent0.putExtra("cy",centerY);
                startActivity(intent0);
            }
        });

        baiduMap = fragment_pharmapp_where_bmv.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);      //设置定位类型（卫星定位之类的）
        baiduMap.setMyLocationEnabled(true);    //开启定位图层

        requestPermission();
    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            Toast.makeText(getActivity(), "当前位置:" + location.getAddrStr(), Toast.LENGTH_LONG).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());        //拿到经纬度
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);  //地图移动到指定经纬度的点
            update = MapStatusUpdateFactory.zoomTo(18f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        //构造定位的数据——我的实际位置
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());     //纬度
        locationBuilder.longitude(location.getLongitude());   //经度
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
//        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(locationMode,true,null));   //以罗盘为定位标志
    }

    private void requestPermission(){
        //        申请权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    //初始化定位选项
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);   //设置高精度定位模式
        option.setCoorType("bd09ll");   //设置返回经纬度坐标类型，不设置默认是GCJ02，除了bd0911外的坐标都是加过密的，因此会有严重偏移
        option.setOpenGps(true);    //打开GPS定位
        option.setScanSpan(3000);       //设置扫描时间间隔
        option.setIsNeedAddress(true);  //设置是否需要定位信息
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onResume() {
        super.onResume();
        fragment_pharmapp_where_bmv.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        fragment_pharmapp_where_bmv.onPause();
    }

    //申请权限第二步
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getActivity(), "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(getActivity(), "发生未知错误", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }
        }
    }

    @Override
    public void onDestroy() {
        mLocationClient.stop();
        fragment_pharmapp_where_bmv.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        super.onDestroy();
    }
}
