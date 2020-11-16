package com.lemon.utils;

import com.alibaba.fastjson.JSONObject;
import com.lemon.pojo.CaseInfo;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @ProjectName: java-auto-api
 * @Author: wss
 * @create: 2020-10-28 11:40
 * @Desc: HttpClient工具类
 */
public class HttpUtils {

    private static Logger logger = Logger.getLogger(HttpUtils.class);

    /**
     * http请求方法
     * @param caseInfo     请求参数
     * @param headers      请求头
     * @return
     */
    public static String call(CaseInfo caseInfo, Map<String, String> headers){
        String responseBody = "";
        try {
            String params = caseInfo.getParams();
            String url = caseInfo.getUrl();
            String method = caseInfo.getMethod();
            //2、判断请求方式，如果是post
            if ("post".equalsIgnoreCase(method)){
                String contentType = caseInfo.getContentType();
                //2.1、判断参数类型，如果是json
                if ("json".equalsIgnoreCase(contentType)){
                    //2.2、判断参数类型，如果是form
                }else if ("form".equalsIgnoreCase(contentType)){
                    //json参数转成key=value参数
                    params = jsonStr2KeyValueStr(params);
                    logger.info("formParams:" +params);
                    //覆盖默认请求头中的Content-Type
                    headers.put("Content-Type","application/x-www-form-urlencoded");
                }
                responseBody = HttpUtils.post(url,params,headers);
                //3、判断请求方式，如果是get
            }else if ("get".equalsIgnoreCase(method)){
                responseBody = HttpUtils.get(url,headers);
                //4、判断请求方式，如果是patch
            }else if ("patch".equalsIgnoreCase(method)){
                responseBody = HttpUtils.patch(url,params,headers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    /**
     * json字符串转成key=value
     * 例如：{"mobilephone":"13877788811","pwd":"12345678"} = > mobilephone=13877788811&pwd=12345678
     * @param json      JSON字符串
     * @return
     */
    public static String jsonStr2KeyValueStr(String json) {
        Map<String ,String> map = JSONObject.parseObject(json, Map.class);
        Set<String> keySet = map.keySet();
        String formParams = "";
        for (String key : keySet){
            //key=value&key=value&key=value&
            formParams += key + "=" +map.get(key) + "&";
        }
        return formParams.substring(0,formParams.length()-1);
    }

    /**
     * 发送一个get请求
     * @param url  携带参数的url
     *      *                  例如：http://api.lemonban.com/futureloan/loans?pageIndex=1&pageSize=1
     *      *                  例如：http://api.lemonban.com/futureloan/member/${member_id}/info
     * @throws Exception
     */
    public static String get(String url,Map<String,String> headers) throws Exception {
        //1、创建请求
        HttpGet get = new HttpGet(url);
        //2、添加请求头
//        get.setHeader("X-Lemonban-Media-Type","lemonban.v1");
        setHeaders(headers, get);
        //3、创建一个客户端
        //createDefault：是一个静态方法，不需要创建对象，创建一个HttpClient客户端。
        HttpClient client = HttpClients.createDefault();
        //4、发送请求，获取响应对象
        //execute(HttpUriRequest):多态的方法，接受HttpUriRequest所有子实现类。
        HttpResponse response = client.execute(get);
        //5、格式化响应对象 response = 响应状态码 + 响应头 + 响应体
        return printResponse(response);
    }

    /**
     * 发送一个post请求
     * @param url      接口地址
     * @param params   接口参数
     * @throws Exception
     */
    public static String post(String url, String params, Map<String,String> headers) throws Exception {
        //1、创建请求
        HttpPost post = new HttpPost(url);
        //2、添加请求头
   //     post.setHeader("X-Lemonban-Media-Type","lemonban.v1");
   //     post.setHeader("Content-Type","application/json");
        setHeaders(headers, post);
        //3、添加请求体（参数）
        StringEntity body = new StringEntity(params,"utf-8");
        post.setEntity(body);
        //4、创建客户端
        //createDefault：是一个静态方法，不需要创建对象，创建一个HttpClient客户端。
        HttpClient client = HttpClients.createDefault();
        //5、发送请求，获取响应对象
        //execute(HttpUriRequest):多态的方法，接受HttpUriRequest所有子实现类。
        HttpResponse response = client.execute(post);
        //6、格式化响应对象 response = 响应状态码 + 响应头 + 响应体
        return printResponse(response);
    }



    /**
     * 发送一个patch请求
     * @param url       接口地址
     * @param params    接口参数
     * @throws Exception
     */
    public static String patch(String url, String params,Map<String,String> headers) throws Exception {
        //1、创建请求
        HttpPatch patch = new HttpPatch(url);
        //2、添加请求头
//        patch.setHeader("X-Lemonban-Media-Type","lemonban.v1");
//        patch.setHeader("Content-Type","application/json");
        setHeaders(headers, patch);
        //3、添加请求体（参数）
        StringEntity body = new StringEntity(params,"utf-8");
        patch.setEntity(body);
        //4、创建客户端
        //createDefault：是一个静态方法，不需要创建对象，创建一个HttpClient客户端。
        HttpClient client = HttpClients.createDefault();
        //5、发送请求，获取响应对象
        //execute(HttpUriRequest):多态的方法，接受HttpUriRequest所有子实现类。
        HttpResponse response = client.execute(patch);
        //6、格式化响应对象 response = 响应状态码 + 响应头 + 响应体
        return printResponse(response);
    }

    /**
     * 打印响应
     * @param response    响应对象
     * @return
     * @throws Exception
     */
    public static String printResponse(HttpResponse response) throws Exception {
        //1、响应状态码  链式编程
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);
        //2、响应头
        Header[] allHeaders = response.getAllHeaders();
        System.out.println(Arrays.toString(allHeaders));
        //3、响应体
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        logger.info(body);
        logger.info("======================");
        return body;
    }

    /**
     * 设置请求头
     * @param headers         包含了请求头的Map集合
     * @param request         请求对象
     */
    public static void setHeaders(Map<String, String> headers, HttpRequest request) {
        //获取所有请求头name
        Set<String> headerNames = headers.keySet();
        //遍历所有的请求头name
        for (String headerName : headerNames){
            //获取请求头name对应的value
            String headerValue = headers.get(headerName);
            //设置请求头name，value
            request.setHeader(headerName,headerValue);
        }
    }
}
