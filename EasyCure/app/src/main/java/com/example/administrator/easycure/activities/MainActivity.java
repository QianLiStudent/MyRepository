package com.example.administrator.easycure.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.easycure.R;
import com.example.administrator.easycure.utils.BaseActivity;
import com.example.administrator.easycure.utils.CacheUtil;
import com.example.administrator.easycure.utils.Constant;
import com.example.administrator.easycure.utils.LangGetUtil;
import com.example.administrator.easycure.utils.NetworkUsable;
import com.example.administrator.easycure.utils.RegexUtil;
import com.example.administrator.easycure.utils.SpUtil;
import com.example.administrator.easycure.utils.StrUtil;
import com.example.administrator.easycure.utils.WiFiConnectChecked;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private Button activity_main_btn_positioning;
    private RelativeLayout activity_main_illness_selection,activity_main_rl_health_information,activity_main_rl_more;

    private Intent intent;

    private ProgressDialog mProgressDialog;

    private boolean welcome_back = false;

    private String mVersionCode = "";
    private String mVersionName = "";
    private String updateMsg = "";
    private String mDownloadUrl = "";

    private static final int NOT_NEED_UPDATE = 0;       //不需要更新标识码
    private static final int NEED_UPDATE = 1;           //需要更新标识码
    private static final int URL_ERROR = 1000;
    private static final int IO_ERROR = 1001;
    private static final int JSON_ERROR = 1002;

    private static final int PERMISSION_REQUEST_INSTALL = 1003;     //安装权限请求状态码
    private static final int INSTALL_ACTIVITY_FINISH = 1010;        //安装完成

    private File apk;   //下载的apk包
    private Uri packageURI;      //包名的uri用户安装权限的绑定

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case NEED_UPDATE:
                    //表示当前需要更新，调用相关的更新操作逻辑
                    showUpdateDialog();
                    break;
                case NOT_NEED_UPDATE:
                    //表示当前是最新版本，不需要更新，因此不做任何操作，这里写出来只是为了和需要更新的逻辑做区分
                    break;
                case URL_ERROR:
                    Toast.makeText(MainActivity.this,"URL解析错误",Toast.LENGTH_SHORT).show();
                    break;
                case IO_ERROR:
                    Toast.makeText(MainActivity.this,"IO错误",Toast.LENGTH_SHORT).show();
                    break;
                case JSON_ERROR:
                    Toast.makeText(MainActivity.this,"JSON解析错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){

         packageURI = Uri.parse("package:" + getPackageName()); //初始化app包名的uri
        //致电客服按钮
        activity_main_btn_positioning = (Button)findViewById(R.id.activity_main_btn_positioning);

        /**
         * activity_main_illness_selection：商店
         * activity_main_btn_doctor：健康资讯
         * activity_main_btn_more：更多
         */
        activity_main_illness_selection = (RelativeLayout)findViewById(R.id.activity_main_illness_selection);
        activity_main_rl_health_information = (RelativeLayout)findViewById(R.id.activity_main_rl_health_information);
        activity_main_rl_more = (RelativeLayout)findViewById(R.id.activity_main_rl_more);

        activity_main_btn_positioning.setOnClickListener(this);
        activity_main_illness_selection.setOnClickListener(this);
        activity_main_rl_health_information.setOnClickListener(this);
        activity_main_rl_more.setOnClickListener(this);

        //这里welcome_back用来判断用户是否从本来就是登录状态回到主界面的，如果为true则表示是从启动页切到主界面的
        welcome_back = getIntent().getBooleanExtra("welcome_back",false);
        if(welcome_back){
            Toast.makeText(this,getResources().getString(R.string.welcome_back),Toast.LENGTH_SHORT).show();
        }

        //检查读写sd卡权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        //检查安装权限
        if(Build.VERSION.SDK_INT >= 26){
            if(!getPackageManager().canRequestPackageInstalls()){
                Toast.makeText(this,"请先授予安装权限，以便软件更新安装",Toast.LENGTH_SHORT).show();
                //表示用户依然没有给安装权限，这时候我们跳到权限设置界面让用户设置
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
                startActivityForResult(intent,PERMISSION_REQUEST_INSTALL);
            }
        }else{
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},
                        PERMISSION_REQUEST_INSTALL);
            }
        }


        //上来先判断用户是否在app中设置WiFi状态自动检查更新
        if(SpUtil.getAutomaticUpdateStatus(this).equals(getResources().getString(R.string.only_wifi))){
            //进入这里表示用户设置WiFi下自动检查更新
            //接着判断当前是否为WiFi状态，如果是的话应该进一步判断
            if(WiFiConnectChecked.isWiFiConnected(MainActivity.this)){
                //进入这里表示当前是WiFi状态，接下来应该判断是否服务器有新版本，若是则进行更新替换，否则什么都不做
                checkUpdate();
            }
        }
    }

    //检查更新，这时候应该先去服务器拿到当前服务器中的app的版本信息
    private void checkUpdate(){
        new Thread(new Runnable(){
            @Override
            public void run() {

                Message message = handler.obtainMessage();

                try {
                    //这个URL地址是开发人员给的，因此绝不会出错，所以不需要考虑找不到的情况
                    URL url = new URL("http://119.23.208.63/ECure-system/public/data.json");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(5000);
                    int code = httpURLConnection.getResponseCode();
                    if(code == 200){
                        InputStream in = httpURLConnection.getInputStream();
                        String jsonStr = StrUtil.stream2String(in);

                        JSONObject json = new JSONObject(jsonStr);
                        mVersionCode = (String)json.get("versionCode");     //最新版本号
                        mVersionName = (String)json.get("versionName");     //最新版本名称
                        updateMsg = (String)json.get("updateMsg");       //最新版本说明
                        mDownloadUrl = (String)json.get("downloadUrl");     //最新版本下载url

                        boolean isNeedUpdate = false;   //更新标志，true表示需要更新，false表示不需要更新
                        //把 点 作为字符串的分隔符需要加上\\，否则可能会被当做正则表达式的符号
                        String[] nowVersion = Constant.getVersionInfo(getApplicationContext()).get("versionName").split("\\.");
                        String[] latestVersion = mVersionName.split("\\.");

                        for(int i = 0;i < nowVersion.length;i++){
                            if(Integer.parseInt(nowVersion[i]) < Integer.parseInt(latestVersion[i])){
                                isNeedUpdate = true;
                                break;
                            }
                        }

                        if(isNeedUpdate){
                            //表示当前需要更新
                            message.what = NEED_UPDATE;
                            //记录当前版本是否为最新版本的bool值，这里表示当前不是最新版本
                            SpUtil.saveUpdateStatus(MainActivity.this,false);
                        }else{
                            message.what = NOT_NEED_UPDATE;
                            //这里表示当前为最新版本
                            SpUtil.saveUpdateStatus(MainActivity.this,true);
                        }
                    }
                    handler.sendMessage(message);
                } catch (MalformedURLException e) {
                    message.what = URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    message.what = IO_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    message.what = JSON_ERROR;
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //提示对话框，表示检查到新版本，只有当用户设置了WiFi自动更新并处于WiFi模式下才会调用
    private void showUpdateDialog(){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(updateMsg)
                .setPositiveButton(getResources().getString(R.string.confirm),new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //判断之前是否存在之前的安装包，若存在则删除
                        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "ECure.apk");
                        if(file != null && file.length() > 0){
                            file.delete();
                        }
                        //开始下载apk
                        downloadApk();
                        dialog.dismiss();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    //下载apk包
    private void downloadApk(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String target = Environment.getExternalStorageDirectory().getPath() + File.separator + "ECure.apk";
            RequestParams params = new RequestParams(mDownloadUrl);
            params.setAutoRename(true); //支持断点续传
            params.setSaveFilePath(target);     //设置apk保存路径
            x.http().get(params,new Callback.ProgressCallback<File>(){

                @Override
                public void onSuccess(File result) {
                    //下载成功后会回调本方法
                    if(mProgressDialog != null && mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }

                    apk = result;

                    //判断用户设备的版本是不是8.0及以上的，若是需要做权限判断
                    if(Build.VERSION.SDK_INT >= 26){
                        boolean b = getPackageManager().canRequestPackageInstalls();
                        if(b){
                            //表示已经拥有安装权限了
                            installApk(apk);
                        }else{
                            //表示没有安装权限，需要做权限申请，直接打开权限设置界面，用请求权限的方式已经无效了
                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
                            startActivityForResult(intent,PERMISSION_REQUEST_INSTALL);
                        }
                    }else{
                        installApk(apk);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    //下载过程中出错了会回调本方法
                    if(mProgressDialog != null && mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                    Toast.makeText(MainActivity.this,"下载失败，请检查网络或app权限",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {
                    //下载开始那个时刻回调本方法
                    mProgressDialog = new ProgressDialog(MainActivity.this);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  //水平进度条
                    mProgressDialog.setMessage("下载中,请稍后...");
                    mProgressDialog.setProgress(0);
                    mProgressDialog.show();

                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    //下载过程中每增下载量就会回调本方法
                    mProgressDialog.setMax((int)total);
                    mProgressDialog.setProgress((int)current);
                }
            });
        }else{
            Toast.makeText(this,"请检查SD卡是否插入",Toast.LENGTH_SHORT).show();
        }
    }

    //在请求权限后会弹窗提示用户设置相关权限，用户操作完后会自动回调本方法
    //之所以需要本回调是因为我们需要在用户设置权限（可能用户开启了权限或关闭了权限）后进一步判断当前权限是否已经开启
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(PERMISSION_REQUEST_INSTALL == requestCode){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(apk != null){
                    installApk(apk);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case PERMISSION_REQUEST_INSTALL:
                //表示刚才打开设置界面让用户设置安装权限后回到本界面的逻辑处理
                //需要判断当前是否已经有权限了
                if(getPackageManager().canRequestPackageInstalls()){
                    //表示开启安装权限了
                    if(apk != null){
                        //表示apk包刚才已经下载到本地了
                        installApk(apk);
                    }
                }else{
                    //表示apk包已经下载好了，但用户依然没有开启安装权限
                    if(apk != null){
                        //表示apk包刚才已经下载到本地了
                        Toast.makeText(MainActivity.this,"安装失败，应用无安装权限",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case INSTALL_ACTIVITY_FINISH:
                //apk安装完成，直接结束掉当前应用
                finish();
                break;
        }
    }

    //apk包安装
    private void installApk(File apk){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //如果是andorid 7.0以上版本需要使用if内的逻辑，因为7.0以上的版本的私有目录被限制了
        if(Build.VERSION.SDK_INT >= 24){
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            Uri apkUri = FileProvider.getUriForFile(MainActivity.this, "com.yll520wcf.test.fileprovider", apk);
            //添加动态权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri,"application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(apk),"application/vnd.android.package-archive");
        }
        startActivityForResult(intent,INSTALL_ACTIVITY_FINISH);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_main_btn_positioning:
                //切换到定位界面
                intent = new Intent(this,DrawerActivity.class);
                intent.putExtra("itemId",5);
                startActivity(intent);
                break;
            case R.id.activity_main_illness_selection:
                //切换到病症选择界面
                intent = new Intent(this,DrawerActivity.class);
                intent.putExtra("itemId",1);
                startActivity(intent);
                break;
            case R.id.activity_main_rl_health_information:  //切换到健康资讯模块
                intent = new Intent(this,DrawerActivity.class);
                intent.putExtra("itemId",2);
                startActivity(intent);
                break;
            case R.id.activity_main_rl_more:
                //切换到更多功能界面
                intent = new Intent(this,FunctionChooseActivity.class);
                startActivity(intent);
                break;
        }
    }

    //按返回键2次退出app
    private long lastTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.KEYCODE_BACK == keyCode){
            if(System.currentTimeMillis() - lastTime > 2000){
                Toast.makeText(this,getResources().getString(R.string.exit), Toast.LENGTH_SHORT).show();
                lastTime = System.currentTimeMillis();
            }else{
                close();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
