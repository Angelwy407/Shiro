package com.shiro;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@SpringBootApplication
@MapperScan("com.shiro.mapper")
public class ShiroSpringbootApplication extends WebMvcConfigurationSupport {
    public static void main(String[] args) {
        SpringApplication.run(ShiroSpringbootApplication.class, args);
    }
    
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/css/");
        registry.addResourceHandler("/js/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/js/");
        registry.addResourceHandler("/json/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/json/");
        super.addResourceHandlers(registry);
    }
}
