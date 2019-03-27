package com.example.administrator.easycure.JavaBean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

//一个病症具体分型的JavaBean（没有分型则当做一种类型）
public class Illness implements Serializable{

    private String illnessType;
    private String illnessName;
    private String illnessDescription;
    private String illnessPolytype;
    private String clinicalFeature;
    private String drugRecommend;

    public String getIllnessType(){
        return this.illnessType;
    }

    public void setIllnessType(String illnessType){
        this.illnessType = illnessType;
    }

    public String getIllnessName(){
        return this.illnessName;
    }

    public void setIllnessName(String illnessName){
        this.illnessName = illnessName;
    }

    public String getIllnessDescription(){
        return this.illnessDescription;
    }

    public void setIllnessDescription(String illnessDescription){
        this.illnessDescription = illnessDescription;
    }

    public String getIllnessPolytype(){
        return this.illnessPolytype;
    }

    public void setIllnessPolytype(String illnessPolytype){
        this.illnessPolytype = illnessPolytype;
    }

    public String getClinicalFeature(){
        return this.clinicalFeature;
    }

    public void setClinicalFeature(String clinicalFeature){
        this.clinicalFeature = clinicalFeature;
    }

    public String getDrugRecommend(){
        return this.drugRecommend;
    }

    public void setDrugRecommend(String drugRecommend){
        this.drugRecommend = drugRecommend;
    }
}
