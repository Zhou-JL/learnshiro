package com.zhoujl.learnshiro;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LearnshiroApplicationTests {

    @Test
    void contextLoads() {


    }


    @Test
    public void shiropwd(){
        String pwd = "123456";
        //md5加密
        Md5Hash md5Hash = new Md5Hash(pwd);
        System.out.println("MD5加密："+md5Hash);
        //带盐的MD5加密
        Md5Hash md5HashSalt = new Md5Hash(pwd, "salt");         //本次选用这个
        System.out.println("MD5加盐加密："+md5HashSalt);
        //带盐的，带迭代次数的MD5加密
        Md5Hash md5Hash1 = new Md5Hash(pwd, "salt", 3);
        System.out.println("MD5加盐加迭代3次加密："+md5Hash1);
        //使用Md5Hash的父类SimpleHash实现加密
        SimpleHash simpleHash = new SimpleHash("MD5", pwd, "salt", 3);
        System.out.println("父类MD5加盐加迭代3次加密："+simpleHash);

    }

}
