package com.example.administrator.easycure.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/12/13 0013.
 */

public class StrUtil {

    //流转字符串
    public static String stream2String(InputStream is){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        String str = null;
        try {
            while((len = is.read(buffer)) != -1){
                baos.write(buffer,0,len);
            }
            str = new String(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }
}
