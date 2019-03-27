package com.example.administrator.easycure.activities;

import android.Manifest;
import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.overlayutil.*;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements View.OnClickListener,OnGetGeoCoderResultListener {

    //我两个地图实现到定位到我的位置了，配置的话就theme不动，
    // 然后方法我都注视了，也是不动就行；动画就是一个控件，几个变量，几个方法还有一个theme

    private Button activity_location_hospital,activity_location_drugstore;

    //这里要使用百度的BDLocation类而不是系统内置的Location
    private com.baidu.location.BDLocation mLocation;
    
    //地图用变量
    private LocationClient mLocationClient;
    private MapView activity_location_bmv;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    //POI检索
    private PoiSearch mPoiSearch;
    private List<PoiInfo> poiInfos;
    private GeoCoder mSearch;   //经纬度转详细地址
    private InfoWindow mInfoWindow;
    private BitmapDescriptor mBitmapDescriptor;

    //onCreate中调用揭露动画
    private View content;//根布局对象（用来控制整个布局）
    private View mPuppet;//揭露层对象
    private int mX ;
    private int mY ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        baiduMap.setMyLocationEnabled(true);

        content = findViewById(R.id.activity_location);
        mPuppet = findViewById(R.id.view_puppet);

        //动画需要依赖于某个视图才可启动，
        // 这里依赖于根布局对象，并且开辟一个子线程，充分利用资源
        content.post(new Runnable() {
            @Override
            public void run() {
                mX = getIntent().getIntExtra("cx", 0);
                mY = getIntent().getIntExtra("cy", 0);
                Animator animator = createRevealAnimator(mX, mY);
                animator.start();
            }
        });

    }

    private void init(){
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location);
        activity_location_hospital = (Button)findViewById(R.id.activity_location_hospital);
        activity_location_drugstore = (Button)findViewById(R.id.activity_location_drugstore);
        activity_location_bmv = (MapView) findViewById(R.id.activity_location_bmv);
        baiduMap = activity_location_bmv.getMap();
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        requestPermission();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_location_hospital:
                //检索医院，医院由于可能需要去大型医院，因此这里用城市检索（范围大一点）
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(mLocation.getCity())
                        .keyword("医院"));
                Log.i("city",mLocation.getCity());
                break;
            case R.id.activity_location_drugstore:
                //检索药店，药店的话一般普通的药物在寻常药店就可以买到了，因此采用周边检索
                LatLng latLng = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
                mPoiSearch.searchNearby(new PoiNearbySearchOption()
                        .keyword("药店")
                        .sortType(PoiSortType.distance_from_near_to_far)
                        .location(latLng)
                        .radius(1000000));
                break;
        }
    }

    //动画
    private Animator createRevealAnimator(int x, int y) {
        float startRadius = 0;
        float endRadius = (float) Math.hypot(content.getHeight(), content.getWidth());

        Animator animator = ViewAnimationUtils.createCircularReveal(
                content, x, y,
                startRadius,
                endRadius);
        animator.setDuration(660);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        //判断标志位reversed，true则为添加返回键版动画监听器，false则为跳转动画开启版
        animator.addListener(animatorListener1);
        return animator;
    }

    //定义动画状态监听器_跳转动画开启版
    private Animator.AnimatorListener animatorListener1 = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
//            content.setVisibility(View.VISIBLE);//跳转进来时，（因为finish之前会将之设置为不可见，）
//  ——后期不用了，因为进来的时候会自动绘制所有布局和控件
            // 根布局要设置为可见，与finish部分的不可见相对应
//            mPuppet.setAlpha(1);
        }
        @Override
        public void onAnimationEnd(Animator animation) {
            mPuppet.startAnimation(createAlphaAnimation());
            mPuppet.setVisibility(View.INVISIBLE);//动画结束时，揭露动画设置为不可见

            activity_location_bmv.setVisibility(View.VISIBLE);
            activity_location_hospital.setVisibility(View.VISIBLE);
            activity_location_drugstore.setVisibility(View.VISIBLE);
        }
        @Override
        public void onAnimationCancel(Animator animation) {
        }
        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    //动画
    private AlphaAnimation createAlphaAnimation() {
        AlphaAnimation aa = new AlphaAnimation(1,0);
        aa.setDuration(400);
        aa.setInterpolator(new AccelerateDecelerateInterpolator());//设置插值器
        return aa;
    }

    //申请列表
    private void requestPermission(){
        //        申请权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LocationActivity.this, permissions, 1);
        } else {
            requestLocation();
            activity_location_hospital.setOnClickListener(this);
            activity_location_drugstore.setOnClickListener(this);
        }
    }

    //地图
    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            Toast.makeText(this, "当前位置:" + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(18f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mInfoWindow != null){
                    baiduMap.hideInfoWindow();
                    activity_location_bmv.postInvalidate();
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(mapPoi.getPosition()));
                return true;
            }
        });
    }

    //地图
    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }
    //地图
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);   //设置高精度定位模式
        option.setCoorType("bd09ll");   //设置返回经纬度坐标类型，不设置默认是GCJ02，除了bd0911外的坐标都是加过密的，因此会有严重偏移
        option.setOpenGps(true);    //打开GPS定位
        option.setScanSpan(3000);       //设置扫描时间间隔
        option.setIsNeedAddress(true);  //设置是否需要定位信息
        mLocationClient.setLocOption(option);

        //初始化POI检索对象
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                //获取POI检索结果，我们暂时只关注这个
                if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    return;
                }

                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {

                    baiduMap.clear();

                    //创建PoiOverlay

                    PoiOverlay overlay = new MyPoiOverlay(baiduMap);

                    //设置overlay可以处理标注点击事件

                    baiduMap.setOnMarkerClickListener(overlay);

                    //设置PoiOverlay数据
                    overlay.setData(poiResult);

                    //取到返回的结果集合
                    poiInfos = poiResult.getAllPoi();

                    //添加PoiOverlay到地图中
                    overlay.addToMap();
                    overlay.zoomToSpan();

                    return;
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                //获取Place详情页检索结果

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
    }

    //定位监听器，当成功定位之后会回调下面api
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                LocationActivity.this.mLocation = location;
                navigateTo(location);
            }
        }
    }

    //自定义POI覆盖物
    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        //覆盖物点击事件
        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            Toast.makeText(LocationActivity.this,"地址：" + poiInfos.get(index).getAddress(),Toast.LENGTH_LONG).show();
            return true;
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        Toast.makeText(LocationActivity.this,"地址：" + reverseGeoCodeResult.getAddress(),Toast.LENGTH_LONG).show();
    }

    //地图
    @Override
    protected void onResume() {
        super.onResume();
        activity_location_bmv.onResume();
    }
    //地图
    @Override
    protected void onPause() {
        super.onPause();
        activity_location_bmv.onPause();
    }
    //地图
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        activity_location_bmv.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        mPoiSearch.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}
