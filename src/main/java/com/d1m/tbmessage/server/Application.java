package com.d1m.tbmessage.server;

import com.d1m.tbmessage.server.wechat.Wechat;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.d1m.tbmessage.server.database.dao")
public class Application {

    private static ApplicationContext applicationContext = null;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(Application.class);
        Wechat.start();
    }

    //get applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //get bean by name
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    //get bean by class
    public static <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

    //get bean by name & class
    public static <T> T getBean(String name, Class<T> clazz){
        return applicationContext.getBean(name, clazz);
    }
}
