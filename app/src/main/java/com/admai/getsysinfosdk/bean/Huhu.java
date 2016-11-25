package com.admai.getsysinfosdk.bean;

/**
 * Created by ZAN on 16/10/9.
 */
public class Huhu {
    
    private static final String TAG = Huhu.class.getSimpleName();
    
    public Huhu() {
        
    }

    public String huhuname;
    public int age;

    @Override
    public String toString() {
        return huhuname+"---"+age;
    }
}
