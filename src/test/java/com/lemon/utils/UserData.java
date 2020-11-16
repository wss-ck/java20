package com.lemon.utils;

import cn.binarywang.tools.generator.ChineseMobileNumberGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: java-auto-api
 * @Author: wss
 * @create: 2020-10-29 13:46
 * @Desc:
 */
public class UserData {
    //存储接口响应变量
    public static Map<String,Object> VARS = new HashMap<>();

    //储存默认请求头
    public static Map<String,String> DEFAULT_HEADERS = new HashMap<>();

    static {
        //静态代码块：类在加载时自动加载一次本代码。
        //静态方法需要手动调用，静态代码块类加载自动执行一次。
        DEFAULT_HEADERS.put("X-Lemonban-Media-Type","lemonban.v2");
        DEFAULT_HEADERS.put("Content-Type","application/json");

        //把需要参数化的数据存储到VARS中
        //随机手机号码
        VARS.put("${register_mb}", ChineseMobileNumberGenerator.getInstance().generate());
        VARS.put("${register_pwd}","12345678");
//        VARS.put("${login_mb}", "从数据库查一个知道密码的账号");
//        VARS.put("${login_pwd}", "从数据库查一个知道密码的账号");
        VARS.put("${amount}","5000");
    }



}
