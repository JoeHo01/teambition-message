package com.d1m.tbmessage.http;

import com.d1m.tbmessage.wechat.Wechat;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.d1m.tbmessage.http.mvc.dao")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
        new Wechat().start();
    }
}
