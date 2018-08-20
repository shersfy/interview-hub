package org.interview.common;

import org.junit.Test;

public class AssertKeyword {
    
    @Test
    public void test01(){
        int a = 1;
        
        // assert exp, exp为Boolean表达式
        assert a==1;
        System.out.println(a);
    }
    
    @Test
    public void test02(){
        int a = 1;
        
        // assert exp, exp为Boolean表达式
        assert a==2;
        System.out.println(a);
    }

    @Test
    public void test03(){
        int a = 1;
        
        // assert exp:msg, exp为Boolean表达式, msg为异常信息
        assert a==2: "断言失败，a不等于2";
        System.out.println(a);
    }

}
