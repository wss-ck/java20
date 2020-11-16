package com.lemon.cases;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.lemon.pojo.CaseInfo;
import com.lemon.pojo.WriteBackData;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.UserData;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ProjectName: java-auto-api
 * @Author: wss
 * @create: 2020-10-30 14:48
 * @Desc:
 */
public class BaseCase {

    private static Logger logger = Logger.getLogger(BaseCase.class);

    //获取testng.xml中 sheetIndex
    public int sheetIndex;
    @BeforeClass
    @Parameters("sheetIndex")
    public void beforeClass(int sheetIndex){
        this.sheetIndex = sheetIndex;
    }

    @AfterSuite
    public void finish() throws Exception {
        ExcelUtils.batchWriter();
    }


    /**
     * 从responseBody 通过jsonPath取出对应参数，存入到UserData.VARS中
     * @param responseBody            接口响应json字符串
     * @param jsonPathExpression      jsonPath表达式
     * @param userDataKey             VARS中key
     */
    public void getParamsInUserData(String responseBody,String jsonPathExpression,String userDataKey) {
        Object userDataValue = JSONPath.read(responseBody, jsonPathExpression);
        System.out.println("userDataKey:" + userDataKey);
        System.out.println("userDataValue:" + userDataValue);
        //存储到Vars中
        if (userDataValue != null){
            UserData.VARS.put(userDataKey,userDataValue);
        }
    }

    /**
     * 获取鉴权头，加入默认请求头，并且返回。
     * @return
     */
    public Map<String, String> getAuthorizationHeader() {
        //从用户变量中取出登录接口存入的token值。
        Object token = UserData.VARS.get("${token}");
        HashMap<String,String> headers = new HashMap<>();
        //添加鉴权头
        headers.put("Authorization","Bearer " + token);
        //添加默认头
        headers.putAll(UserData.DEFAULT_HEADERS);
        return headers;
    }




    /**
     * 添加回写对象到回写集合中
     * @param sheetIndex    sheetIndex
     * @param rowNum        行号
     * @param cellNum       列号
     * @param content       回写内容
     */
    public void addWriteBackData(int sheetIndex,int rowNum,int cellNum,String content) {
        //响应回写
        WriteBackData wdb = new WriteBackData(sheetIndex,rowNum,cellNum,content);
        //批量回写，存储到一个List
        ExcelUtils.wbdList.add(wdb);
    }

    /**
     * 接口响应断言
     * @param expectedResult     断言的期望值
     * @param responseBody       接口响应内容
     * @return                   接口响应断言结果
     */
    public boolean responseAssert(String expectedResult, String responseBody) {
        //{"获取实际值的表达式":期望值,"$.msg":"OK"}
        //expectedResult转成Map
        Map<String ,Object> map = JSONObject.parseObject(expectedResult, Map.class);
        //遍历map
        Set<String> keySet = map.keySet();
        boolean responseAssertFlag = true;
        for (String actualValueExpression : keySet){
            //获取了期望值
            Object expectedValue = map.get(actualValueExpression);
            //通过实际值表达式从响应体获取实际值
            Object actualValue = JSONPath.read(responseBody, actualValueExpression);
            //断言，只要失败一次，整个断言就失败。
            if (!expectedValue.equals(actualValue)){
                //断言失败
                responseAssertFlag = false;
                break;
            }
        }
        logger.info("接口响应断言结果：" +responseAssertFlag);
        return responseAssertFlag;
    }

    /**
     * 参数化替换
     * @param caseInfo
     */
    @Step("参数化")
    public void paramsReplace(CaseInfo caseInfo) {
        //现在只有占位符，没有真实的手机号码和密码
        //sql：select count(*) from member a where a.mobile_phone = '${register_mb}';
        //${register_mb}=13212312311 ${register_pwd}=12345678
        //params：{"mobile_phone":"${register_mb}","pwd":"${register_pwd}"}
        String params = caseInfo.getParams();
        String sql = caseInfo.getSql();
        String expectedResult = caseInfo.getExpectedResult();
        String url = caseInfo.getUrl();
        Set<String> keySet = UserData.VARS.keySet();
        for (String placeHolder : keySet) {
            String value = UserData.VARS.get(placeHolder).toString();
            if (StringUtils.isNotBlank(params)){
                params = params.replace(placeHolder, value);
            }
            if (StringUtils.isNotBlank(sql)){
                sql = sql.replace(placeHolder,value);
            }
            if (StringUtils.isNotBlank(expectedResult)){
                expectedResult = expectedResult.replace(placeHolder,value);
            }
            if (StringUtils.isNotBlank(url)){
                url = url.replace(placeHolder,value);
            }
        }
        caseInfo.setParams(params);
        caseInfo.setSql(sql);
        caseInfo.setExpectedResult(expectedResult);
        caseInfo.setUrl(url);
        logger.info(caseInfo);
    }

}
