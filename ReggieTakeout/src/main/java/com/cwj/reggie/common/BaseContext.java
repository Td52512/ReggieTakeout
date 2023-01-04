package com.cwj.reggie.common;

//基于ThreadLocal封装工具类 用来保存和获取当前登陆id
public class BaseContext {
    private BaseContext() {}

    private static ThreadLocal<Long> local = new ThreadLocal<>();

    public static void set(Long id){
        local.set(id);
    }

    public static Long get(){
        return local.get();
    }

}
