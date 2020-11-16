package com.lemon.cases;

import com.lemon.pojo.CaseInfo;
import com.lemon.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;


/**
 * @ProjectName: java-auto-api
 * @Author: wss
 * @create: 2020-10-28 14:38
 * @Desc: 注册接口测试类型
 */
public class RegisterCase extends BaseCase{


    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo){
//        1、参数化替换
        paramsReplace(caseInfo);
//        2、数据库前置查询结果(数据断言必须在接口执行前后都查询)
        Long beforeSQLResult = (Long)SQLUtils.getSingleResult(caseInfo.getSql());
//        3、调用接口
        String responseBody = HttpUtils.call(caseInfo, UserData.DEFAULT_HEADERS);
//        4、断言响应结果  断言，期望值和实际值匹配，匹配上了就是断言成功，相反断言失败。
        boolean responseAssertFlag = responseAssert(caseInfo.getExpectedResult(), responseBody);
//        5、添加接口响应回写内容
        addWriteBackData(sheetIndex,caseInfo.getId(), Constants.RESPONSE_CELL_NUM, responseBody);
//        6、数据库后置查询结果
        Long afterSQLResult = (Long)SQLUtils.getSingleResult(caseInfo.getSql());
//        7、数据库断言
        boolean sqlAssertFlag = sqlAssert(caseInfo.getSql(), beforeSQLResult, afterSQLResult);
//        8、添加断言回写内容
        String assertResult= responseAssertFlag ? Constants.ASSERT_SUCCESS : Constants.ASSERT_FAILED;
        addWriteBackData(sheetIndex,caseInfo.getId(), Constants.ASSERT_CELL_NUM,assertResult);
//        9、添加日志
//        10、报表断言
        Assert.assertEquals(assertResult,Constants.ASSERT_SUCCESS);
    }


    /**
     *
     * @param sql    数据库断言,因为每个接口的业务逻辑不一样，所以数据库断言方法无法抽取道父类中供其他子类使用。
     * @param beforeSQLResult
     * @param afterSQLResult
     * @return
     */
    public boolean sqlAssert(String sql, Long beforeSQLResult, Long afterSQLResult) {
        boolean flag = false;
        if (StringUtils.isNoneBlank(sql)){
            System.out.println("beforeSQLResult:" +beforeSQLResult);
            System.out.println("afterSQLResult:"+afterSQLResult);
            if (beforeSQLResult ==0 && afterSQLResult == 1 ){
                System.out.println("数据库断言成功");
                flag = true;
            }else{
                System.out.println("数据库断言失败");
            }
        }
        return flag;
    }

    @DataProvider
    public Object[] datas() throws Exception {
        List<CaseInfo> list = ExcelUtils.read(sheetIndex, 1, CaseInfo.class);
        return list.toArray();
    }
}
