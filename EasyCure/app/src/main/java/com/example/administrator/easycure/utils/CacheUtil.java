package com.example.administrator.easycure.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/18 0018.
 */

public class CacheUtil {

    //设置缓存数据
    public static void saveDataByCache(Context context, String cacheFileName,Object data){
        File cache = new File(context.getCacheDir(),cacheFileName);

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        BufferedOutputStream bos = null;

        //判断是否已经存在同名缓存文件了，如果是则删除掉然后重新创建一个缓存文件
        if(cache.length() > 0){
            cache.delete();
            try{
                cache.createNewFile();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        try{
            fos = new FileOutputStream(cache);

            if(data instanceof Bitmap){
                bos = new BufferedOutputStream(fos);

                ((Bitmap)data).compress(Bitmap.CompressFormat.JPEG, 100, fos);
                bos.flush();
            }else if(data instanceof String){
                osw = new OutputStreamWriter(fos);
                bw = new BufferedWriter(osw);

                bw.write((String)data);
                bw.flush();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(bw != null){
                try{
                    bw.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(osw != null){
                try{
                    osw.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(fos != null){
                try{
                    fos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(bos != null){
                try{
                    fos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    //读缓存文件

    /**
     *
     * @param context   上下文对象
     * @param cacheName 缓存文件名
     * @param dataType  从缓存文件中获取的数据类型
     * @return  对象类型
     */
    public static Object getCacheData(Context context,String cacheName,String dataType){
        File cache = new File(context.getCacheDir(),cacheName);

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        StringBuffer stringBuffer = new StringBuffer();

        if(cache.exists() && cache.length() > 0){
            try{
                if(dataType.equals("Bitmap")){
                    return BitmapFactory.decodeFile(cache.getAbsolutePath());
                }else if(dataType.equals("String")){
                    fis = new FileInputStream(cache);
                    isr = new InputStreamReader(fis);
                    br = new BufferedReader(isr);

                    String str = "";

                    while((str = br.readLine()) != null){
                        stringBuffer.append(str);
                    }
                    return stringBuffer.toString();
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(br != null){
                    try{
                        br.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(isr != null){
                    try{
                        isr.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(fis != null){
                    try{
                        fis.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    //获得模糊的缓存数据，主要是照顾advice那些建议中同一个type_id的数据查询,vagueCacheName是用来做模糊查询的模糊缓存文件名
    public static List<String> getVagueCacheData(Context context,String vagueCacheName){

        File cacheDir = new File(context.getCacheDir().getPath());

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        List<String> results = new ArrayList<>();

        if(cacheDir.exists()){
            try{
                File[] cacheFiles = cacheDir.listFiles();
                for(File cache : cacheFiles){
                    if(cache.getPath().indexOf(vagueCacheName) != -1){
                        //进入这里表示找到了
                        fis = new FileInputStream(cache);
                        isr = new InputStreamReader(fis);
                        br = new BufferedReader(isr);

                        String str = "";
                        StringBuffer stringBuffer = new StringBuffer();

                        while((str = br.readLine()) != null){
                            stringBuffer.append(str);
                        }
                        results.add(stringBuffer.toString());
                    }
                }
                return results;
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(br != null){
                    try{
                        br.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(isr != null){
                    try{
                        isr.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(fis != null){
                    try{
                        fis.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return results;
    }
}
