package com.zhoujl.learnshiro.config.shiro;

import com.zhoujl.learnshiro.config.jwt.JWTFilter;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-17 9:30
 * @see: com.zhoujl.learnshiro.config
 * @Version: 1.0
 */
@Configuration
public class ShiroConfig {



    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager(UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        //创建加密对象
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();

        // 设置自定义 realm.
        securityManager.setRealm(userRealm);

        //设置rememberMe
        securityManager.setRememberMeManager(rememberMeManager());
        //设置缓存管理器
        securityManager.setCacheManager(getEhCacheManager());

        //关闭session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }

    /**
     * 先走 filter ，然后 filter 如果检测到请求头存在 token，则用 token 去 login，走 Realm 去验证
     */
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        // 添加自己的过滤器并且取名为jwt
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        //设置我们自定义的JWT过滤器
        filterMap.put("jwt", new JWTFilter());
        factoryBean.setFilters(filterMap);
        // 设置无权限时跳转的 url;
        factoryBean.setUnauthorizedUrl("/unauthorized/无权限");

        Map<String, String> filterRuleMap = new HashMap<>();
        // Swagger接口文档
        // filterChainDefinitionMap.put("/v2/api-docs", "anon");
        // filterChainDefinitionMap.put("/webjars/**", "anon");
        // filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        // filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        // filterChainDefinitionMap.put("/doc.html", "anon");
        // 公开接口
        // filterChainDefinitionMap.put("/api/**", "anon");
        //设置需要进行登录认证的拦截范围
        filterRuleMap.put("/**","authc");
        //设置不认证可以访问的资源
        filterRuleMap.put("/login", "anon");
        // 所有请求通过我们自己的JWT Filter
        filterRuleMap.put("/**", "jwt");
        // 访问 /unauthorized/** 不通过JWTFilter
        filterRuleMap.put("/unauthorized/**", "anon");   //用于重定向返回错误
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }

    /**
     * 添加注解支持，如果不加的话很有可能注解失效
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {

        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    /**
     * Cookie对象
     */
    private SimpleCookie rememberMeCookie() {
        //cookie的name名：rememberMe
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        // 记住时间，单位秒，默认3天
        cookie.setMaxAge(60 * 60 * 24 * 3);
        return cookie;
    }

    /**
     * 创建shiro的cookie管理对象;
     */
    private CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        // rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("BxOUmwApBB32ravaQc5Zng=="));
        return cookieRememberMeManager;
    }


    /**
     * shiro缓存管理器。需要添加到securityManager中
     *
     * @return cacheManager
     */
    @Bean
    public EhCacheManager getEhCacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:ehcache/ehcache-shiro.xml");
        return cacheManager;
    }



}