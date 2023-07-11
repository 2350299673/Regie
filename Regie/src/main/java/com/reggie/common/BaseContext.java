package com.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户的ID
 * 已线程为作用域，每一个线程单独保存自己的一个副本
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    //通过线程存id
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    //通过线程取id
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
