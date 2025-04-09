package com.shiro.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.shiro.realm.MyRealm;
import net.sf.ehcache.CacheManager;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class ShiroConfig {
    @Autowired
    private MyRealm myRealm;
    //配置SecurityManager
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(){
        //1. 创建defaultWebSecurityManager对象
        DefaultWebSecurityManager defaultWebSecurityManager=new DefaultWebSecurityManager();
        //2. 创建加密对象设置相关属性
        HashedCredentialsMatcher matcher=new HashedCredentialsMatcher();
        //2.1 采用MD5加密
        matcher.setHashAlgorithmName("md5");
        //2.2 采用的迭代加密的次数
        matcher.setHashIterations(3);
        //3. 将加密对象存储到myRealm当中
        myRealm.setCredentialsMatcher(matcher);
        //4. 将myRealm存入defaultWebSecurityManager对象
        defaultWebSecurityManager.setRealm(myRealm);
        //4.5 设置rememberMe
        defaultWebSecurityManager.setRememberMeManager(rememberMeManager());
        //4.6设置缓存管理器
        defaultWebSecurityManager.setCacheManager(getEhCacheManager());
        //5. 返回
        return defaultWebSecurityManager;
            }
//    缓存管理器
    public EhCacheManager getEhCacheManager(){
        EhCacheManager ehCacheManager=new EhCacheManager();
        InputStream is=null;
        try {
            is = ResourceUtils.getInputStreamForPath("classpath:ehcache/ehcache-shiro.xml");
        } catch (IOException e){
            e.printStackTrace();
        }
        CacheManager cacheManager=new CacheManager(is);
        ehCacheManager.setCacheManager(cacheManager);
        return ehCacheManager;
    }


    //配置Shiro内置的过滤器拦截范围
    @Bean
    public DefaultShiroFilterChainDefinition shiroFilterChainDefinition(){
        DefaultShiroFilterChainDefinition definition=new DefaultShiroFilterChainDefinition();
        //设置不认证可以访问的资源
        definition.addPathDefinition("/myController/userLogin","anon");
        definition.addPathDefinition("/myController/login","anon");
        definition.addPathDefinition("/myController/register","anon");
        definition.addPathDefinition("/myController/userRegister","anon");
        //去除对css和js的验证!!!
        definition.addPathDefinition("/css/**", "anon");
        definition.addPathDefinition("/js/**", "anon");
        definition.addPathDefinition("/json/**", "anon");
        //配置登出的过滤器
        definition.addPathDefinition("/logout","logout");
        //设置需要进行登录认证的拦截范围
        definition.addPathDefinition("/**","authc");
        // map.put("/static/**", "anon");无效

        //添加存在用户的过滤器（rememberMe）
        definition.addPathDefinition("/**","user");
        return definition;
    }
    //cookie属性设置
    public SimpleCookie rememberMeCookie(){
        SimpleCookie cookie=new SimpleCookie("rememberMe");
        //设置跨域
        //cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(30*24*60*60);
        return cookie;
    }
    //创建Shiro的cookie管理对象
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager=new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        cookieRememberMeManager.setCipherKey("1234567890987654".getBytes());
        return cookieRememberMeManager;
    }
    @Bean
    public ShiroDialect shiroDialect(){
        return new ShiroDialect();
    }
}
