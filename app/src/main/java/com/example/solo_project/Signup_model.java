package com.example.solo_project;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Signup_model {
    @Expose
    @SerializedName("email")private String email;
    @Expose
    @SerializedName("nickname")private String nickname;
    @Expose
    @SerializedName("PW")private String PW;
    @Expose
    @SerializedName("response") private  String response;
    @Expose
    @SerializedName("verify") private  String verify;
    @Expose
    @SerializedName("credit") private String credit;
    @Expose
    @SerializedName("phoneNumber") private String phoneNumber;
    public String getNickname(){
        return nickname;
    }
    public String getPW(){
        return PW;
    }
    public String getE_mail(){
        return email;
    }
    public String getCredit(){
        return credit;
    }
    public String getResponse(){return response;}
    public String getVerify(){return verify;}
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public void setResponse(String response){this.response = response;}
    public void setE_mail(String E_mail){
        this.email = E_mail;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public void setPW(String PW){
        this.PW = PW;
    }
    public void setVerify(String verify){this.verify = verify;}

}
