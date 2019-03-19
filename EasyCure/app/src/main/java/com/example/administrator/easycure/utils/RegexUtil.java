package com.example.administrator.easycure.utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2019/3/17 0017.
 */

public class RegexUtil {

    //把json字符串，当然不全是，可能有这种结构{key-value},{key-value}
    public static List<JSONObject> parseJsonStr2JsonObjList(String jsonStr){
        String regex = "\\{.*?\\}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(jsonStr);

        List<JSONObject> list = new ArrayList<>();

        while(m.find()){
            try{
                JSONObject json = new JSONObject(m.group());
                list.add(json);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return list;
    }
}
